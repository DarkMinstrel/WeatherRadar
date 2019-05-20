package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.darkminstrel.weatherradar.data.WeatherType

class ViewMain(root: View) {
    private val ivRadar = root.findViewById<ImageView>(R.id.iv_radar)
    private val progress = root.findViewById<View>(R.id.progress)
    private val error = root.findViewById<View>(R.id.error)
    private val bottomSheet = root.findViewById<View>(R.id.bottom_sheet)
    private val bottomSheetShadow = root.findViewById<View>(R.id.shadow)
    private val bottomSheerContainer = bottomSheet.findViewById<ViewGroup>(R.id.bottom_sheet_container)

    init {
        fillLegend()
    }

    fun setProgress(){
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        ivRadar.visibility = View.INVISIBLE
        bottomSheet.visibility = View.INVISIBLE
        bottomSheetShadow.visibility = View.INVISIBLE
    }

    fun setError(t:Throwable){
        DBG(t)
        progress.visibility = View.GONE
        error.visibility = View.VISIBLE
        ivRadar.visibility = View.INVISIBLE
        bottomSheet.visibility = View.INVISIBLE
        bottomSheetShadow.visibility = View.INVISIBLE
    }

    fun setImage(bitmap: Bitmap){
        val wasVisible = (ivRadar.visibility==View.VISIBLE)
        progress.visibility = View.GONE
        error.visibility = View.GONE
        ivRadar.visibility = View.VISIBLE
        ivRadar.setImageBitmap(bitmap)
        bottomSheet.visibility = View.VISIBLE
        bottomSheetShadow.visibility = View.VISIBLE
        if(!wasVisible) {
            ivRadar.alpha = 0f
            ivRadar.animate().alpha(1f).start()
        }
    }

    private fun fillLegend(){
        bottomSheerContainer.removeAllViews()
        val inflater = LayoutInflater.from(bottomSheerContainer.context)
        for(type in WeatherType.values()){
            val view = inflater.inflate(R.layout.legend_item, bottomSheerContainer, false)
            bottomSheerContainer.addView(view)
            view.findViewById<TextView>(android.R.id.text1).setText(type.stringId)
            view.findViewById<View>(android.R.id.icon).setBackgroundColor(type.color)
        }
    }
}