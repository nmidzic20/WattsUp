package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import hr.foi.air.wattsup.ui.component.TopAppBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val authService = NetworkService.authService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(onArrowBackClick: () -> Unit, onLogInClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        RegistrationView(onLogInClick)
    }
}

@Composable
fun RegistrationView(onLogInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp, 5.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 90.dp, 0.dp, 40.dp),
            style = MaterialTheme.typography.headlineLarge,
            text = stringResource(R.string.registerLabel),
        )
        CentralView(Modifier.padding(0.dp, 15.dp), onLogInClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralView(modifier: Modifier, onLogInClick: () -> Unit) {
    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    var card: Card? by remember { mutableStateOf(null) }
    var invalidEmail by remember { mutableStateOf(false) }
    var invalidPassword by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        modifier = modifier,
        value = firstName,
        onValueChange = { firstName = it },
        label = { Text(stringResource(R.string.first_name_label)) },
    )

    OutlinedTextField(
        modifier = modifier,
        value = lastName,
        onValueChange = { lastName = it },
        label = { Text(stringResource(R.string.last_name_label)) },
    )

    OutlinedTextField(
        modifier = modifier,
        value = email,
        onValueChange = {
            email = it
            invalidEmail = !Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matches(it)
        },
        colors = if(invalidEmail){
            TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Red,
                cursorColor = Color.Red,
                textColor = Color.Red)
        }else{
            TextFieldDefaults.outlinedTextFieldColors()
        },
        label = { Text(stringResource(R.string.e_mail_label)) },
    )

    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = {
            password = it
            invalidPassword = !Regex("^.{6,}\$").matches(it)
        },
        colors = if(invalidPassword){
            TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Red,
                cursorColor = Color.Red,
                textColor = Color.Red)
        }else{
            TextFieldDefaults.outlinedTextFieldColors()
        },
        visualTransformation = PasswordVisualTransformation(),
        label = { Text(stringResource(R.string.password_label)) },
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier = Modifier
                .padding(0.dp, 15.dp)
                .width(200.dp),
            value = card?.value ?: "",
            onValueChange = { card = Card(it) },
            label = { Text(stringResource(R.string.card_label)) },
        )
        Spacer(modifier = Modifier.width(4.dp))
        ElevatedButton(
            onClick = {

            },
            modifier = Modifier
                .padding(10.dp)
                .clip(MaterialTheme.shapes.medium),
            contentPadding = PaddingValues(10.dp, 0.dp),
            interactionSource = interactionSource,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp),
            )
        }
    }

    ElevatedButton(
        onClick = {
            if (invalidPassword || invalidEmail) {
                // TODO: implement popup with notification what went wrong
            } else {
                authService.registerUser(
                    RegistrationBody(firstName, lastName, email, password, card),
                ).enqueue(
                    object : Callback<RegistrationResponseBody> {
                        override fun onResponse(
                            call: Call<RegistrationResponseBody>?,
                            response: Response<RegistrationResponseBody>?,
                        ) {
                            Log.i("RES", response.toString())

                            if (response?.isSuccessful == true) {
                                val responseBody = response.body()
                                val message = responseBody?.message
                                Log.i("Response", (message ?: response).toString())
                                onLogInClick()
                            } else {
                                val responseBody = response?.body()
                                val message = responseBody?.message
                                Log.i("Response", (message ?: response).toString())
                                onLogInClick()
                            }
                        }

                        override fun onFailure(
                            call: Call<RegistrationResponseBody>?,
                            t: Throwable?,
                        ) {
                            val message = "Failed to register user"
                            Log.i("Response", message)
                            Log.i("Response", t.toString())
                            onLogInClick()
                        }
                    },
                )
            }
        },
        modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp),
        contentPadding = PaddingValues(122.dp, 0.dp),
        interactionSource = interactionSource,
    ) {
        Text(text = "Register", style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.Alreadyhaveaccountlabel),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.width(2.dp))
        TextButton(
            modifier = Modifier
                .padding(0.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(0.dp),
            onClick = onLogInClick,
        ) {
            Text(
                stringResource(R.string.log_in_label),
            )
        }
    }
}

/*
@Composable
fun PopupWithMessage( isOpen: Boolean, message: String, onDismiss: () -> Unit) {
    if(isOpen){
        Dialog(
            onDismissRequest = {  },
            content = {
                Card(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Blue,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = message, modifier = Modifier.padding(8.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButton(onClick = { onDismiss }) {
                            Text("OK")
                        }
                    }
                }
            }
        )
    }

}*/

@Preview(showBackground = false)
@Composable
fun RegistrationPreview() {
    RegistrationScreen({}, {})
}
