package amat.laundry

import amat.laundry.ui.theme.ColorRed
import amat.laundry.ui.theme.ColorYellow
import amat.laundry.ui.theme.FontBlack
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.compose.ui.graphics.Color
import java.io.Serializable
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

fun generateMd5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun isEmailValid(emailAddress: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
}

fun isNumberPhoneValid(numberPhone: String): Boolean {
    if (!Pattern.matches("[a-zA-Z]+", numberPhone)) {
        return numberPhone.length in 7..15
    }
    return false
}

fun checkIsEmailValid(emailAddress: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
}

fun checkIsNumberPhoneValid(numberPhone: String): Boolean {
    if (!Pattern.matches("[a-zA-Z]+", numberPhone)) {
        return numberPhone.length in 7..15
    }
    return false
}

fun generateZeroInvoice(value: String): String {
    var newValue = ""
    if (value.length == 1) {
        newValue = "000$value"
    } else if (value.length == 2) {
        newValue = "00$value"
    } else if (value.length == 3) {
        newValue = "0$value"
    } else if (value.length == 4) {
        newValue = value
    }

    return newValue
}

@SuppressLint("SimpleDateFormat")
fun dateUniversalToDisplay(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("dd MMM yyyy")
    return fmtOut.format(date as Date)
}

@SuppressLint("SimpleDateFormat")
fun dateTimeUniversalToDisplay(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("dd MMM yyyy HH:mm")
    return fmtOut.format(date as Date)
}

@SuppressLint("SimpleDateFormat")
fun dateTimeToKodeInvoice(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("yyyyMMdd")
    return fmtOut.format(date as Date)
}

@SuppressLint("SimpleDateFormat")
fun generateDateTimeNow(): String {
    val c = Calendar.getInstance()
    val fmtOut = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun calenderSelect(date: Date): String {
    val fmtOut = SimpleDateFormat("yyyy-MM-dd")
    return fmtOut.format(date)
}

@SuppressLint("SimpleDateFormat")
fun generateDateNow(): String {
    val c = Calendar.getInstance()
    val fmtOut = SimpleDateFormat("yyyy-MM-dd")
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
        "3 Bulan" -> c.add(Calendar.MONTH, valueAdd * 3)
        "6 Bulan" -> c.add(Calendar.MONTH, valueAdd * 6)
        "Tahun" -> c.add(Calendar.YEAR, valueAdd)
    }

    val fmtOut = SimpleDateFormat("yyyy-MM-dd")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun generateTextDuration(type: String, valueAdd: Int): String {
    var data = ""
    when (type) {
        "Hari" -> {
            data = "$valueAdd Hari"
        }

        "Minggu" -> {
            data = "$valueAdd Minggu"
        }

        "Bulan" -> data = "$valueAdd Bulan"
        "3 Bulan" -> {
            data = "${valueAdd * 3} Bulan"
        }

        "6 Bulan" -> data = "${valueAdd * 6} Bulan"
        "Tahun" -> data = "$valueAdd Tahun"
    }
    return data
}

@SuppressLint("SimpleDateFormat")
fun dateToDisplayMidFormat(dateString: String): String {
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val date = fmt.parse(dateString)

        val fmtOut = SimpleDateFormat("dd MMM yyyy")
        fmtOut.format(date)
    } catch (e: Exception) {
        dateString
    }
}

@SuppressLint("SimpleDateFormat")
fun dateToDisplayDayMonth(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("dd MMM")
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
        if (typeWa == "Standard" || typeWa == "") {
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

@SuppressLint("SimpleDateFormat")
fun convertDateToDay(dateString: String, dayAdd: Int = 0): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date
    c.add(Calendar.DAY_OF_MONTH, dayAdd)
    val fmtOut = SimpleDateFormat("dd")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun convertDateToMonth(dateString: String, dayAdd: Int = 0): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date
    c.add(Calendar.DAY_OF_MONTH, dayAdd)

    val fmtOut = SimpleDateFormat("M")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun convertDateToYear(dateString: String, dayAdd: Int = 0): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date
    c.add(Calendar.DAY_OF_MONTH, dayAdd)
    val fmtOut = SimpleDateFormat("yyyy")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun dateDialogToUniversalFormat(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-M-dd")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("yyyy-MM-dd")
    return fmtOut.format(date as Date)
}

@SuppressLint("SimpleDateFormat")
fun generateLimitText(checkOutDate: String?): String {
    try {
        val myFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateNow = myFormat.format(Calendar.getInstance().time)
        val dateNowParse = myFormat.parse(dateNow)

        val date2 = checkOutDate?.let { myFormat.parse(it) }
        val diff = date2!!.time - dateNowParse!!.time
        val day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString()
        return if (day.toInt() == 0) {
            "Hari Terakhir"
        } else if (day.toInt() in 1..3) {
            "Sisa $day Hari"
        } else if (day.toInt() < 0) {
            "Lewat " + Math.abs(day.toInt()) + " Hari"
        } else {
            "Sisa $day Hari"
        }
    } catch (e: Exception) {
        return "Batas $checkOutDate"
    }
}

@SuppressLint("SimpleDateFormat")
fun generateLimitColor(checkOutDate: String?): Color {
    return try {
        val myFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateNow = myFormat.format(Calendar.getInstance().time)
        val dateNowParse = myFormat.parse(dateNow)

        val date2 = checkOutDate?.let { myFormat.parse(it) }
        val diff = date2!!.time - dateNowParse!!.time
        val day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString()
        if (day.toInt() in 1..3) {
            ColorYellow
        } else if (day.toInt() <= 0) {
            ColorRed
        } else {
            FontBlack
        }
    } catch (e: Exception) {
        FontBlack
    }
}

@SuppressLint("SimpleDateFormat")
fun getLimitDay(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val dateLimit = fmt.parse(dateString)
    val currentDate = Date()
    return try {
        if (dateLimit != null) {
            val diff: Long = dateLimit.time - currentDate.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diff)
            diffInDays.toInt().toString()
        } else {
            ""
        }

    } catch (e: Exception) {
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun dateRoomDay(dateString: String, dayAdd: Int = 0): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date
    c.add(Calendar.DAY_OF_MONTH, dayAdd)
    val fmtOut = SimpleDateFormat("dd")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun dateRoomMonth(dateString: String, dayAdd: Int = 0): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date
    c.add(Calendar.DAY_OF_MONTH, dayAdd)

    val fmtOut = SimpleDateFormat("M")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun dateRoomYear(dateString: String, dayAdd: Int = 0): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    c.time = fmt.parse(dateString) as Date
    c.add(Calendar.DAY_OF_MONTH, dayAdd)
    val fmtOut = SimpleDateFormat("yyyy")
    return fmtOut.format(c.time)
}

@SuppressLint("SimpleDateFormat")
fun checkDateRangeValid(dateStart: String, dateEnd: String): Boolean {
    try {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val start = fmt.parse(dateStart)
        val end = fmt.parse(dateEnd)
        if (start != null) {
            if (start <= end) {
                return true
            }
        }
        return false
    } catch (e: Exception) {
        return false
    }
}

fun cleanPointZeroFloat(value: Float): String {
    return if (value == 0F) {
        ""
    } else {
        val myString = value.toString()
        val last2Character = myString.substring(myString.length - 2, myString.length)
        if (last2Character == ".0") {
            myString.substring(0, myString.length - 2)
        } else {
            value.toString()
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun dateDialogToRoomFormat(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-M-dd")
    val date = fmt.parse(dateString)

    val fmtOut = SimpleDateFormat("yyyy-MM-dd")
    return fmtOut.format(date as Date)
}

fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        activity.intent.getSerializableExtra(name, clazz)!!
    else
        activity.intent.getSerializableExtra(name) as T
}

