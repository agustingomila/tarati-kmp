package com.agustin.tarati.services.billing

/**
 * Indica si el pago Supporter vía Stripe está disponible en esta plataforma.
 *
 * Stripe (pago externo de bienes digitales) está prohibido por las políticas de
 * Google Play y la App Store, así que el flujo de Checkout es solo Desktop + Web.
 * En Android la compra del Supporter va por Google Play (C2); en iOS quedará para
 * StoreKit. Cuando es false, la pantalla oculta el botón de checkout.
 */
expect fun supporterStripeAvailable(): Boolean
