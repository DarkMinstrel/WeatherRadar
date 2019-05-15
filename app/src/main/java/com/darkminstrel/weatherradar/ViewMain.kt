package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView

class ViewMain(root: View) {
    private val ivRadar = root.findViewById<ImageView>(R.id.iv_radar)
    private val progress = root.findViewById<View>(R.id.progress)
    private val error = root.findViewById<View>(R.id.error)

    fun setProgress(){
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        ivRadar.visibility = View.INVISIBLE
    }

    fun setError(t:Throwable){
        progress.visibility = View.GONE
        error.visibility = View.VISIBLE
        ivRadar.visibility = View.INVISIBLE
    }

    fun setImage(bitmap: Bitmap){
        progress.visibility = View.GONE
        error.visibility = View.GONE
        ivRadar.visibility = View.VISIBLE
        ivRadar.setImageBitmap(bitmap)
    }
}