package com.darkminstrel.weatherradar.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.animation.addListener
import com.darkminstrel.weatherradar.Config
import com.darkminstrel.weatherradar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fab:FloatingActionButton
    private val progress:ProgressBar
    private var progressAnimator: AnimatorSet? = null
    private var isPlaying:Boolean? = null

    init {
        inflate(context, R.layout.fabprogress, this)
        fab = findViewById(R.id.fab)
        progress = findViewById(R.id.progress)
        setPlaying(false)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.fab.setOnClickListener(l)
    }

    private fun setPlaying(playing:Boolean) {
        if(playing && isPlaying!=true){
            (fab.drawable as? AnimatedVectorDrawable)?.stop()
            val drawable = context.getDrawable(R.drawable.play_to_stop_animation) as AnimatedVectorDrawable
            fab.setImageDrawable(drawable)
            drawable.start()
        }else if(!playing && isPlaying!=false){
            (fab.drawable as? AnimatedVectorDrawable)?.stop()
            val drawable = context.getDrawable(R.drawable.stop_to_play_animation) as AnimatedVectorDrawable
            fab.setImageDrawable(drawable)
            drawable.start()
        }
        isPlaying = playing
    }

    fun stop(){
        setPlaying(false)
        progressAnimator?.cancel()
        progress.visibility = View.INVISIBLE
        progressAnimator = null
    }

    fun play(duration:Long){
        setPlaying(true)
        progressAnimator?.cancel()
        if(progress.max!=1000) progress.max = 1000
        progress.visibility = View.VISIBLE
        progress.alpha = 1f
        val animProgress = ObjectAnimator.ofInt(progress, "progress", 0, 1000).setDuration(duration).apply{
            addListener(onEnd = {setPlaying(false)})}
        val animFade = ObjectAnimator.ofFloat(progress, "alpha", 1f, 0f).setDuration(Config.SLIDESHOW_ANIMATION_PERIOD_MS)

        progressAnimator = AnimatorSet().apply {
            playSequentially(animProgress, animFade)
            start()
        }
    }


}