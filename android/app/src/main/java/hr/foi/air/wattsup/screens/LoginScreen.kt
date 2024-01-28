package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.LoadingSpinner
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.viewmodels.AuthenticationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLogin: () -> Unit,
    onArrowBackClick: () -> Unit,
    viewModel: AuthenticationViewModel,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        val modifier = Modifier.padding(it)
        LoginView(onRegisterClick, onLogin, context, viewModel, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    onRegisterClick: () -> Unit,
    onLogin: () -> Unit,
    context: Context,
    viewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier,
) {
    val interactionSource by viewModel.interactionSource.observeAsState()
    val email by viewModel.email.observeAsState()
    val password by viewModel.password.observeAsState()
    val showLoading by viewModel.showLoading.observeAsState()
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val modifier = Modifier.padding(0.dp, 15.dp)

        OutlinedTextField(
            modifier = modifier,
            value = email!!,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text(stringResource(R.string.emailLabel)) },
            singleLine = true,

        )

        OutlinedTextField(
            modifier = modifier,
            value = password!!,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text(stringResource(R.string.passwordLabel)) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(imageVector = image, description)
                }
            },

        )

        ElevatedButton(
            onClick = {
                if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    viewModel.showToast(context, "Fill all required fields")
                } else {
                    viewModel.loginUser(email!!, password!!, context, onLogin)
                }
            },
            modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp),
            contentPadding = PaddingValues(122.dp, 0.dp),
            interactionSource = interactionSource!!,
            enabled = !showLoading!!,
        ) {
            if (showLoading == true) {
                LoadingSpinner(
                    Modifier
                        .height(25.dp)
                        .width(25.dp)
                        .wrapContentSize(Alignment.Center),
                )
            } else {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.LoginDontHaveAccountLabel),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.width(2.dp))

            TextButton(
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(0.dp),
                onClick = onRegisterClick,
            ) {
                Text(
                    stringResource(R.string.registerLabel),
                )
            }
        }
    }
}
