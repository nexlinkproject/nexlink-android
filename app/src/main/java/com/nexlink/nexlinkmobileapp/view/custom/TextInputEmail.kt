package com.nexlink.nexlinkmobileapp.view.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class TextInputEmail @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs) {
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!isValidEmail(s.toString())) {
            setError("Email tidak valid", null)
        } else {
            error = null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        return email.matches(emailRegex.toRegex(RegexOption.IGNORE_CASE))
    }
}