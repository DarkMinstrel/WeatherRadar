package com.darkminstrel.weatherradar.ui.act_main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.RadarException
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.data.WeatherType
import com.google.android.material.appbar.AppBarLayout

@SuppressLint("ClickableViewAccessibility")
class ActMainViewHolder(root: View) {
    private val ivRadar = root.findViewById<ImageView>(R.id.iv_radar)
    private val progress = root.findViewById<View>(R.id.progress)
    private val error = root.findViewById<TextView>(R.id.error)
    private val appBar:AppBarLayout? = root.findViewById(R.id.app_bar_layout)
    private val legendLand:View? = root.findViewById(R.id.legend_land)
    private val legendPort:View? = root.findViewById(R.id.legend_port)
    private val legendContainer = root.findViewById<ViewGroup>(R.id.legend_container)
    private val vhSlideshow = ViewHolderSlideshow(
        root.findViewById(R.id.fab),
        root.findViewById(R.id.fab_progress),
        this::setImage)

    init {
        //prevents touch conflict with collapsing toolbar
        ivRadar.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        appBar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            //DBG("appbar offs = $verticalOffset")
        })

        fillLegend()
    }

    fun setProgress(){
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        appBar?.visibility = View.INVISIBLE
        ivRadar.visibility = View.INVISIBLE
        legendLand?.visibility = View.INVISIBLE
        legendPort?.visibility = View.INVISIBLE
    }

    fun setError(t:Throwable){
        DBG(t)
        progress.visibility = View.GONE
        error.visibility = View.VISIBLE
        appBar?.visibility = View.INVISIBLE
        ivRadar.visibility = View.INVISIBLE
        legendLand?.visibility = View.INVISIBLE
        legendPort?.visibility = View.INVISIBLE
        error.setText(if(t is RadarException) R.string.radar_unavailable else R.string.network_error)
    }

    fun setImage(bitmap: Bitmap){
        val wasVisible = (ivRadar.visibility==View.VISIBLE)
        progress.visibility = View.GONE
        error.visibility = View.GONE
        appBar?.visibility = View.VISIBLE
        ivRadar.visibility = View.VISIBLE
        ivRadar.setImageBitmap(bitmap)
        legendLand?.visibility = View.VISIBLE
        legendPort?.visibility = View.VISIBLE
        if(!wasVisible) {
            ivRadar.alpha = 0f
            ivRadar.animate().alpha(1f).start()
        }
    }

    fun setSlideshow(slideshow:List<TimedBitmap>?){
        vhSlideshow.setSlideshow(slideshow)
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

}