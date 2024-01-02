package amat.kelolakost.ui.component

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UnitTypeItem(
    modifier: Modifier = Modifier,
    name: String,
    note: String,
    priceGuarantee: String,
    priceDay: Int,
    priceWeek: Int,
    priceMonth: Int,
    priceThreeMonth: Int,
    priceSixMonth: Int,
    priceYear: Int
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = name[0].uppercase() + name.drop(1), style = TextStyle(fontSize = 18.sp),
                color = FontBlack
            )
            Text(
                text = note, style = TextStyle(fontSize = 14.sp),
                color = FontBlack
            )
            Text(
                text = stringResource(id = R.string.subtitle_price_guarantee) + " " + priceGuarantee,
                style = TextStyle(fontSize = 14.sp),
                color = FontBlack
            )
            if (priceDay != 0) {
                Price(value = "${currencyFormatterStringViewZero(priceDay.toString())}/Hari")
            }
            if (priceWeek != 0) {
                Price(value = "${currencyFormatterStringViewZero(priceWeek.toString())}/Minggu")
            }
            if (priceMonth != 0) {
                Price(value = "${currencyFormatterStringViewZero(priceMonth.toString())}/Bulan")
            }
            if (priceThreeMonth != 0) {
                Price(value = "${currencyFormatterStringViewZero(priceThreeMonth.toString())}/3 Bulan")
            }
            if (priceSixMonth != 0) {
                Price(value = "${currencyFormatterStringViewZero(priceSixMonth.toString())}/6 Bulan")
            }
            if (priceYear != 0) {
                Price(value = "${currencyFormatterStringViewZero(priceYear.toString())}/Tahun")
            }
        }
        Divider(
            color = GreyLight,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}