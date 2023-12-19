package com.example.storyapp_ade.advanced_ui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import com.example.storyapp_ade.R
import com.google.android.material.textfield.TextInputEditText

class MyEditText : TextInputEditText {

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

        when (inputType) {
            EMAIL -> {
                hint = "Email"
                textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            }

            PASSWORD -> {
                hint = "Password"
                textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            }
        }
    }

    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = p0.toString()
                when (inputType) {
                    EMAIL -> {
                        if (!isValidEmail(p0.toString())) {
                            isValid = false
                            error = context.getString(R.string.wrong_email_popup)
                        } else {
                            isValid = true
                            error = null
                        }
                    }

                    PASSWORD -> {
                        if (input.length < 8) {
                            setError(context.getString(R.string.wrong_pass_popup), null)
                        } else {
                            error = null
                        }
                    }

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {

        val emailRegex =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|org|net|edu|gov|mil|int|aero|coop|museum|name|biz|info|pro|asia|mobi|tel|jobs|travel|[a-zA-Z]{2})$"
        return email.matches(emailRegex.toRegex())
    }


    companion object {
        const val EMAIL = 0x00000021
        const val PASSWORD = 0x00000081
        var isValid: Boolean = false
    }
}
