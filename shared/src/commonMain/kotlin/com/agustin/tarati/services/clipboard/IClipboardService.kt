package com.agustin.tarati.services.clipboard

/**
 * Interfaz de servicio de clipboard multiplataforma.
 *
 * Implementaciones:
 * - [ClipboardServiceImpl] en androidApp (usa ClipboardManager Android)
 * - [DesktopClipboardService] en desktopApp (usa java.awt.Toolkit)
 */
interface IClipboardService {
    suspend fun copyText(
        label: String,
        text: String,
    ): Boolean

    suspend fun getText(): String?
}