package com.darkminstrel.weatherradar.ui

import android.content.Context
import android.util.AttributeSet
import com.jsibbold.zoomage.ZoomageView

class PortraitZoomageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ZoomageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }

}