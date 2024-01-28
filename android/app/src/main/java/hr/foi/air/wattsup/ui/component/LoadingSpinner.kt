package hr.foi.air.wattsup.ui.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier,
    )
}
