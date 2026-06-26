package com.agustin.tarati.services.billing

/** Android: la compra del Supporter va por Google Play (C2), no por Stripe. */
actual fun supporterStripeAvailable(): Boolean = false
