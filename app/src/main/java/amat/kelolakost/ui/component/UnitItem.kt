package amat.kelolakost.ui.component

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.TealGreen
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UnitItem(
    modifier: Modifier = Modifier,
    name: String,
    tenantName: String,
    limitCheckOut: String,
    unitStatusId: Int,
    unitTypeName: String,
    priceDay: Int,
    priceWeek: Int,
    priceMonth: Int,
    priceThreeMonth: Int,
    priceSixMonth: Int,
    priceYear: Int,
    colorLimitCheckOut: Color = FontBlack,
    onClickCheckIn: () -> Unit,
    onClickExtend: () -> Unit,
    onClickCheckOut: () -> Unit,
    onClickMoveUnit: () -> Unit,
    onClickFinishRenovation: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name[0].uppercase() + name.drop(1),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )
                BoxRectangle(title = unitTypeName[0].uppercase() + unitTypeName.drop(1))
            }

            if (unitStatusId == 1) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubUnit(imageVector = Icons.Default.Person, title = tenantName)
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = limitCheckOut,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(color = colorLimitCheckOut)
                    )
                }
            }

            if (unitStatusId == 2) {
                BuildPrice(
                    priceDay = priceDay,
                    priceWeek = priceWeek,
                    priceMonth = priceMonth,
                    priceThreeMonth = priceThreeMonth,
                    priceSixMonth = priceSixMonth,
                    priceYear = priceYear
                )
            }

            if (unitStatusId == 3 || unitStatusId == 4) {
                Text(
                    text = "Note Todo",
                )
            }

            BuildIcon(
                unitStatusId = unitStatusId,
                onClickCheckIn = onClickCheckIn,
                onClickExtend = onClickExtend,
                onClickCheckOut = onClickCheckOut,
                onClickMoveUnit = onClickMoveUnit,
                onClickFinishRenovation = onClickFinishRenovation
            )

        }
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}

@Composable
fun BuildPrice(
    priceDay: Int,
    priceWeek: Int,
    priceMonth: Int,
    priceThreeMonth: Int,
    priceSixMonth: Int,
    priceYear: Int,
) {
    if (priceDay != 0) {
        Price(
            tint = TealGreen,
            value = "${currencyFormatterStringViewZero(priceDay.toString())}/Hari"
        )
    }
    if (priceWeek != 0) {
        Price(
            tint = TealGreen,
            value = "${currencyFormatterStringViewZero(priceWeek.toString())}/Minggu"
        )
    }
    if (priceMonth != 0) {
        Price(
            tint = TealGreen,
            value = "${currencyFormatterStringViewZero(priceMonth.toString())}/Bulan"
        )
    }
    if (priceThreeMonth != 0) {
        Price(
            tint = TealGreen,
            value = "${currencyFormatterStringViewZero(priceThreeMonth.toString())}/3 Bulan"
        )
    }
    if (priceSixMonth != 0) {
        Price(
            tint = TealGreen,
            value = "${currencyFormatterStringViewZero(priceSixMonth.toString())}/6 Bulan"
        )
    }
    if (priceYear != 0) {
        Price(
            tint = TealGreen,
            value = "${currencyFormatterStringViewZero(priceYear.toString())}/Tahun"
        )
    }
}

@Composable
fun BuildIcon(
    unitStatusId: Int,
    onClickCheckIn: () -> Unit,
    onClickExtend: () -> Unit,
    onClickCheckOut: () -> Unit,
    onClickMoveUnit: () -> Unit,
    onClickFinishRenovation: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        when (unitStatusId) {
            2 -> {
                IconUnit(id = R.drawable.check_in, Modifier.clickable {
                    onClickCheckIn()
                })
            }

            3 -> {
                IconUnit(id = R.drawable.finish_job, Modifier.clickable {
                    onClickFinishRenovation()
                })
            }

            4 -> {
                IconUnit(id = R.drawable.finish_job, Modifier.clickable {
                    onClickFinishRenovation()
                })
            }

            1 -> {
                IconUnit(id = R.drawable.ic_baseline_autorenew_24, Modifier.clickable {
                    onClickMoveUnit()
                })
                IconUnit(id = R.drawable.add_duration, Modifier.clickable {
                    onClickExtend()
                })
                IconUnit(id = R.drawable.check_out, Modifier.clickable {
                    onClickCheckOut()
                })
            }
        }

    }
}

@Composable
fun IconUnit(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .size(20.dp),
        painter = painterResource(id = id),
        contentDescription = "",
    )
}

@Composable
fun SubUnit(
    imageVector: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .padding(end = 4.dp),
            imageVector = imageVector,
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = TealGreen)
        )
        Text(
            text = title, style = TextStyle(fontSize = 16.sp), maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}