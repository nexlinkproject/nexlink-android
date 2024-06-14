package com.nexlink.nexlinkmobileapp.view.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.nexlink.nexlinkmobileapp.R

class TextInputPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs) {
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString().length < 8) {
            setError(context.getString(R.string.len_password), null)
        } else {
            error = null
        }
    }
}