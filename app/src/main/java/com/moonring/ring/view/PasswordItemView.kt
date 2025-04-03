package com.moonring.ring.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.moonring.ring.R

class PasswordItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

private var textChangeListener: TextChangeListener? = null

    var showCloseEyeIcon = true
    init {
        LayoutInflater.from(context).inflate(R.layout.password_item_view, this, true)


        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PasswordItemView,
            0, 0).apply {
            try {
                val hint = getString(R.styleable.PasswordItemView_passwordHint)
                setPasswordHint(hint)

                showCloseEyeIcon = getBoolean(R.styleable.PasswordItemView_showCloseEyeIcon, true)
            } finally {
                recycle()
            }
        }

        initView()
    }


    fun setTextChangeListener(listener: TextChangeListener?) {
        this.textChangeListener = listener
    }
    interface TextChangeListener {
        fun afterTextChanged()
    }

    private fun initView() {
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val editCloseIcon: ImageView = findViewById(R.id.editCloseIcon)


        editCloseIcon.visibility = View.GONE


        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s?.length?:0 > 0) {
                    editCloseIcon.visibility = View.VISIBLE
                } else {
                    editCloseIcon.visibility = View.GONE
                }

                textChangeListener?.afterTextChanged()
            }
        })


        editCloseIcon.setOnClickListener {
            passwordInput.text.clear()
        }


        val closeEyeIcon: ImageView = findViewById(R.id.closeEyeIcon)

        closeEyeIcon.visibility = if (showCloseEyeIcon) View.VISIBLE else View.GONE
        passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
        closeEyeIcon.setImageResource(R.drawable.ic_close_eye)
        closeEyeIcon.tag = "close"



        closeEyeIcon.setOnClickListener {

            if (closeEyeIcon.tag == "close") {

                passwordInput.transformationMethod = null
                closeEyeIcon.setImageResource(R.drawable.ic_open_eye)
                closeEyeIcon.tag = "open"
            } else {

                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                closeEyeIcon.setImageResource(R.drawable.ic_close_eye)
                closeEyeIcon.tag = "close"
            }

            passwordInput.requestFocus()

            passwordInput.setSelection(passwordInput.text.length)
        }

    }

    private fun setPasswordHint(hint: String?) {
        hint?.let {
            val passwordInput: EditText = findViewById(R.id.passwordInput)
            passwordInput.hint = it
        }
    }
    fun setHint(hint: String) {
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        passwordInput.hint = hint
    }

    fun setPasswordIcon(icon: Drawable) {
        val passwordIcon: ImageView = findViewById(R.id.passwordIcon)
        passwordIcon.setImageDrawable(icon)
    }

    fun getPasswordText(): String {
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        return passwordInput.text.toString()
    }


}
