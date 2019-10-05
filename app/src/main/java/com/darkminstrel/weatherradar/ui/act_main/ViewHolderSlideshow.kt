package com.darkminstrel.weatherradar.ui.act_main

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
    private var slideshowAnimator:Animator? = null
    private var progressAnimator: ObjectAnimator? = null

    fun setSlideshow(slideshow:List<TimedBitmap>?){
        slideshowAnimator?.stop()
        slideshowAnimator = null
        animateProgressbar(null)
        if(slideshow==null) {
            vhFab.hide(true)
            vhFab.setOnClickListener(null)
        } else {
            vhFab.show(true)
            vhFab.setPlaying(false)
            vhFab.setOnClickListener{
                if(slideshowAnimator!=null){
                    slideshowAnimator?.stop()
                    slideshowAnimator = null
                    vhFab.setPlaying(false)
                }else{
                    slideshowAnimator = Animator(slideshow){
                        slideshowAnimator = null
                        vhFab.setPlaying(false)
                    }.apply{ start() }
                    vhFab.setPlaying(true)
                }
            }
        }
    }

    private inner class Animator(private val slideshow:List<TimedBitmap>, private val onFinishListener:()->Unit) {
        private val handler = Handler()
        private var index = 0
        fun start(){
            runnable.run()
            animateProgressbar((slideshow.size) * Config.ANIMATION_PERIOD)
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
                    handler.postDelayed(this, Config.ANIMATION_PERIOD)
                }else{
                    handler.removeCallbacks(this)
                    onFinishListener.invoke()
                }
            }
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun animateProgressbar(duration:Long?){
        progressAnimator?.cancel()
        if(duration!=null){
            slideProgress.visibility = View.VISIBLE
            if(slideProgress.max!=1000) slideProgress.max = 1000
            slideProgress.setProgress(0)
            progressAnimator = ObjectAnimator.ofInt(slideProgress, "progress", 1000).setDuration(duration).apply { start() }
        }else{
            slideProgress.visibility = View.INVISIBLE
            progressAnimator = null
        }
    }

}