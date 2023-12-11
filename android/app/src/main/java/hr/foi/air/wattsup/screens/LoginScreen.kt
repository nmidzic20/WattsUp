package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.TokenManager
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
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
fun LoginScreen(onRegisterClick: () -> Unit, onLogin: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
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
    var statusMessage: String by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp, 15.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 200.dp, 0.dp, 40.dp),
            style = MaterialTheme.typography.headlineLarge,
            text = stringResource(R.string.login),
        )
        val modifier = Modifier.padding(0.dp, 15.dp)

        OutlinedTextField(
            modifier = modifier,
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.emailLabel)) },
        )

        OutlinedTextField(
            modifier = modifier,
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.passwordLabel)) },
        )
        if(showToast){
            Text(
                text = statusMessage
            )
        }
        ElevatedButton(
            onClick = {
                authService.loginUser(
                    LoginBody(email, password),
                ).enqueue(
                    object : Callback<LoginResponseBody> {
                        override fun onResponse(
                            call: Call<LoginResponseBody>?,
                            response: Response<LoginResponseBody>?,
                        ) {
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
                                val responseBody = response!!.body()
                                statusMessage = "Invalid username or password"
                                showToast = true
                                coroutineScope.launch {
                                    delay(2000)
                                    showToast = false
                                }
                                Log.i("Response", statusMessage)
                            }
                        }

                        override fun onFailure(call: Call<LoginResponseBody>?, t: Throwable?) {
                            statusMessage = "Failed to login user"
                            Log.i("Response", statusMessage)
                            Log.i("Response", t.toString())
                            showToast = true
                            coroutineScope.launch {
                                delay(2000)
                                showToast = false
                            }
                        }
                    },
                )
            },
            modifier = Modifier.padding(0.dp, 180.dp, 0.dp, 0.dp),
            contentPadding = PaddingValues(122.dp, 0.dp),
            interactionSource = interactionSource,
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.White,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.LoginDontHaveAccountLabel),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.width(2.dp))
            TextButton(
                modifier = Modifier
                    .padding(0.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(0.dp),
                onClick = onRegisterClick,
            ) {
                Text(
                    stringResource(R.string.registerLabel),
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}

/*@Preview(showBackground = false)
@Composable
fun LoginPreview() {
    LoginScreen {}
}*/
