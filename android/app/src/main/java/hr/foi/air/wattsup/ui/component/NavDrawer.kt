package hr.foi.air.wattsup.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NavDrawer(
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 16.sp),
    onItemClick: (String) -> Unit = {}, // Navigate on Clicking
) {
    LazyColumn(modifier) {
        item {
            Text(text = "Drawer header")
        }
        items(screens) // Here, add a list of Screen Object
        { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item.title) // pass the title as route to navigate
                    }
                    .padding(16.dp),
            ) {
                // Design for each Drawer Item
                Icon(imageVector = item.icon, contentDescription = item.title)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title, // Drawer Item name
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
