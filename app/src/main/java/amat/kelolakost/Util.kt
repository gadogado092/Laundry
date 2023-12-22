package amat.kelolakost

import android.annotation.SuppressLint
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.regex.Pattern

fun isEmailValid(emailAddress: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
}

fun isNumberPhoneValid(numberPhone: String): Boolean {
    if(!Pattern.matches("[a-zA-Z]+", numberPhone)) {
        return numberPhone.length in 7..13
    }
    return false
}

@SuppressLint("SimpleDateFormat")
fun generateDateTimeNow(): String {
    val c = Calendar.getInstance()
    val fmtOut = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return fmtOut.format(c.time)
}