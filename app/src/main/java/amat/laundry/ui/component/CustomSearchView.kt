package amat.laundry.ui.component

import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomSearchView(
    placeHolderText: String = "Search",
    search: String,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onValueChange: (String) -> Unit
) {
    val backgroundColor = FontWhite

    Row(
        modifier
            .background(GreenDark)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onClickBack
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(CircleShape)
                .background(backgroundColor),
        ) {
            TextField(
                value = search,
                onValueChange = onValueChange,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    placeholderColor = Color(0XFF888D91),
                    leadingIconColor = Color(0XFF888D91),
                    trailingIconColor = Color(0XFF888D91),
                    textColor = GreenDark,
                    focusedIndicatorColor = Color.Transparent, cursorColor = Color(0XFF070E14)
                ),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.padding(bottom = 4.dp),
                        imageVector = Icons.Default.Search, contentDescription = ""
                    )
                },
                placeholder = { Text(text = placeHolderText) }
            )
        }
    }

}