package com.project.ecorangeestimater.utils

import android.app.Activity
import android.text.Spanned
import android.widget.Toast
import androidx.core.text.HtmlCompat

fun Activity.showToast(message:String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun spanned(text: String): Spanned {
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS)
}