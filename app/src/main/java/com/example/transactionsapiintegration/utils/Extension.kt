package com.dictatenow.androidapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.security.MessageDigest
import java.util.Calendar
import java.util.UUID

object Extension {
    private var dialog: Dialog? = null

    /*  fun showProgress(context: Context) {
          dialog?.dismiss()
          dialog = Dialog(context)
          dialog!!.setContentView(R.layout.progress)
          dialog!!.setCancelable(false)
          dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
          dialog!!.show()
      }
  */
    fun stopProgress() {
        if (dialog != null) dialog!!.cancel()
    }

    fun trimEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.startsWith(" ")) {
                    editText.setText(s.trimStart())
                    editText.setSelection(editText.text.length)
                }
            }

        })
    }

   @SuppressLint("DefaultLocale")
    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay)
                onDateSelected(formattedDate)
            }, year, month, day)

        // Set minimum date to today to prevent past dates
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    @SuppressLint("DefaultLocale")
    fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            // Format the selected time as a string (e.g., HH:mm)
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    fun getDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return md5(androidId)
    }
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }
    fun convertToSeconds(time: String): Int {
        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
        return when (parts.size) {
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]  // HH:MM:SS
            2 -> parts[0] * 60 + parts[1]  // MM:SS (optional support)
            1 -> parts[0]  // SS
            else -> 0
        }
    }


}