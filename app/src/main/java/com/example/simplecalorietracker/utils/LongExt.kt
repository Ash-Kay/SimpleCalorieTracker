package com.example.simplecalorietracker.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

fun Long.compactNumber(): String {
    if (this < 1000) return "" + this
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return String.format(
        "%.1f %cCal", this / 1000.0.pow(exp.toDouble()),
        "kMGTPE"[exp - 1]
    )
}

fun Long.toHumanDate(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(this)
}