package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.ErrorColor
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.TealGreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            keyboardOptions = keyboardOptions,
            isError = isError,
            label = { Text(text = label, color = FontBlack) },
            singleLine = singleLine,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = TealGreen,
                errorBorderColor = ErrorColor
            )
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = errorMessage,
            fontSize = 14.sp,
            color = Color.Red
        )
    }

}