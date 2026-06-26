package com.agustin.tarati.features.online.supporter

import androidx.compose.runtime.Composable
import com.agustin.tarati.services.billing.IBillingManager
import com.agustin.tarati.services.billing.SUPPORTER_PRODUCT_ID
import org.koin.compose.koinInject

/** Android: lanza la compra del producto managed `supporter` en Google Play. */
@Composable
actual fun rememberSupporterPurchaseAction(): (() -> Unit)? {
    val billing = koinInject<IBillingManager>()
    return { billing.launchPurchaseFlow(SUPPORTER_PRODUCT_ID) }
}
