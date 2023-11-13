package hr.foi.air.wattsup.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    showDismissButton: Boolean = true,
    confirmButtonText: String = "Confirm",
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        iconContentColor = Color.Gray, // MaterialTheme.colorScheme.primary,
        icon = {
            Icon(icon, contentDescription = "Alert Dialog Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            if (showDismissButton) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text("Dismiss")
                }
            }
        },
    )
}
