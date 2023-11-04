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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage() {

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
        ){
        LoginView()
    }

}

/*@Composable
fun Header() {
    ElevatedCard(
        modifier = Modifier
            .height(66.dp)
            .fillMaxWidth(),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally )
        {
            Text(
                text = "Watt's up",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(5.dp),
                fontSize = 40.sp,
                fontWeight = FontWeight.Normal
            )
        }

    }
}
 */
@Composable
fun LoginView(){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp, 15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            modifier = Modifier.padding(0.dp,200.dp,0.dp,40.dp),
            style = MaterialTheme.typography.headlineLarge,
            text = stringResource(R.string.login)
        )
        PasswordandEmailOutlinedTextField(Modifier.padding(0.dp,15.dp))
        ButtonLoginActionAndText()
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordandEmailOutlinedTextField(modifier: Modifier){
    var email: String by remember { mutableStateOf("")}

    OutlinedTextField(
        modifier = modifier,
        value = email,
        onValueChange = { email = it },
        label = { Text(stringResource(R.string.emailLabel)) }
    )
    
    var password: String by remember { mutableStateOf("")}

    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = { password = it },
        label = { Text(stringResource(R.string.passwordLabel)) }
    )

}
@Composable
fun ButtonLoginActionAndText() {
    val interactionSource = remember { MutableInteractionSource() }
    ElevatedButton(
        onClick = { /* do something */ },
        modifier = Modifier.padding(0.dp,240.dp,0.dp,0.dp),
        contentPadding = PaddingValues(122.dp,0.dp),
        interactionSource = interactionSource) {
        Text(text = "Login",style = MaterialTheme.typography.bodyMedium)
    }
    Row (verticalAlignment = Alignment.CenterVertically){
        Text(
            stringResource(R.string.LoginDontHaveAccountLabel),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(2.dp))
        TextButton(
            modifier = Modifier.padding(0.dp).wrapContentWidth(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(0.dp),
            onClick = {  }
        ) {
            Text(stringResource(R.string.registerLabel))

        }
        }
    }

@Preview(showBackground = false)
@Composable
fun LoginPreview() {
    LoginPage()
}