package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.utils.UserCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val authService = NetworkService.authService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onRegisterClick: () -> Unit, onLogin: () -> Unit, onArrowBackClick: () -> Unit) {
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
        LoginView(onRegisterClick, onLogin, context)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(onRegisterClick: () -> Unit, onLogin: () -> Unit, context: Context) {
    val interactionSource = remember { MutableInteractionSource() }
    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    val showLoading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val modifier = Modifier.padding(0.dp, 15.dp)

        OutlinedTextField(
            modifier = modifier,
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.emailLabel)) },
            singleLine = true,
        )

        OutlinedTextField(
            modifier = modifier,
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.passwordLabel)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        ElevatedButton(
            onClick = {
                showLoading.value = true

                authService.loginUser(
                    LoginBody(email, password),
                ).enqueue(
                    object : Callback<LoginResponseBody> {
                        override fun onResponse(
                            call: Call<LoginResponseBody>?,
                            response: Response<LoginResponseBody>?,
                        ) {
                            showLoading.value = false
                            Log.i("RES", response.toString())
                            if (response?.isSuccessful == true) {
                                val responseBody = response.body()
                                val tokenManager = TokenManager.getInstance(context)
                                tokenManager.setrefreshToken(responseBody!!.refreshToken)
                                tokenManager.setrefreshTokenExpiresAt(responseBody.refreshToken)
                                tokenManager.setjWTtoken(responseBody.jwt)
                                Log.i("Response", responseBody.jwt)
                                onLogin()
                            } else {
                                toast(context, "Invalid username or password")
                            }
                        }

                        override fun onFailure(call: Call<LoginResponseBody>?, t: Throwable?) {
                            showLoading.value = false
                            Log.i("Response", t.toString())
                            toast(context, "Failed to login user")
                        }
                    },
                )
            },
            modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp),
            contentPadding = PaddingValues(122.dp, 0.dp),
            interactionSource = interactionSource,
            enabled = !showLoading.value,
        ) {
            if (showLoading.value) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .height(25.dp)
                        .width(25.dp)
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White,
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

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

/*@Preview(showBackground = false)
@Composable
fun LoginPreview() {
    LoginScreen {}
}*/
