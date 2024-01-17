package amat.kelolakost.ui.component

import amat.kelolakost.R
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
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookingItem(
    id: String,
    unitName: String,
    unitTypeName: String,
    kostName: String,
    name: String,
    numberPhone: String,
    planCheckIn: String,
    onClickCheckIn: (String) -> Unit,
    onClickCancel: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = unitName[0].uppercase() + unitName.drop(1),
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
        }
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1F)) {
                SubBooking(Icons.Default.Person, name)
                SubBooking(Icons.Default.PhoneIphone, numberPhone)
                SubBooking(Icons.Default.House, kostName)
            }
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = planCheckIn,
                style = TextStyle(color = FontBlack),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconBooking(
                id = R.drawable.baseline_close_24,
                modifier = Modifier
                    .clickable { onClickCancel(id) }
                    .align(Alignment.Bottom)
            )
            IconBooking(
                id = R.drawable.check_in,
                modifier = Modifier
                    .clickable { onClickCheckIn(id) }
                    .align(Alignment.Bottom)
            )
        }
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}

@Composable
fun SubBooking(
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
            text = title, style = TextStyle(fontSize = 16.sp)
        )
    }
}

@Composable
fun IconBooking(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .size(20.dp),
        painter = painterResource(id = id),
        contentDescription = "",
    )
}