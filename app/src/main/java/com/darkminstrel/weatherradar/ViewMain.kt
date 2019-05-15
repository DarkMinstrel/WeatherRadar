package com.darkminstrel.weatherradar

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView

class ViewMain(activity: View) {
    private val ivRadar = activity.findViewById<ImageView>(R.id.iv_radar)
    private val progress = activity.findViewById<View>(R.id.progress)

    fun setProgress(){
        progress.visibility = View.VISIBLE
        ivRadar.visibility = View.INVISIBLE
    }

    fun setImage(bitmap: Bitmap){
        progress.visibility = View.GONE
        ivRadar.visibility = View.VISIBLE
        ivRadar.setImageBitmap(bitmap)
    }
}