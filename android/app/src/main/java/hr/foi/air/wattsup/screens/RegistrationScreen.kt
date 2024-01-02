package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.ui.component.LoadingSpinner
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.viewmodels.AuthenticationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onArrowBackClick: () -> Unit,
    onLogInClick: () -> Unit,
    viewModel: AuthenticationViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        val modifier = Modifier.padding(it)

        RegistrationView(onLogInClick, viewModel, modifier)
    }
}

@Composable
fun RegistrationView(
    onLogInClick: () -> Unit,
    viewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 50.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CentralView(Modifier.padding(0.dp, 15.dp), onLogInClick, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralView(modifier: Modifier, onLogInClick: () -> Unit, viewModel: AuthenticationViewModel) {
    val firstName by viewModel.firstName.observeAsState()
    val lastName by viewModel.lastName.observeAsState()
    val email by viewModel.email.observeAsState()
    val password by viewModel.password.observeAsState()
    val card by viewModel.card.observeAsState()
    val invalidEmail by viewModel.invalidEmail.observeAsState()
    val invalidPassword by viewModel.invalidPassword.observeAsState()
    val interactionSource by viewModel.interactionSource.observeAsState()
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
    val showLoading by viewModel.showLoading.observeAsState()

    val context = LocalContext.current

    OutlinedTextField(
        modifier = modifier,
        value = firstName!!,
        onValueChange = { viewModel.updateFirstName(it) },
        label = { Text(stringResource(R.string.first_name_label)) },
        singleLine = true,
    )

    OutlinedTextField(
        modifier = modifier,
        value = lastName!!,
        onValueChange = { viewModel.updateLastName(it) },
        label = { Text(stringResource(R.string.last_name_label)) },
        singleLine = true,
    )

    OutlinedTextField(
        modifier = modifier,
        value = email!!,
        onValueChange = { viewModel.updateEmail(it) },
        colors = if (invalidEmail == true) {
            TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Red,
                cursorColor = Color.Red,
                textColor = Color.Red,
            )
        } else {
            TextFieldDefaults.outlinedTextFieldColors()
        },
        label = { Text(stringResource(R.string.e_mail_label)) },
        singleLine = true,
    )

    OutlinedTextField(
        modifier = modifier,
        value = password!!,
        onValueChange = { viewModel.updatePassword(it) },
        colors = if (invalidPassword == true) {
            TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Red,
                cursorColor = Color.Red,
                textColor = Color.Red,
            )
        } else {
            TextFieldDefaults.outlinedTextFieldColors()
        },
        visualTransformation = if (passwordVisible == true) VisualTransformation.None else PasswordVisualTransformation(),
        label = { Text(stringResource(R.string.password_label)) },
        singleLine = true,
        trailingIcon = {
            val image = if (passwordVisible == true) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            val description = if (passwordVisible == true) "Hide password" else "Show password"

            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                Icon(imageVector = image, description)
            }
        },
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(0.dp, 15.dp)
                .width(200.dp),
            value = card?.value ?: "",
            onValueChange = { viewModel.updateCard(Card(id = 0, value = it)) },
            label = { Text(stringResource(R.string.card_label)) },
            singleLine = true,
        )

        Spacer(modifier = Modifier.width(4.dp))

        ElevatedButton(
            onClick = {
            },
            modifier = Modifier
                .padding(10.dp)
                .clip(MaterialTheme.shapes.medium),
            contentPadding = PaddingValues(10.dp, 0.dp),
            interactionSource = interactionSource!!,
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
            viewModel.updateInvalidEmail(
                !Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matches(
                    email!!,
                ),
            )
            viewModel.updateInvalidPassword(!Regex("^.{6,}\$").matches(password!!))
            if (invalidPassword == true || invalidEmail == true) {
                viewModel.showToast(context, "Invalid e-mail or password")
            } else {
                viewModel.registerUser(
                    firstName!!,
                    lastName!!,
                    email!!,
                    password!!,
                    card,
                    context,
                    onLogInClick,
                )
            }
        },
        modifier = Modifier.padding(0.dp, 30.dp, 0.dp, 0.dp),
        contentPadding = PaddingValues(122.dp, 0.dp),
        interactionSource = interactionSource!!,
        enabled = !showLoading!!,
    ) {
        if (showLoading == true) {
            LoadingSpinner()
        } else {
            Text(
                text = "Register",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
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

@Preview(showBackground = false)
@Composable
fun RegistrationPreview() {
    RegistrationScreen({}, {}, AuthenticationViewModel())
}
