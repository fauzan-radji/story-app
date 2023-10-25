package com.fauzan.storytelling.components

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import com.fauzan.storytelling.R
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText : TextInputEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) { return }
                if (s.toString().length >= 8) { return }

                error = context.getString(R.string.password_must_be_at_least_8_characters)
            }
            override fun afterTextChanged(s: android.text.Editable?) { }
        })
    }
}