package com.darkminstrel.weatherradar.ui.act_main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.darkminstrel.weatherradar.DBG
import com.darkminstrel.weatherradar.R
import com.darkminstrel.weatherradar.RadarException
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.data.WeatherType
import com.darkminstrel.weatherradar.databinding.ActMainBinding
import com.darkminstrel.weatherradar.setTopDrawable
import com.google.android.material.appbar.AppBarLayout

@SuppressLint("ClickableViewAccessibility")
class ActMainViewHolder(private val binding: ActMainBinding, private val vm:ActMainViewModel) {
    private val vhSlideshow = ViewHolderSlideshow(
        binding.fabProgress,
        this::setImage)

    init {
        binding.apply {
            //prevents touch conflict with collapsing toolbar
            ivRadar.setOnTouchListener { v, _ ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            val collapseThreshold = root.resources.getDimensionPixelSize(R.dimen.circular_progress_radius)
            appBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                vhSlideshow.setAppBarCollapsed(verticalOffset < -collapseThreshold)
            })

            btnRefresh.setOnClickListener { vm.reload() }
        }

        fillLegend()
    }

    fun setProgress(){
        binding.apply{
            progress.visibility = View.VISIBLE
            error.visibility = View.GONE
            appBarLayout?.visibility = View.INVISIBLE
            ivRadar.visibility = View.INVISIBLE
            legendLand?.visibility = View.INVISIBLE
            legendPort?.visibility = View.INVISIBLE
        }
    }

    fun setError(t:Throwable){
        DBG(t)
        binding.apply{
            progress.visibility = View.GONE
            error.visibility = View.VISIBLE
            appBarLayout?.visibility = View.INVISIBLE
            ivRadar.visibility = View.INVISIBLE
            legendLand?.visibility = View.INVISIBLE
            legendPort?.visibility = View.INVISIBLE
            if(t is RadarException){
                tvError.setText(R.string.radar_unavailable)
                tvError.setTopDrawable(R.drawable.radar)
            }else{
                tvError.setText(R.string.network_error)
                tvError.setTopDrawable(R.drawable.dino)
            }
        }
    }

    fun setImage(bitmap: Bitmap){
        binding.apply {
            val wasVisible = (ivRadar.visibility == View.VISIBLE)
            progress.visibility = View.GONE
            error.visibility = View.GONE
            appBarLayout?.visibility = View.VISIBLE
            ivRadar.visibility = View.VISIBLE
            (ivRadar as ImageView).setImageBitmap(bitmap)
            legendLand?.visibility = View.VISIBLE
            legendPort?.visibility = View.VISIBLE
            if (!wasVisible) {
                ivRadar.alpha = 0f
                ivRadar.animate().alpha(1f).start()
            }
        }
    }

    fun setSlideshow(slideshow:List<TimedBitmap>?){
        vhSlideshow.setSlideshow(slideshow)
    }

    private fun fillLegend(){
        binding.legendContainer.also {
            it.removeAllViews()
            val inflater = LayoutInflater.from(it.context)
            for (type in WeatherType.values()) {
                val view = inflater.inflate(R.layout.legend_item, it, false)
                it.addView(view)
                view.findViewById<TextView>(android.R.id.text1).setText(type.stringId)
                view.findViewById<View>(android.R.id.icon).setBackgroundColor(type.color)
            }
        }
    }

}