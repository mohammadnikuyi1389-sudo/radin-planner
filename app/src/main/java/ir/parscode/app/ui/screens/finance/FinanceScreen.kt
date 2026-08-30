package ir.parscode.app.ui.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.data.local.entity.FinanceRecordEntity
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinanceUiState(val records: List<FinanceRecordEntity> = emptyList(), val income: Long = 0, val expense: Long = 0)

class FinanceViewModel : ViewModel() {
    private val dao = ServiceLocator.financeDao
    val state: StateFlow<FinanceUiState> = dao.observeAll().map { list ->
        FinanceUiState(
            records = list,
            income = list.filter { it.type == "INCOME" }.sumOf { it.amount },
            expense = list.filter { it.type == "EXPENSE" }.sumOf { it.amount },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceUiState())

    fun add(type: String, amount: Long, category: String) {
        if (amount <= 0) return
        viewModelScope.launch { dao.insert(FinanceRecordEntity(type = type, amount = amount, category = category, dateIso = DateUtils.todayIso())) }
    }
    fun edit(record: FinanceRecordEntity, type: String, amount: Long, category: String) {
        if (amount <= 0) return
        viewModelScope.launch { dao.update(record.copy(type = type, amount = amount, category = category)) }
    }
    fun delete(r: FinanceRecordEntity) { viewModelScope.launch { dao.delete(r) } }
}
fun financeViewModelFactory() = viewModelFactory { initializer { FinanceViewModel() } }

@Composable
fun FinanceScreen(viewModel: FinanceViewModel = viewModel(factory = financeViewModelFactory())) {
    val s by viewModel.state.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<FinanceRecordEntity?>(null) }
    val balance = s.income - s.expense

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("مالی", style = Typography.headlineMedium, color = PcGold) }
        item {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Text("موجودی کل: ${DateUtils.toPersianDigits(balance.toString())} تومان", style = Typography.titleLarge, color = PcGold)
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("درآمد: ${DateUtils.toPersianDigits(s.income.toString())}", color = PcSuccess)
                    Text("هزینه: ${DateUtils.toPersianDigits(s.expense.toString())}", color = PcDanger)
                }
            }
        }
        item { GoldButton(text = "ثبت درآمد / هزینه", onClick = { showAdd = true }) }
        if (s.records.isEmpty()) {
            item { GlowCard(modifier = Modifier.fillMaxWidth()) { Text("هنوز تراکنشی ثبت نشده.", color = PcTextSecondary) } }
        } else {
            items(s.records, key = { it.id }) { r ->
                GlowCard(modifier = Modifier.fillMaxWidth().clickable { editing = r }) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(r.category, color = PcTextPrimary)
                            Text(DateUtils.formatJalaliLong(r.dateIso), style = Typography.bodySmall, color = PcTextSecondary)
                        }
                        Text(
                            "${if (r.type == "INCOME") "+" else "-"}${DateUtils.toPersianDigits(r.amount.toString())}",
                            color = if (r.type == "INCOME") PcSuccess else PcDanger,
                        )
                    }
                    var showConfirm by remember { mutableStateOf(false) }
                    TextButton(onClick = { showConfirm = true }) { Text("حذف", color = PcDanger) }
                    if (showConfirm) {
                        AlertDialog(
                            onDismissRequest = { showConfirm = false },
                            containerColor = PcSurface,
                            title = { Text("حذف این تراکنش؟") },
                            confirmButton = { TextButton(onClick = { viewModel.delete(r); showConfirm = false }) { Text("حذف", color = PcDanger) } },
                            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("انصراف") } },
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        FinanceFormDialog(title = "ثبت تراکنش", confirmLabel = "ثبت", onDismiss = { showAdd = false }) { type, amount, cat ->
            viewModel.add(type, amount, cat); showAdd = false
        }
    }
    editing?.let { r ->
        FinanceFormDialog(
            title = "ویرایش تراکنش", confirmLabel = "ذخیره",
            initialType = r.type, initialAmount = r.amount.toString(), initialCategory = r.category,
            onDismiss = { editing = null },
        ) { type, amount, cat -> viewModel.edit(r, type, amount, cat); editing = null }
    }
}

@Composable
private fun FinanceFormDialog(
    title: String,
    confirmLabel: String,
    initialType: String = "EXPENSE",
    initialAmount: String = "",
    initialCategory: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, Long, String) -> Unit,
) {
    var type by remember { mutableStateOf(initialType) }
    var amount by remember { mutableStateOf(initialAmount) }
    var category by remember { mutableStateOf(initialCategory) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text(title) },
        text = {
            Column {
                Row {
                    FilterChip(selected = type == "INCOME", onClick = { type = "INCOME" }, label = { Text("درآمد") })
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(selected = type == "EXPENSE", onClick = { type = "EXPENSE" }, label = { Text("هزینه") })
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("مبلغ (تومان)") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("دسته") })
            }
        },
        confirmButton = {
            val validAmount = amount.toLongOrNull()
            TextButton(
                onClick = { onConfirm(type, validAmount!!, category.ifBlank { "سایر" }) },
                enabled = validAmount != null && validAmount > 0,
            ) { Text(confirmLabel, color = PcGold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}
