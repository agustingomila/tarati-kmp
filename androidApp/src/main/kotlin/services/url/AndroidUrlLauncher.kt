package com.agustin.tarati.services.url

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

class AndroidUrlLauncher(private val context: Context) : IUrlLauncher {
    override fun openUrl(url: String) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
