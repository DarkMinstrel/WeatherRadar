package com.darkminstrel.weatherradar.ui.act_main

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.data.WeatherType
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ActMainViewHolder(root: View) {
    private val ivRadar = root.findViewById<ImageView>(R.id.iv_radar)
    private val progress = root.findViewById<View>(R.id.progress)
    private val error = root.findViewById<View>(R.id.error)
    private val legend = root.findViewById<View>(R.id.legend)
    private val legendContainer = root.findViewById<ViewGroup>(R.id.legend_container)
    private val bottomSheetShadow:View? = root.findViewById(R.id.shadow)

    init {
        fillLegend()
    }

    fun setProgress(){
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        ivRadar.visibility = View.INVISIBLE
        legend.visibility = View.INVISIBLE
        bottomSheetShadow?.visibility = View.INVISIBLE
    }

    fun setError(t:Throwable){
        DBG(t)
        progress.visibility = View.GONE
        error.visibility = View.VISIBLE
        ivRadar.visibility = View.INVISIBLE
        legend.visibility = View.INVISIBLE
        bottomSheetShadow?.visibility = View.INVISIBLE
    }

    fun setImage(bitmap: Bitmap){
        val wasVisible = (ivRadar.visibility==View.VISIBLE)
        progress.visibility = View.GONE
        error.visibility = View.GONE
        ivRadar.visibility = View.VISIBLE
        ivRadar.setImageBitmap(bitmap)
        legend.visibility = View.VISIBLE
        bottomSheetShadow?.visibility = View.VISIBLE
        if(!wasVisible) {
            ivRadar.alpha = 0f
            ivRadar.animate().alpha(1f).start()
        }
    }

    private fun fillLegend(){
        legendContainer.removeAllViews()
        val inflater = LayoutInflater.from(legendContainer.context)
        for(type in WeatherType.values()){
            val view = inflater.inflate(R.layout.legend_item, legendContainer, false)
            legendContainer.addView(view)
            view.findViewById<TextView>(android.R.id.text1).setText(type.stringId)
            view.findViewById<View>(android.R.id.icon).setBackgroundColor(type.color)
        }
    }

    fun expandLegend(){
        val behavior = BottomSheetBehavior.from(legend)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}