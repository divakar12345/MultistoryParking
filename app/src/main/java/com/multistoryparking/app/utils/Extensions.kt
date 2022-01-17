package com.multistoryparking.app.utils

import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/* To Non-Null */

fun String?.nonNull() = this ?: orEmpty()

fun Editable?.nonNull() = this ?: ""

fun Int?.nonNull() = this ?: 0

fun Double?.nonNull() = this ?: 0.0

/* Observable to Normal */

fun ObservableField<String>.toNormal() = get().nonNull().trim()

fun ObservableBoolean.toNormal() = get()

/* To Normal */

fun TextInputEditText.toNormal() = text.nonNull().toString().trim()

fun Spinner.setItemSelectedListener(
    nothingSelected: (parent: AdapterView<*>, selected: String) -> Unit = { _, _ -> },
    itemSelected: (parent: AdapterView<*>, view: View?, position: Int, id: Long, selected: String) -> Unit = { _, _, _, _, _ -> },
    selected: (position: Int, selected: String) -> Unit = { _, _ -> },
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            parent?.let {
                nothingSelected(
                    it,
                    it.getItemAtPosition(it.selectedItemPosition).toString()
                )
                selected(
                    it.selectedItemPosition,
                    it.getItemAtPosition(it.selectedItemPosition).toString()
                )
            }
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parent?.let {
                itemSelected(
                    it,
                    view,
                    position,
                    id,
                    it.getItemAtPosition(position).toString()
                )
                selected(position, it.getItemAtPosition(position).toString())
            }
        }
    }
}

@Throws(IOException::class)
private fun InputStream.copyTo(dst: File) {
    val out = FileOutputStream(dst)
    val buf = ByteArray(1024)
    var len: Int
    while (read(buf).let { len = it; it > 0 })
        out.write(buf, 0, len)
    close()
    out.close()
}