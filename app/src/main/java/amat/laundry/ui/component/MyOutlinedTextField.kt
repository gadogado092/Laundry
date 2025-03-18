package amat.laundry.ui.component

import amat.laundry.ui.theme.ErrorColor
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.TealGreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
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
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
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

@Composable
fun MyOutlinedTextFieldCurrency(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    currencyValue: String = "",
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            label = { Text(text = label, color = FontBlack) },
            singleLine = singleLine,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = TealGreen,
                errorBorderColor = ErrorColor
            )
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = currencyValue,
                fontSize = 14.sp,
                color = FontBlack
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = errorMessage,
                fontSize = 14.sp,
                color = Color.Red
            )
        }
    }

}