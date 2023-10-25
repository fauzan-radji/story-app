package com.fauzan.storytelling.components

import com.fauzan.storytelling.R
import com.google.android.material.textfield.TextInputEditText

class EmailEditText : TextInputEditText {
    constructor(context: android.content.Context) : super(context) {
        init()
    }
    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) { return }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) { return }

                error = context.getString(R.string.email_is_invalid)
            }
            override fun afterTextChanged(s: android.text.Editable?) { }
        })
    }
}