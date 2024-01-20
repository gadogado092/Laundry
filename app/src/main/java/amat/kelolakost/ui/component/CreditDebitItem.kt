package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.ColorRed
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.TealGreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreditDebitItem(
    modifier: Modifier = Modifier,
    creditDebitId: String,
    creditDebitName: String,
    remaining: String,
    dueDate: String,
    status: Int,
    onClickHistory: (String) -> Unit,
    onClickPay: (String) -> Unit,
    onClickRemove: (String) -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ItemTitle(text = creditDebitName)
                BoxRectangle(
                    title = generateTextStatus(status),
                    backgroundColor = generateColorStatus(status)
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (remaining.isEmpty() || remaining == "0") "Lunas" else remaining,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = FontBlack
                )
                Text(
                    text = dueDate,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = FontBlack
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ItemIconText(
                imageVector = Icons.Default.ListAlt,
                title = "Riwayat",
                modifier = Modifier
                    .clickable {
                        onClickHistory(creditDebitId)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            if (remaining.isEmpty() || remaining == "0") {
                ItemIconText(
                    imageVector = Icons.Default.DeleteOutline,
                    title = "Hapus",
                    modifier = Modifier
                        .clickable {
                            onClickRemove(creditDebitId)
                        }
                        .padding(end = 16.dp, top = 4.dp, bottom = 4.dp, start = 8.dp)
                )
            } else {
                ItemIconText(
                    imageVector = Icons.Default.Payment,
                    title = "Bayar",
                    modifier = Modifier
                        .clickable {
                            onClickPay(creditDebitId)
                        }
                        .padding(end = 16.dp, top = 4.dp, bottom = 4.dp, start = 8.dp)
                )
            }
        }
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}

fun generateTextStatus(status: Int): String {
    if (status == 0) {
        return "Hutang"
    } else if (status == 1) {
        return "Piutang"
    }
    return ""
}

fun generateColorStatus(status: Int): Color {
    if (status == 0) {
        return ColorRed
    } else if (status == 1) {
        return TealGreen
    }
    return TealGreen
}