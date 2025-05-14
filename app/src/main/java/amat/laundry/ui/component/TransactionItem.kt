package amat.laundry.ui.component

import amat.laundry.R
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.ColorPayment
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontBlue
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.TealGreen
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    invoiceCode: String,
    price: String,
    createAt: String,
    customerName: String,
    isFullPayment: Boolean,
    paymentDate: String,
    finishAt: String,
    statusId: Int,
    estimationReadyToPickup: String,
    onClickWa: () -> Unit,
    onClickPhone: () -> Unit,
    onClickSms: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = invoiceCode,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlue,
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = createAt,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 4.dp),
                            imageVector = Icons.Default.Person,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(color = TealGreen)
                        )
                        Text(
                            text = customerName,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = FontBlack,
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = price,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                BoxRectangle(
                    title = if (isFullPayment) "Lunas $paymentDate" else "Belum Lunas ",
                    backgroundColor = if (isFullPayment) Blue else ColorPayment
                )
            }

            generateStatusLaundry(
                statusId,
                finishAt,
                estimationReadyToPickup,
                onClickWa,
                onClickPhone,
                onClickSms
            )

        }
        Spacer(Modifier.height(4.dp))
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}

@Composable
fun generateStatusLaundry(
    statusId: Int,
    finishAt: String,
    estimationReadyToPickup: String,
    onClickWa: () -> Unit,
    onClickPhone: () -> Unit,
    onClickSms: () -> Unit
) {
    if (statusId == 1) {
        Row(verticalAlignment = Alignment.Bottom) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
                imageVector = Icons.Default.LocalLaundryService,
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = TealGreen)
            )
            Text(
                text = "Diproses - Est. Siap Ambil $estimationReadyToPickup",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                ),
                modifier = Modifier.padding(end = 8.dp, bottom = 2.dp)
            )
        }
    } else if (statusId == 2) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                    imageVector = Icons.Default.ShoppingCartCheckout,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = TealGreen)
                )
                Text(
                    text = "Siap Ambil",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(end = 8.dp, bottom = 2.dp)
                )
            }

            Row {
                IconTenant(
                    id = R.drawable.ic_outline_call_24, modifier = Modifier
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
                IconTenant(
                    id = R.drawable.ic_wa,
                    modifier = Modifier
                        .clickable {
                            onClickWa()
                        }
                        .align(Bottom)
                )
            }
        }
    } else if (statusId == 3) {
        Row(verticalAlignment = Alignment.Bottom) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = Blue)
            )
            Text(
                text = "Selesai $finishAt",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                ),
                modifier = Modifier.padding(end = 8.dp, bottom = 2.dp)
            )
        }
    }
}

@Composable
fun IconTransaction(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .size(20.dp),
        painter = painterResource(id = id),
        contentDescription = "",
    )
}