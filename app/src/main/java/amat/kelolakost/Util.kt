package amat.kelolakost

import android.annotation.SuppressLint
import android.util.Patterns
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.regex.Pattern

fun isEmailValid(emailAddress: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
}

fun isNumberPhoneValid(numberPhone: String): Boolean {
    if (!Pattern.matches("[a-zA-Z]+", numberPhone)) {
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

@SuppressLint("SimpleDateFormat")
fun addDateLimitApp(dateString: String, type: String, valueAdd: Int): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date

    when (type) {
        "Hari" -> c.add(Calendar.DAY_OF_MONTH, valueAdd)
        "Minggu" -> c.add(Calendar.DAY_OF_MONTH, valueAdd * 7)
        "Bulan" -> c.add(Calendar.MONTH, valueAdd)
        "3 Bulan" -> c.add(Calendar.MONTH, valueAdd)
        "6 Bulan" -> c.add(Calendar.MONTH, valueAdd)
        "Tahun" -> c.add(Calendar.YEAR, valueAdd)
    }

    val fmtOut = SimpleDateFormat("yyyy-MM-dd")
    return fmtOut.format(c.time)
}
@SuppressLint("SimpleDateFormat")
fun dateToDisplayMidFormat(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("dd MMM yyyy")
    return fmtOut.format(date)
}
fun currencyFormatterStringViewZero(num: String): String {
    if (num == "0" || num == "" || num == "null" || num.isEmpty()) {
        return "0"
    }
    try {
        val m = num.toDouble()
        val formatter = DecimalFormat("###,###,###,###,###")
        return formatter.format(m).replace(',', '.')
    } catch (e: Exception) {
        return "error"
    }
}
