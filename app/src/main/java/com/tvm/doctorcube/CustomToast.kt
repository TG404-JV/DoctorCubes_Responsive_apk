package com.tvm.doctorcube

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.tvm.doctorcube.databinding.CustomToastBinding

object CustomToast {

    /**
     * Displays a custom toast message using ViewBinding.
     *
     * @param activity The activity context.
     * @param message  The text to display in the toast.
     */
    @JvmStatic
    fun showToast(activity: Activity, message: String?) {
        if (message.isNullOrBlank()) return

        // Inflate custom_toast.xml using ViewBinding
        val binding = CustomToastBinding.inflate(LayoutInflater.from(activity))

        // Set values
        binding.toastIcon.setImageResource(R.drawable.logo_doctor_cubes_dynamic)
        binding.toastText.text = message

        // Create and show toast
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)

        @Suppress("DEPRECATION")
        toast.view = binding.root // Safe usage with view binding

        toast.show()
    }
}
