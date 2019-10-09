package com.darkminstrel.weatherradar.ui.act_main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.data.TimedBitmap
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewHolderSlideshow(fab: FloatingActionButton, private val slideProgress:ProgressBar, private val imageEater:(bitmap:Bitmap)->Unit) {

    private val vhFab = ViewHolderFab(fab).apply {
        setPlaying(false)
        hide(false)
    }
    private var slideshowHandler:SlideshowHandler? = null
    private var progressAnimator: AnimatorSet? = null

    fun setSlideshow(slideshow:List<TimedBitmap>?){
        slideshowHandler?.stop()
        slideshowHandler = null
        animateProgressbar(null)
        if(slideshow==null) {
            vhFab.hide(true)
            vhFab.setOnClickListener(null)
        } else {
            vhFab.show(true)
            vhFab.setPlaying(false)
            vhFab.setOnClickListener{
                if(slideshowHandler!=null){
                    slideshowHandler?.stop()
                    slideshowHandler = null
                    vhFab.setPlaying(false)
                }else{
                    slideshowHandler = SlideshowHandler(slideshow){
                        slideshowHandler = null
                        vhFab.setPlaying(false)
                    }.apply{ start() }
                    vhFab.setPlaying(true)
                }
            }
        }
    }

    private inner class SlideshowHandler(private val slideshow:List<TimedBitmap>, private val onFinishListener:()->Unit) {
        private val handler = Handler()
        private var index = 0
        fun start(){
            runnable.run()
            animateProgressbar((slideshow.size) * Config.SLIDESHOW_ANIMATION_PERIOD_MS)
        }
        fun stop(){
            imageEater.invoke(slideshow.last().bitmap)
            animateProgressbar(null)
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

    private fun animateProgressbar(duration:Long?){
        progressAnimator?.cancel()
        if(duration!=null){
            if(slideProgress.max!=1000) slideProgress.max = 1000
            slideProgress.visibility = View.VISIBLE
            slideProgress.alpha = 1f
            val animProgress = ObjectAnimator.ofInt(slideProgress, "progress", 0, 1000).setDuration(duration)
            val animFade = ObjectAnimator.ofFloat(slideProgress, "alpha", 1f, 0f).setDuration(Config.SLIDESHOW_ANIMATION_PERIOD_MS)

            progressAnimator = AnimatorSet().apply {
                playSequentially(animProgress, animFade)
                start()
            }
        }else{
            slideProgress.visibility = View.INVISIBLE
            progressAnimator = null
        }
    }

}