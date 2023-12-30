package hr.foi.air.wattsup.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSpinner() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .height(25.dp)
            .width(25.dp)
            .wrapContentSize(Alignment.Center),
    )
}
