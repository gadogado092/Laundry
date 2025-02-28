package amat.laundry.ui.component

import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreenDark
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityTextField(
    modifier: Modifier = Modifier,
    value: String,
    errorMessage: String = "",
    onAddClick: () -> Unit,
    onMinClick: () -> Unit,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            singleLine = true,
            enabled = false,
            textStyle = TextStyle(color = FontBlack, textAlign = TextAlign.Center),
            leadingIcon = {
                Image(
                    modifier = Modifier.clickable {
                        onMinClick()
                    },
                    imageVector = Icons.Default.Remove,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(GreenDark)
                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier.clickable {
                        onAddClick()
                    },
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(GreenDark)
                )
            }
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = errorMessage,
            fontSize = 14.sp,
            color = Color.Red
        )
    }
}

@Composable
fun SimpleQuantityTextField(
    modifier: Modifier = Modifier,
    value: String,
    onAddClick: () -> Unit,
    onMinClick: () -> Unit,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.padding(0.dp),
            value = value,
            onValueChange = {},
            singleLine = true,
            enabled = false,
            textStyle = TextStyle(color = FontBlack, textAlign = TextAlign.Center),
            leadingIcon = {
                Image(
                    modifier = Modifier.clickable {
                        onMinClick()
                    },
                    imageVector = Icons.Default.Remove,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(GreenDark)
                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier.clickable {
                        onAddClick()
                    },
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(GreenDark)
                )
            }
        )
    }
}