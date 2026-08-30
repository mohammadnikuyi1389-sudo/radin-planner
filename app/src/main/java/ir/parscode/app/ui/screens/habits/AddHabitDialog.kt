package ir.parscode.app.ui.screens.habits

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
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, target: String?) -> Unit,
    initialTitle: String = "",
    initialTarget: String = "",
    dialogTitle: String = "افزودن عادت جدید",
    confirmLabel: String = "افزودن",
) {
    var title by remember { mutableStateOf(initialTitle) }
    var target by remember { mutableStateOf(initialTarget) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان عادت") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("هدف روزانه (اختیاری)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title, target.ifBlank { null }) }, enabled = title.isNotBlank()) {
                Text(confirmLabel, color = PcGold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}
