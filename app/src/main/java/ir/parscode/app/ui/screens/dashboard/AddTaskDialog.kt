package ir.parscode.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.theme.PcGold
import ir.parscode.app.ui.theme.PcSurface

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, time: String?) -> Unit,
    initialTitle: String = "",
    initialTime: String = "",
    dialogTitle: String = "افزودن وظیفه جدید",
    confirmLabel: String = "افزودن",
) {
    var title by remember { mutableStateOf(initialTitle) }
    var time by remember { mutableStateOf(initialTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان وظیفه") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("ساعت (اختیاری، مثلا ۰۸:۰۰)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title, time.ifBlank { null }) }, enabled = title.isNotBlank()) {
                Text(confirmLabel, color = PcGold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}
