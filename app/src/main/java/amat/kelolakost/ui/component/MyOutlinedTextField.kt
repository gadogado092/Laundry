package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.TealGreen
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        modifier = modifier,
        label = { Text(text = label, color = FontBlack) },
        singleLine = singleLine,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = TealGreen
        ),
        keyboardOptions = keyboardOptions
    )
}