package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailCreditTenantItem(
    modifier: Modifier = Modifier,
    creditTenantId: String,
    remainingDebt: String,
    note: String,
    date: String,
    onClickHistory: (String) -> Unit,
    onClickPay: (String) -> Unit
) {
    Column(modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (remainingDebt.isEmpty() || remainingDebt == "0") "Lunas" else remainingDebt,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = FontBlack
                )
                Text(
                    text = date,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = FontBlack
                )
            }
            Text(
                text = note,
                style = TextStyle(fontSize = 14.sp),
                color = FontBlack
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ItemIconText(
                imageVector = Icons.Default.ListAlt,
                title = "Riwayat",
                modifier = Modifier
                    .clickable {
                        onClickHistory(creditTenantId)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            ItemIconText(
                imageVector = Icons.Default.Payment,
                title = "Bayar",
                modifier = Modifier
                    .clickable {
                        onClickPay(creditTenantId)
                    }
                    .padding(end = 16.dp, top = 4.dp, bottom = 4.dp, start = 8.dp)
            )
        }
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}