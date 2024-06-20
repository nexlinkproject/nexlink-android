package com.nexlink.nexlinkmobileapp.view.utils

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import com.nexlink.nexlinkmobileapp.R

fun changeBackgroundColor(view: View, color: Int) {
    val layerDrawable = view.background as LayerDrawable
    val itemDrawable = layerDrawable.findDrawableByLayerId(R.id.item_background) as GradientDrawable
    itemDrawable.setColor(color)
}