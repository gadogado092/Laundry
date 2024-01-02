package amat.kelolakost.ui.component

import amat.kelolakost.R
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.TealGreen
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
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
fun TenantItem(
    name: String,
    numberPhone: String,
    limitCheckOut: String,
    unitId: String,
    unitName: String,
    kostName: String,
    modifier: Modifier = Modifier,
    colorLimitCheckOut: Color = FontBlack,
    onClickWa: () -> Unit,
    onClickPhone: () -> Unit,
    onClickSms: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 18.sp,
                    color = FontBlack,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (unitId != "0") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1F)) {
                        SubTenant(Icons.Default.Bed, unitName)
                        SubTenant(Icons.Default.House, kostName)
                    }
                    Text(
                        text = limitCheckOut,
                        style = TextStyle(color = colorLimitCheckOut),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = if (unitId != "0") {
                    Alignment.Top
                } else {
                    Alignment.CenterVertically
                }
            ) {
                SubTenant(Icons.Default.PhoneIphone, numberPhone, modifier = Modifier.weight(1F))
                IconTenant(
                    id = R.drawable.ic_wa,
                    modifier = Modifier
                        .clickable {
                            onClickWa()
                        }
                        .align(Bottom)
                )
                IconTenant(id = R.drawable.ic_outline_call_24, modifier = Modifier
                    .clickable {
                        onClickPhone()
                    }
                    .align(Bottom))
                IconTenant(
                    id = R.drawable.ic_twotone_sms_24,
                    modifier = Modifier
                        .clickable { onClickSms() }
                        .align(Bottom)
                )
            }
        }
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}

@Composable
fun SubTenant(
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

@Composable
fun IconTenant(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .size(20.dp),
        painter = painterResource(id = id),
        contentDescription = "",
    )
}