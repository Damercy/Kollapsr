package dev.dayaonweb.kollapsr

import android.content.res.Resources.getSystem

val Int.toPx: Int get() = (this * getSystem().displayMetrics.density).toInt()