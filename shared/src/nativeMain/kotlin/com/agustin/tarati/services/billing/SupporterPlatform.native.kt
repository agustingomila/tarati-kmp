package com.agustin.tarati.services.billing

/** iOS: el pago Supporter vía Stripe no aplica (políticas de la App Store). */
actual fun supporterStripeAvailable(): Boolean = false
