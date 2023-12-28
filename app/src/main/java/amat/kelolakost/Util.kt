package amat.kelolakost

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.Patterns
import java.net.URLEncoder
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
    val numClean = num.replace(".", "")
    if (numClean == "0" || numClean == "" || numClean == "null" || numClean.isEmpty()) {
        return "0"
    }
    try {
        val m = numClean.toDouble()
        val formatter = DecimalFormat("###,###,###,###,###")
        return formatter.format(m).replace(',', '.')
    } catch (e: Exception) {
        return "error"
    }
}

fun currencyFormatterString(num: String): String {
    val numClean = num.replace(".", "")
    if (numClean == "0" || numClean == "" || numClean == "null" || numClean.isEmpty()) {
        return ""
    }
    return try {
        val m = numClean.toDouble()
        val formatter = DecimalFormat("###,###,###,###,###")
        formatter.format(m).replace(',', '.')
    } catch (e: Exception) {
        ""
    }
}

fun cleanCurrencyFormatter(num: String): Int {
    return try {
        val numClean = num.replace(".", "")
        if (numClean == "0" || numClean == "" || numClean == "null" || numClean.isEmpty()) {
            0
        } else {
            numClean.toInt()
        }
    } catch (e: Exception) {
        0
    }
}

fun sendWhatsApp(c: Context, phone: String, message: String, typeWa: String = "Standard") {
    var str = phone
    if (str.isNotEmpty()) {
        if (phone[0] == '0') {
            str = "62" + str.substring(1)
        }
    }

    val packageManager = c.packageManager
    val i = Intent(Intent.ACTION_VIEW)
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    Log.d("myLog", "call wa")
    try {
        Log.d("myLog", "try wa")
        Log.d("myLog", str)
        Log.d("myLog", packageManager.toString())

        val url = "https://wa.me/" + str + "?text=" + URLEncoder.encode(message, "UTF-8")
        if (typeWa == "Standard") {
            i.setPackage("com.whatsapp")
        } else if (typeWa == "Business") {
            i.setPackage("com.whatsapp.w4b")
        }
        i.data = Uri.parse(url)
        c.startActivity(i)
    } catch (e: Exception) {
        /*e.printStackTrace()*/
        Log.d("myLog", e.message.toString())
    }
}
