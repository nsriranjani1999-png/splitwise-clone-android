package com.example.splitwise.util

import java.math.BigDecimal
import java.math.RoundingMode

/** 90000 paise -> "₹900.00" */
fun formatRupees(paise: Long): String = "₹" + BigDecimal.valueOf(paise, 2).toPlainString()

/** "900" or "12.5" -> paise as Long. Returns null if the text is not a valid amount. */
fun parseToPaise(text: String): Long? = try {
    BigDecimal(text.trim())
        .setScale(2, RoundingMode.HALF_UP)
        .movePointRight(2)
        .longValueExact()
} catch (e: Exception) {
    null
}
