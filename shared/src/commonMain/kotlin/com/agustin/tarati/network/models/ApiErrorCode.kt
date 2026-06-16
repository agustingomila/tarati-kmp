package com.agustin.tarati.network.models

import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.api_error_account_banned
import com.agustin.tarati.shared.generated.resources.api_error_email_taken
import com.agustin.tarati.shared.generated.resources.api_error_internal_error
import com.agustin.tarati.shared.generated.resources.api_error_invalid_credentials
import com.agustin.tarati.shared.generated.resources.api_error_invalid_request
import com.agustin.tarati.shared.generated.resources.api_error_invalid_username
import com.agustin.tarati.shared.generated.resources.api_error_rate_limited
import com.agustin.tarati.shared.generated.resources.api_error_reset_token_invalid
import com.agustin.tarati.shared.generated.resources.api_error_unknown
import com.agustin.tarati.shared.generated.resources.api_error_user_not_found
import com.agustin.tarati.shared.generated.resources.api_error_username_taken
import org.jetbrains.compose.resources.getString

/** Códigos de error canónicos devueltos por el servidor en el campo `code` del JSON de error. */
object ApiErrorCode {
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val USERNAME_TAKEN = "USERNAME_TAKEN"
    const val EMAIL_TAKEN = "EMAIL_TAKEN"
    const val ACCOUNT_BANNED = "ACCOUNT_BANNED"
    const val RATE_LIMITED = "RATE_LIMITED"
    const val INVALID_RESET_TOKEN = "INVALID_RESET_TOKEN"
    const val INVALID_USERNAME = "INVALID_USERNAME"
    const val INVALID_REQUEST = "INVALID_REQUEST"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val USER_NOT_FOUND = "USER_NOT_FOUND"
}

/**
 * Mapea un código de error del servidor a un string localizado.
 *
 * Llamar desde contextos `suspend` (ViewModels, Repositories).
 * Para códigos desconocidos devuelve un mensaje genérico.
 */
suspend fun localizedApiError(code: String): String = when (code) {
    ApiErrorCode.INVALID_CREDENTIALS -> getString(Res.string.api_error_invalid_credentials)
    ApiErrorCode.USERNAME_TAKEN -> getString(Res.string.api_error_username_taken)
    ApiErrorCode.EMAIL_TAKEN -> getString(Res.string.api_error_email_taken)
    ApiErrorCode.ACCOUNT_BANNED -> getString(Res.string.api_error_account_banned)
    ApiErrorCode.RATE_LIMITED -> getString(Res.string.api_error_rate_limited)
    ApiErrorCode.INVALID_RESET_TOKEN -> getString(Res.string.api_error_reset_token_invalid)
    ApiErrorCode.INVALID_USERNAME -> getString(Res.string.api_error_invalid_username)
    ApiErrorCode.INVALID_REQUEST -> getString(Res.string.api_error_invalid_request)
    ApiErrorCode.INTERNAL_ERROR -> getString(Res.string.api_error_internal_error)
    ApiErrorCode.USER_NOT_FOUND -> getString(Res.string.api_error_user_not_found)
    else -> getString(Res.string.api_error_unknown)
}
