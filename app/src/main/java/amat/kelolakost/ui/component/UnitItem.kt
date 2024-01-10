package amat.kelolakost.ui.component

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.entity.PriceDuration
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.TealGreen
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    id: String,
    name: String,
    noteMaintenance: String,
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
    priceGuarantee: Int,
    colorLimitCheckOut: Color = FontBlack,
    onClickCheckIn: (String, String, String, String, String) -> Unit,
    onClickExtend: (String, String, String) -> Unit,
    onClickCheckOut: (String) -> Unit,
    onClickMoveUnit: (String) -> Unit,
    onClickFinishRenovation: (String) -> Unit
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
                if (unitStatusId == 3) {
                    Text(
                        text = "Pembersihan\n$noteMaintenance",
                    )
                } else {
                    Text(
                        text = "Perbaikan\n$noteMaintenance",
                    )
                }

            }

            BuildIcon(
                unitId = id,
                unitName = name,
                unitStatusId = unitStatusId,
                onClickCheckIn = onClickCheckIn,
                onClickExtend = onClickExtend,
                onClickCheckOut = onClickCheckOut,
                onClickMoveUnit = onClickMoveUnit,
                onClickFinishRenovation = onClickFinishRenovation,
                priceGuarantee = priceGuarantee.toString(),
                priceDuration = getPriceDuration(
                    priceDay = priceDay,
                    priceWeek = priceWeek,
                    priceMonth = priceMonth,
                    priceThreeMonth = priceThreeMonth,
                    priceSixMonth = priceSixMonth,
                    priceYear = priceYear
                )
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
    unitId: String,
    unitName: String,
    unitStatusId: Int,
    priceDuration: PriceDuration,
    priceGuarantee: String,
    onClickCheckIn: (String, String, String, String, String) -> Unit,
    onClickExtend: (String, String, String) -> Unit,
    onClickCheckOut: (String) -> Unit,
    onClickMoveUnit: (String) -> Unit,
    onClickFinishRenovation: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        when (unitStatusId) {
            2 -> {
                IconUnit(id = R.drawable.check_in, Modifier.clickable {
                    onClickCheckIn(
                        unitId,
                        unitName,
                        priceDuration.price,
                        priceDuration.duration,
                        priceGuarantee
                    )
                })
            }

            3 -> {
                IconUnit(id = R.drawable.finish_job, Modifier.clickable {
                    onClickFinishRenovation(unitId)
                })
            }

            4 -> {
                IconUnit(id = R.drawable.finish_job, Modifier.clickable {
                    onClickFinishRenovation(unitId)
                })
            }

            1 -> {
                IconUnit(id = R.drawable.check_out, Modifier.clickable {
                    onClickCheckOut(unitId)
                })
                IconUnit(id = R.drawable.ic_baseline_autorenew_24, Modifier.clickable {
                    onClickMoveUnit(unitId)
                })
                IconUnit(id = R.drawable.add_duration, Modifier.clickable {
                    onClickExtend(
                        unitId, priceDuration.price,
                        priceDuration.duration
                    )
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

fun getPriceDuration(
    priceDay: Int,
    priceWeek: Int,
    priceMonth: Int,
    priceThreeMonth: Int,
    priceSixMonth: Int,
    priceYear: Int
): PriceDuration {
    if (priceDay != 0) {
        return PriceDuration(priceDay.toString(), "Hari")
    }
    if (priceWeek != 0) {
        return PriceDuration(priceWeek.toString(), "Minggu")
    }
    if (priceMonth != 0) {
        return PriceDuration(priceMonth.toString(), "Bulan")
    }
    if (priceThreeMonth != 0) {
        return PriceDuration(priceThreeMonth.toString(), "3 Bulan")
    }
    if (priceSixMonth != 0) {
        return PriceDuration(priceSixMonth.toString(), "6 Bulan")
    }
    if (priceYear != 0) {
        return PriceDuration(priceYear.toString(), "Tahun")
    }

    return PriceDuration("", "")
}