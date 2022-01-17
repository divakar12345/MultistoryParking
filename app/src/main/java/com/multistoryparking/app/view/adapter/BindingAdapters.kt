package com.multistoryparking.app.view.adapter

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.multistoryparking.app.R

@BindingAdapter("isGone")
fun View.isGone(state: Boolean = true) {
    isGone = state
}

@BindingAdapter("imageRes")
fun ImageView.setImageRes(@DrawableRes res: Int) {
    Glide.with(context)
        .load(res)
        .error(
            ResourcesCompat.getDrawable(
                resources,
                R.mipmap.ic_launcher,
                resources.newTheme().apply { applyStyle(R.style.Base_App, true) })
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}