package com.darkminstrel.weatherradar.ui.act_main

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.darkminstrel.weatherradar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewHolderFab(private val fab: FloatingActionButton) {

    private val accelerateInterpolator = AccelerateInterpolator()
    private val overshootInterpolator = OvershootInterpolator()

    private var isPlaying:Boolean
    init {
        isPlaying = false
        fab.setImageResource(R.drawable.play_icon)
    }

    fun hide(animate:Boolean){
        fab.animate().cancel();
        if(animate){
            fab.animate().scaleX(0f).scaleY(0f).setInterpolator(accelerateInterpolator).start()
        }else{
            fab.scaleX = 0f
            fab.scaleY = 0f
        }
    }
    fun show(animate:Boolean){
        fab.animate().cancel();
        if(animate){
            fab.animate().scaleX(1f).scaleY(1f).setInterpolator(overshootInterpolator).start()
        }else{
            fab.scaleX = 1f
            fab.scaleY = 1f
        }
    }
    fun setOnClickListener(listener: (()->Unit)?) = this.fab.setOnClickListener{listener?.invoke()}

    fun setPlaying(playing:Boolean) {
        if(playing && !isPlaying){
            (fab.drawable as? AnimatedVectorDrawable)?.stop()
            val drawable = fab.context.getDrawable(R.drawable.play_to_stop_animation)
            fab.setImageDrawable(drawable)
            (drawable as AnimatedVectorDrawable).start()
        }else if(!playing && isPlaying){
            (fab.drawable as? AnimatedVectorDrawable)?.stop()
            val drawable = fab.context.getDrawable(R.drawable.stop_to_play_animation)
            fab.setImageDrawable(drawable)
            (drawable as AnimatedVectorDrawable).start()
        }
        isPlaying = playing
    }

}