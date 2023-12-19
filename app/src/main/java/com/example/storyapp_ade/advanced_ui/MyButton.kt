package com.example.storyapp_ade.advanced_ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import android.view.Gravity
import com.example.storyapp_ade.R


class MyButton : AppCompatButton {
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable

    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground

        setTextColor(txtColor)

        textSize = 18f
        gravity = Gravity.CENTER

        text =
            if (isEnabled) resources.getString(R.string.sip) else resources.getString(R.string.isiDulu)
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
    }
}