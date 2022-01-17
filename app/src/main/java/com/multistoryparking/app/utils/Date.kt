package com.multistoryparking.app.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

val now get() = System.currentTimeMillis()

fun Long.toCalendar(): Calendar =
    Calendar.getInstance().apply { timeInMillis = this@toCalendar }

fun Date.toFormat(pattern: String = "MMM d, h:mm a"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

@SuppressLint("SimpleDateFormat")
fun String.timeToInstant(pattern: String = "HH:mm"): String =
    (SimpleDateFormat("HH:mm:ss").parse(this) ?: Date()).toFormat(pattern)

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}