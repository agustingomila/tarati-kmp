package com.agustin.tarati.features.online.supporter

import androidx.compose.runtime.Composable

/** Web: sin compra nativa — el supporter se paga por Stripe. */
@Composable
actual fun rememberSupporterPurchaseAction(): (() -> Unit)? = null
