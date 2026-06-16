package com.agustin.tarati.features.online.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.auth_hide_password
import com.agustin.tarati.shared.generated.resources.auth_login_tab
import com.agustin.tarati.shared.generated.resources.auth_reset_button
import com.agustin.tarati.shared.generated.resources.auth_reset_confirm
import com.agustin.tarati.shared.generated.resources.auth_reset_field
import com.agustin.tarati.shared.generated.resources.auth_reset_min_length
import com.agustin.tarati.shared.generated.resources.auth_reset_mismatch
import com.agustin.tarati.shared.generated.resources.auth_reset_success
import com.agustin.tarati.shared.generated.resources.auth_reset_title
import com.agustin.tarati.shared.generated.resources.auth_show_password
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Sheet para restablecer contraseña usando el [token] del email de recuperación.
 *
 * Mostrado por el webApp (WASM) cuando la URL contiene `/reset-password?token=xxx`.
 * El sheet no puede descartarse sin completar el flujo — el usuario puede
 * actualizar la contraseña o ignorar el tab/ventana.
 *
 * @param token   UUID del reset token extraído de la URL.
 * @param onDone  Callback invocado cuando la contraseña se actualizó correctamente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordSheet(
    token: String,
    onDone: () -> Unit,
    authViewModel: IAuthViewModel = koinInject(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { /* No permite descartar — el token es de un solo uso */ },
        sheetState = sheetState,
        dragHandle = null,
    ) {
        ResetPasswordContent(
            token = token,
            onDone = onDone,
            authViewModel = authViewModel,
        )
    }
}

@Composable
private fun ResetPasswordContent(
    token: String,
    onDone: () -> Unit,
    authViewModel: IAuthViewModel,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }

    val labelTitle = localizedString(Res.string.auth_reset_title)
    val labelField = localizedString(Res.string.auth_reset_field)
    val labelConfirm = localizedString(Res.string.auth_reset_confirm)
    val labelButton = localizedString(Res.string.auth_reset_button)
    val labelSuccess = localizedString(Res.string.auth_reset_success)
    val errMinLength = localizedString(Res.string.auth_reset_min_length)
    val errMismatch = localizedString(Res.string.auth_reset_mismatch)
    val labelShow = localizedString(Res.string.auth_show_password)
    val labelHide = localizedString(Res.string.auth_hide_password)

    fun validate(): Boolean {
        var valid = true
        passwordError = if (password.length < 8) {
            valid = false; errMinLength
        } else null
        confirmError = if (password != confirm) {
            valid = false; errMismatch
        } else null
        return valid
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
            .padding(top = 32.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = labelTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(24.dp))

        if (success) {
            Text(
                text = labelSuccess,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(localizedString(Res.string.auth_login_tab))
            }
        } else {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text(labelField) },
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it) } },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) TaratiIcons.VisibilityOff else TaratiIcons.Visibility,
                            contentDescription = if (showPassword) labelHide else labelShow,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it; confirmError = null },
                label = { Text(labelConfirm) },
                isError = confirmError != null,
                supportingText = confirmError?.let { { Text(it) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (validate()) {
                        scope.launch {
                            isLoading = true
                            serverError = null
                            val result = authViewModel.resetPassword(token, password)
                            isLoading = false
                            if (result.isSuccess) success = true
                            else serverError = result.exceptionOrNull()?.message ?: "Reset failed"
                        }
                    }
                }),
                modifier = Modifier.fillMaxWidth(),
            )

            AnimatedVisibility(visible = serverError != null) {
                Text(
                    text = serverError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button
                    focusManager.clearFocus()
                    scope.launch {
                        isLoading = true
                        serverError = null
                        val result = authViewModel.resetPassword(token, password)
                        isLoading = false
                        if (result.isSuccess) success = true
                        else serverError = result.exceptionOrNull()?.message ?: "Reset failed"
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(labelButton)
                }
            }
        }
    }
}
