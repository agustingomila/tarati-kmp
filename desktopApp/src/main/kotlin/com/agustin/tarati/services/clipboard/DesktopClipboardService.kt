package com.agustin.tarati.services.clipboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

/**
 * Implementación Desktop de IClipboardService usando java.awt.Toolkit.
 * Disponible en todas las plataformas JVM (Windows, macOS, Linux).
 */
class DesktopClipboardService : IClipboardService {

    override suspend fun copyText(label: String, text: String): Boolean {
        return try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(StringSelection(text), null)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getText(): String? {
        return try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            withContext(Dispatchers.IO) {
                clipboard.getData(DataFlavor.stringFlavor)
            } as? String
        } catch (e: Exception) {
            null
        }
    }
}