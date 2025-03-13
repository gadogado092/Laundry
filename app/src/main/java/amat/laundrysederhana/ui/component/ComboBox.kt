package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.ErrorColor
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.TealGreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComboBox(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = false,
    onClick: () -> Unit
) {
    Column(modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
        }
        OutlinedTextField(
            value = value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .clickable {
                    onClick()
                },
            onValueChange = {},
            enabled = false,
            textStyle = TextStyle(color = FontBlack, fontSize = 18.sp),
            singleLine = singleLine,
            isError = isError,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = TealGreen,
                errorBorderColor = ErrorColor
            ),
            trailingIcon = {
                Image(
                    imageVector = Icons.Default.ArrowDropDown,
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
fun ComboBox(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String
) {
    Column(modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
        }
        OutlinedTextField(
            value = value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            onValueChange = {},
            textStyle = TextStyle(color = FontBlack),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = TealGreen,
                errorBorderColor = ErrorColor
            ),
        )
    }
}