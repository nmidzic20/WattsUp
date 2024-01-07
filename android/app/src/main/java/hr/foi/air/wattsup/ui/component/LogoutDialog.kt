package hr.foi.air.wattsup.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import hr.foi.air.wattsup.network.models.TokenManager

@Composable
fun LogoutDialog(openAlertDialog: MutableState<Boolean>, onArrowBackClick: () -> Unit) {
    val context = LocalContext.current

    when {
        openAlertDialog.value -> {
            Dialog(onDismissRequest = { openAlertDialog.value = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            fontSize = 20.sp,
                            text = "Would you like to log out?",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            TextButton(
                                onClick = { openAlertDialog.value = false },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    color = Color.LightGray,
                                )
                            }
                            TextButton(
                                onClick = {
                                    openAlertDialog.value = false
                                    TokenManager.getInstance(
                                        context,
                                    ).setJWTToken("")
                                    onArrowBackClick()
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Log out",
                                    fontSize = 16.sp,
                                    color = Color.Red,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
