package com.darkminstrel.weatherradar.ui.act_main

import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.ViewModel
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewHolderFab(private val fab: FloatingMusicActionButton) {

    private val accelerateInterpolator = AccelerateInterpolator()
    private val overshootInterpolator = OvershootInterpolator()

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

    fun setIdle() = fab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_STOP)

    fun setPlaying() = fab.changeMode(FloatingMusicActionButton.Mode.STOP_TO_PLAY)

}