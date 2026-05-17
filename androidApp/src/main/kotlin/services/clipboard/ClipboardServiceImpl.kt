package com.agustin.tarati.services.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClipboardServiceImpl(
    private val context: Context,
) : IClipboardService {
    override suspend fun copyText(
        label: String,
        text: String,
    ): Boolean =
        withContext(Dispatchers.Main) {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, text)
                clipboard.setPrimaryClip(clip)
                true
            } catch (_: Exception) {
                false
            }
        }

    override suspend fun getText(): String? =
        withContext(Dispatchers.Main) {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.primaryClip
                    ?.getItemAt(0)
                    ?.text
                    ?.toString()
            } catch (_: Exception) {
                null
            }
        }
}
