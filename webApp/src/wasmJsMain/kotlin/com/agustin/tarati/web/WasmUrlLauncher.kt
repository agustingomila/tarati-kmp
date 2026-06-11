@file:OptIn(ExperimentalWasmJsInterop::class)

package com.agustin.tarati.web

import com.agustin.tarati.services.url.IUrlLauncher
import kotlinx.browser.window

class WasmUrlLauncher : IUrlLauncher {
    override fun openUrl(url: String) {
        window.open(url, "_blank")
    }
}
