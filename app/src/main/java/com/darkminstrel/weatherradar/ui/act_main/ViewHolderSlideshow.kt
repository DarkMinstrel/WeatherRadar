package com.darkminstrel.weatherradar.ui.act_main

import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.data.TimedBitmap

class ViewHolderSlideshow(private val fab: ViewHolderFab, private val slideProgress:ProgressBar, private val vh:ActMainViewHolder) {

    private var slideshowAnimator:Animator? = null
    private var progressAnimator: ObjectAnimator? = null

    fun init(){
        fab.hide(false)
        fab.setOnClickListener(null)
        fab.setIdle()
    }

    fun setSlideshow(slideshow:List<TimedBitmap>?){
        slideshowAnimator?.stop()
        slideshowAnimator = null
        animateProgress(null)
        if(slideshow==null) {
            fab.hide(true)
            fab.setOnClickListener(null)
        } else {
            fab.show(true)
            fab.setIdle()
            fab.setOnClickListener{
                if(slideshowAnimator!=null){
                    slideshowAnimator?.stop()
                    slideshowAnimator = null
                    fab.setIdle()
                }else{
                    slideshowAnimator = Animator(slideshow){
                        slideshowAnimator = null
                        fab.setIdle()
                    }.apply{ start() }
                    fab.setPlaying()
                }
            }
        }
    }

    private inner class Animator(private val slideshow:List<TimedBitmap>, private val onFinishListener:()->Unit) {
        private val handler = Handler()
        private var index = 0
        fun start(){
            runnable.run()
            animateProgress((slideshow.size) * Config.ANIMATION_PERIOD)
        }
        fun stop(){
            vh.setImage(slideshow.last().bitmap)
            animateProgress(null)
            handler.removeCallbacks(runnable)
        }
        private val runnable = object:Runnable {
            override fun run() {
                val bitmap = slideshow[index++].bitmap
                vh.setImage(bitmap)
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
    private fun animateProgress(duration:Long?){
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