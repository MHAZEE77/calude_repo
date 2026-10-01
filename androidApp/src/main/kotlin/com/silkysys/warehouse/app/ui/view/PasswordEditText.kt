package com.silkysys.warehouse.app.ui.view


import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val visibleIcon: Drawable? =
        ContextCompat.getDrawable(context, com.silkysys.warehouse.R.drawable.ic_visibility_on)
    private val hiddenIcon: Drawable? =
        ContextCompat.getDrawable(context, com.silkysys.warehouse.R.drawable.ic_visibility_off)

    private var isPasswordVisible = false

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
        typeface = ResourcesCompat.getFont(context, com.silkysys.warehouse.R.font.regular)
        updateEndIcon()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawables[2]
            if (drawableEnd != null) {
                val touchAreaStart = width - paddingEnd - drawableEnd.bounds.width()
                if (event.x >= touchAreaStart) {
                    togglePasswordVisibility()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        val selection = selectionEnd

        transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        updateEndIcon()
        setSelection(selection.coerceAtMost(text?.length ?: 0))

        contentDescription = context.getString(
            if (isPasswordVisible) com.silkysys.warehouse.R.string.hide_password else com.silkysys.warehouse.R.string.show_password
        )
    }

    private fun updateEndIcon() {
        val icon = if (isPasswordVisible) hiddenIcon else visibleIcon
        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
    }
}