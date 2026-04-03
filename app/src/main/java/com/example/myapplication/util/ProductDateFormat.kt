package com.example.myapplication.util

import android.content.Context
import android.text.format.DateFormat
import com.example.myapplication.R
import java.util.Date

fun formatProductExpiry(context: Context, expiryDateMillis: Long?): String {
    if (expiryDateMillis == null) {
        return context.getString(R.string.product_expiry_none)
    }
    val formatted = DateFormat.getDateFormat(context).format(Date(expiryDateMillis))
    return context.getString(R.string.product_expiry_format, formatted)
}
