package hr.foi.air.wattsup.pages

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.TopAppBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPage() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.charger_mode)) },
                navigationIcon = {
                },
            )
        },
    ) {
            RegistrationView()
    }
}

@Composable
fun RegistrationView(){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp, 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            modifier = Modifier.padding(0.dp,90.dp,0.dp,40.dp),
            style = MaterialTheme.typography.headlineLarge,
            text = stringResource(R.string.registerLabel)
        )
        PasswordAndEmailOutlinedTextField(Modifier.padding(0.dp,15.dp))
        ButtonRegisterActionAndText()
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordAndEmailOutlinedTextField(modifier: Modifier){
    val interactionSource = remember { MutableInteractionSource() }

    var firstName: String by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = firstName,
        onValueChange = { firstName = it },
        label = { Text(stringResource(R.string.first_name_label)) }
    )

    var lastName: String by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = lastName,
        onValueChange = { lastName = it },
        label = { Text(stringResource(R.string.last_name_label)) }
    )

    var email: String by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = email,
        onValueChange = { email = it },
        label = { Text(stringResource(R.string.e_mail_label)) }
    )

    var password: String by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = { password = it },
        label = { Text(stringResource(R.string.password_label)) }
    )

    var RFIDcard: String by remember { mutableStateOf("") }
    Row (verticalAlignment = Alignment.CenterVertically){
        OutlinedTextField(
            modifier = Modifier.padding(0.dp,15.dp).width(200.dp),
            value = RFIDcard,
            onValueChange = { RFIDcard = it },
            label = { Text(stringResource(R.string.rfid_card_label)) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        ElevatedButton(
            onClick = { /* do something */ },
            modifier = Modifier.padding(10.dp).clip(MaterialTheme.shapes.medium),
            contentPadding = PaddingValues(10.dp,0.dp),
            interactionSource = interactionSource) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}
@Composable
fun ButtonRegisterActionAndText() {
    val interactionSource = remember { MutableInteractionSource() }
    ElevatedButton(
        onClick = { /* do something */ },
        modifier = Modifier.padding(0.dp,90.dp,0.dp,0.dp),
        contentPadding = PaddingValues(122.dp,0.dp),
        interactionSource = interactionSource) {
        Text(text = "Register",style = MaterialTheme.typography.bodyMedium)
    }
    Row (verticalAlignment = Alignment.CenterVertically){
        Text(
            stringResource(R.string.Alreadyhaveaccountlabel),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(2.dp))
        TextButton(
            modifier = Modifier
                .padding(0.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(0.dp),
            onClick = {  }
        ) {
            Text(
                stringResource(R.string.log_in_label)
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun RegistrationPagePreview() {
    RegistrationPage()
}