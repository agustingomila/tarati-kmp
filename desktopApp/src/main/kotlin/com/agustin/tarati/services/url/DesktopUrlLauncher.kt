package com.agustin.tarati.services.url

import java.awt.Desktop
import java.net.URI

class DesktopUrlLauncher : IUrlLauncher {
    override fun openUrl(url: String) {
        runCatching {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(url))
            }
        }
    }
}
