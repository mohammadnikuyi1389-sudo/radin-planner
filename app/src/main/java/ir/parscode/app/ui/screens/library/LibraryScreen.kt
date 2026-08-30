package ir.parscode.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.data.local.entity.LibraryItemEntity
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val dao = ServiceLocator.libraryDao
    val items: StateFlow<List<LibraryItemEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun add(title: String, author: String?, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch { dao.upsert(LibraryItemEntity(title = title.trim(), author = author?.ifBlank { null }, category = category)) }
    }
    fun edit(item: LibraryItemEntity, title: String, author: String?, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch { dao.upsert(item.copy(title = title.trim(), author = author?.ifBlank { null }, category = category)) }
    }
    fun delete(item: LibraryItemEntity) { viewModelScope.launch { dao.delete(item) } }
    fun toggleFavorite(item: LibraryItemEntity) { viewModelScope.launch { dao.upsert(item.copy(isFavorite = !item.isFavorite)) } }
    fun markViewed(item: LibraryItemEntity) { viewModelScope.launch { dao.upsert(item.copy(lastViewedIso = DateUtils.todayIso())) } }
    fun deleteMany(list: List<LibraryItemEntity>) { viewModelScope.launch { dao.deleteAll(list) } }
    fun moveUp(list: List<LibraryItemEntity>, index: Int) = swap(list, index, index - 1)
    fun moveDown(list: List<LibraryItemEntity>, index: Int) = swap(list, index, index + 1)
    private fun swap(list: List<LibraryItemEntity>, a: Int, b: Int) {
        if (a !in list.indices || b !in list.indices) return
        val reordered = list.toMutableList().apply { val tmp = this[a]; this[a] = this[b]; this[b] = tmp }
        val withOrder = reordered.mapIndexed { i, item -> item.copy(sortOrder = i) }
        viewModelScope.launch { dao.updateAll(withOrder) }
    }
}
fun libraryViewModelFactory() = viewModelFactory { initializer { LibraryViewModel() } }

private val CATEGORIES = listOf("همه", "آموزشی", "کسب‌وکار", "توسعه فردی", "سلامت")

@Composable
fun LibraryScreen(viewModel: LibraryViewModel = viewModel(factory = libraryViewModelFactory())) {
    val items by viewModel.items.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<LibraryItemEntity?>(null) }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("همه") }

    val filtered = items.filter { item ->
        (selectedCategory == "همه" || item.category == selectedCategory) &&
            (query.isBlank() || item.title.contains(query, ignoreCase = true) || (item.author ?: "").contains(query, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("کتابخانه", style = Typography.headlineMedium, color = PcGold) }
        item { GoldButton(text = "افزودن منبع جدید", onClick = { showAdd = true }) }
        val favorites = items.filter { it.isFavorite }
        val recent = items.filter { it.lastViewedIso != null }.sortedByDescending { it.lastViewedIso }.take(10)
        if (favorites.isNotEmpty()) {
            item {
                Text("موارد مورد علاقه", style = Typography.titleMedium, color = PcGold)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(favorites, key = { "fav_${it.id}" }) { fav ->
                        MiniCard(fav) { viewModel.markViewed(fav) }
                    }
                }
            }
        }
        if (recent.isNotEmpty()) {
            item {
                Text("آخرین مشاهده‌ها", style = Typography.titleMedium, color = PcGold)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(recent, key = { "recent_${it.id}" }) { r ->
                        MiniCard(r) { viewModel.markViewed(r) }
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("جست‌وجو کتاب، فایل یا منبع...") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                CATEGORIES.forEach { cat ->
                    val count = if (cat == "همه") items.size else items.count { it.category == cat }
                    FilterChip(
                        selected = cat == selectedCategory,
                        onClick = { selectedCategory = cat },
                        label = { Text("$cat (${DateUtils.toPersianDigits(count)})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PcGold, selectedLabelColor = PcBackground, containerColor = PcSurfaceRaised),
                    )
                }
            }
        }
        if (filtered.isEmpty()) {
            item { GlowCard(modifier = Modifier.fillMaxWidth()) { Text("منبعی با این مشخصات یافت نشد.", color = PcTextSecondary) } }
        } else {
            itemsIndexed(filtered) { idx, it2 ->
                GlowCard(modifier = Modifier.fillMaxWidth().clickable { viewModel.markViewed(it2) }) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        IconButton(onClick = { viewModel.toggleFavorite(it2) }) {
                            Icon(
                                if (it2.isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null, tint = PcGold,
                            )
                        }
                        if (selectedCategory == "همه" && query.isBlank()) {
                            Column {
                                IconButton(onClick = { viewModel.moveUp(items, idx) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "بالا", tint = PcTextSecondary)
                                }
                                IconButton(onClick = { viewModel.moveDown(items, idx) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "پایین", tint = PcTextSecondary)
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            Text(it2.title, style = Typography.titleMedium, color = PcTextPrimary)
                            if (it2.author != null) Text(it2.author, style = Typography.bodySmall, color = PcTextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { it2.progressPercent / 100f },
                                modifier = Modifier.fillMaxWidth(),
                                color = PcGold, trackColor = PcSurfaceRaised,
                            )
                            Text("${DateUtils.toPersianDigits(it2.progressPercent)}٪ مطالعه", style = Typography.bodySmall, color = PcGoldMuted)
                        }
                    }
                    TextButton(onClick = { editingItem = it2 }) { Text("ویرایش", color = PcGold) }
                    TextButton(onClick = { viewModel.delete(it2) }) { Text("حذف", color = PcDanger) }
                }
            }
        }
    }

    if (showAdd) {
        LibraryItemDialog(title = "افزودن منبع", confirmLabel = "افزودن", onDismiss = { showAdd = false }) { t, a, c ->
            viewModel.add(t, a, c); showAdd = false
        }
    }
    editingItem?.let { item ->
        LibraryItemDialog(
            title = "ویرایش منبع", confirmLabel = "ذخیره",
            initialTitle = item.title, initialAuthor = item.author ?: "", initialCategory = item.category,
            onDismiss = { editingItem = null },
        ) { t, a, c ->
            viewModel.edit(item, t, a, c); editingItem = null
        }
    }
}

@Composable
private fun LibraryItemDialog(
    title: String,
    confirmLabel: String,
    initialTitle: String = "",
    initialAuthor: String = "",
    initialCategory: String = "آموزشی",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
) {
    var t by remember { mutableStateOf(initialTitle) }
    var author by remember { mutableStateOf(initialAuthor) }
    var category by remember { mutableStateOf(initialCategory) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("عنوان") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("نویسنده (اختیاری)") })
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CATEGORIES.drop(1).forEach { cat ->
                        FilterChip(
                            selected = cat == category, onClick = { category = cat }, label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PcGold, selectedLabelColor = PcBackground, containerColor = PcSurfaceRaised),
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(t, author, category) }, enabled = t.isNotBlank()) { Text(confirmLabel, color = PcGold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@Composable
private fun MiniCard(item: LibraryItemEntity, onClick: () -> Unit) {
    GlowCard(modifier = Modifier.width(140.dp).clickable(onClick = onClick)) {
        Text(item.title, style = Typography.bodyMedium, color = PcTextPrimary, maxLines = 2)
        if (item.author != null) Text(item.author, style = Typography.bodySmall, color = PcTextSecondary, maxLines = 1)
        Text("${DateUtils.toPersianDigits(item.progressPercent)}٪", style = Typography.bodySmall, color = PcGoldMuted)
    }
}
