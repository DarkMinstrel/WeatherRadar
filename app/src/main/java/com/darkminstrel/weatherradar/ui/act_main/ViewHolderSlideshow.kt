package com.darkminstrel.weatherradar.ui.act_main

import android.graphics.Bitmap
import android.os.Handler
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.darkminstrel.weatherradar.ui.FabProgress

@Suppress("SameParameterValue")
class ViewHolderSlideshow(private val fabProgress: FabProgress, private val imageEater:(bitmap:Bitmap)->Unit) {

    private var appBarCollapsed = false
    private var slideshow:List<TimedBitmap>? = null
    private var slideshowHandler:SlideshowHandler? = null
    init {
        hideFab(false)
    }

    fun setSlideshow(slideshow:List<TimedBitmap>?){
        this.slideshow = slideshow
        updateFabVisibility()

        slideshowHandler?.stop()
        slideshowHandler = null
        if(slideshow==null) {
            fabProgress.setOnClickListener(null)
        } else {
            fabProgress.stop()
            fabProgress.setOnClickListener{
                if(slideshowHandler!=null){
                    slideshowHandler?.stop()
                    slideshowHandler = null
                    fabProgress.stop()
                }else{
                    slideshowHandler = SlideshowHandler(slideshow){
                        slideshowHandler = null
                    }.apply{ start() }
                }
            }
        }
    }

    private inner class SlideshowHandler(private val slideshow:List<TimedBitmap>, private val onFinishListener:()->Unit) {
        private val handler = Handler()
        private var index = 0
        fun start(){
            runnable.run()
            fabProgress.play((slideshow.size) * Config.SLIDESHOW_ANIMATION_PERIOD_MS)
        }
        fun stop(){
            imageEater.invoke(slideshow.last().bitmap)
            fabProgress.stop()
            handler.removeCallbacks(runnable)
        }
        private val runnable = object:Runnable {
            override fun run() {
                val bitmap = slideshow[index++].bitmap
                imageEater.invoke(bitmap)
                if(index < slideshow.size){
                    handler.postDelayed(this, Config.SLIDESHOW_ANIMATION_PERIOD_MS)
                }else{
                    handler.removeCallbacks(this)
                    onFinishListener.invoke()
                }
            }
        }
    }

    fun setAppBarCollapsed(collapsed:Boolean){
        this.appBarCollapsed = collapsed
        updateFabVisibility()
    }

    private val accelerateInterpolator = AccelerateInterpolator()
    private val overshootInterpolator = OvershootInterpolator()
    private var oldFabVisibility = false

    private fun updateFabVisibility(){
        val newFabVisibility = slideshow!=null && !appBarCollapsed
        if(slideshow==null) {
            hideFab(false)
        } else if(oldFabVisibility!=newFabVisibility) {
            if(newFabVisibility) showFab(true) else hideFab(true)
        }
        oldFabVisibility = newFabVisibility
    }

    private fun hideFab(animate:Boolean){
        fabProgress.animate().cancel();
        if(animate){
            fabProgress.animate().scaleX(0f).scaleY(0f).setInterpolator(accelerateInterpolator).setDuration(200).start()
        }else{
            fabProgress.scaleX = 0f
            fabProgress.scaleY = 0f
        }
    }
    private fun showFab(animate:Boolean){
        fabProgress.animate().cancel();
        if(animate){
            fabProgress.animate().scaleX(1f).scaleY(1f).setInterpolator(overshootInterpolator).setDuration(400).start()
        }else{
            fabProgress.scaleX = 1f
            fabProgress.scaleY = 1f
        }
    }

}