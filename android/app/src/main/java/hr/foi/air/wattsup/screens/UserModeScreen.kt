@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.LogoutDialog
import hr.foi.air.wattsup.ui.component.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserModeScreen(
    onHistoryClick: () -> Unit,
    onCardsClick: () -> Unit,
    onArrowBackClick: () -> Unit,
) {
    val showLogoutDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Mode") },
                navigationIcon = {
                    IconButton(onClick = onArrowBackClick) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actionIcon = {
                    IconButton(onClick = { showLogoutDialog.value = true }) {
                        Icon(Icons.Filled.ExitToApp, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        val modifier = Modifier.padding(it)

        UserModeView(onHistoryClick, onCardsClick, onArrowBackClick, showLogoutDialog, modifier)
    }
}

@Composable
fun UserModeView(
    onHistoryClick: () -> Unit,
    onCardsClick: () -> Unit,
    onArrowBackClick: () -> Unit,
    showLogoutDialog: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    LogoutDialog(showLogoutDialog, onArrowBackClick)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            // .padding(0.dp, 15.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedButton(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = onHistoryClick,
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedButton(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = onCardsClick,
        ) {
            Text(
                text = stringResource(R.string.cards),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun UserModePreview() {
    UserModeScreen({}, {}, {})
}
