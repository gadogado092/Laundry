package amat.laundry.ui.component

import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreyLight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreditTenantItem(
    modifier: Modifier = Modifier,
    tenantName: String,
    total: String,
    tenantNumberPhone: String
) {
    Column(modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            ItemTitle(text = tenantName)
            ItemIconText(imageVector = Icons.Default.PhoneIphone, title = tenantNumberPhone)
            Text(
                text = total,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = FontBlack
                )
            )
        }
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}