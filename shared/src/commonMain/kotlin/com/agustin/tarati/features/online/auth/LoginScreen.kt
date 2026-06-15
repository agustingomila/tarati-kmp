package com.agustin.tarati.features.online.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.guest_login
import com.agustin.tarati.shared.generated.resources.guest_login_description
import com.agustin.tarati.shared.generated.resources.guest_terms_link_privacy
import com.agustin.tarati.shared.generated.resources.guest_terms_link_rules
import com.agustin.tarati.shared.generated.resources.guest_terms_link_terms
import com.agustin.tarati.shared.generated.resources.guest_terms_prefix
import com.agustin.tarati.shared.generated.resources.guest_username_hint
import com.agustin.tarati.shared.generated.resources.guest_username_invalid
import com.agustin.tarati.shared.generated.resources.guest_username_label
import com.agustin.tarati.shared.generated.resources.forgot_password_back
import com.agustin.tarati.shared.generated.resources.forgot_password_email_label
import com.agustin.tarati.shared.generated.resources.forgot_password_link
import com.agustin.tarati.shared.generated.resources.forgot_password_send
import com.agustin.tarati.shared.generated.resources.forgot_password_sent
import com.agustin.tarati.shared.generated.resources.forgot_password_title
import com.agustin.tarati.shared.generated.resources.login_button
import com.agustin.tarati.shared.generated.resources.login_email
import com.agustin.tarati.shared.generated.resources.login_email_error
import com.agustin.tarati.shared.generated.resources.login_hide_password
import com.agustin.tarati.shared.generated.resources.login_password
import com.agustin.tarati.shared.generated.resources.login_password_error
import com.agustin.tarati.shared.generated.resources.login_remember_me
import com.agustin.tarati.shared.generated.resources.login_show_password
import com.agustin.tarati.shared.generated.resources.login_tab
import com.agustin.tarati.shared.generated.resources.login_title
import com.agustin.tarati.shared.generated.resources.login_username
import com.agustin.tarati.shared.generated.resources.login_username_error
import com.agustin.tarati.shared.generated.resources.or_separator
import com.agustin.tarati.shared.generated.resources.register_button
import com.agustin.tarati.shared.generated.resources.register_tab
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class LoginMode { LOGIN, REGISTER, FORGOT_PASSWORD }

@Composable
private fun GuestTermsText() {
    val uriHandler = LocalUriHandler.current
    val variantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val style = MaterialTheme.typography.bodySmall

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = localizedString(Res.string.guest_terms_prefix),
            style = style,
            color = variantColor,
            textAlign = TextAlign.Center,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = localizedString(Res.string.guest_terms_link_terms),
                style = style,
                color = primaryColor,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://tarati.tech/terms") }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
            Text(" · ", style = style, color = variantColor)
            Text(
                text = localizedString(Res.string.guest_terms_link_privacy),
                style = style,
                color = primaryColor,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://tarati.tech/privacy") }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
            Text(" · ", style = style, color = variantColor)
            Text(
                text = localizedString(Res.string.guest_terms_link_rules),
                style = style,
                color = primaryColor,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://tarati.tech/rules") }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSheet(
    onLoginSuccess: () -> Unit,
    onDismiss: () -> Unit,
    authViewModel: IAuthViewModel = koinInject(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LoginSheetContent(
            onLoginSuccess = onLoginSuccess,
            authViewModel = authViewModel,
        )
    }
}

@Composable
private fun LoginSheetContent(
    onLoginSuccess: () -> Unit,
    authViewModel: IAuthViewModel = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val authState by authViewModel.authState.collectAsState()
    val focusManager = LocalFocusManager.current

    var mode by remember { mutableStateOf(LoginMode.LOGIN) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var guestUsername by remember { mutableStateOf("") }
    var guestUsernameError by remember { mutableStateOf<String?>(null) }
    var guestServerError by remember { mutableStateOf<String?>(null) }

    var forgotEmail by remember { mutableStateOf("") }
    var forgotEmailError by remember { mutableStateOf<String?>(null) }
    var forgotSent by remember { mutableStateOf(false) }
    var forgotLoading by remember { mutableStateOf(false) }

    val isLoading = authState is AuthState.Authenticating
    val serverError = (authState as? AuthState.Error)?.message

    val labelLogin = localizedString(Res.string.login_tab)
    val labelRegister = localizedString(Res.string.register_tab)
    val labelUsername = localizedString(Res.string.login_username)
    val labelEmail = localizedString(Res.string.login_email)
    val labelPassword = localizedString(Res.string.login_password)
    val labelShowPw = localizedString(Res.string.login_show_password)
    val labelHidePw = localizedString(Res.string.login_hide_password)
    val errUsername = localizedString(Res.string.login_username_error)
    val errEmail = localizedString(Res.string.login_email_error)
    val errPassword = localizedString(Res.string.login_password_error)
    val errGuestUsername = localizedString(Res.string.guest_username_invalid)
    val labelForgotLink = localizedString(Res.string.forgot_password_link)
    val labelForgotTitle = localizedString(Res.string.forgot_password_title)
    val labelForgotEmailLabel = localizedString(Res.string.forgot_password_email_label)
    val labelForgotSend = localizedString(Res.string.forgot_password_send)
    val labelForgotSent = localizedString(Res.string.forgot_password_sent)
    val labelForgotBack = localizedString(Res.string.forgot_password_back)

    // Cerrar el sheet solo cuando el usuario se autenticó como cuenta registrada (no como invitado).
    // Los invitados ya tienen authState = Authenticated, así que sin este guard el sheet
    // se cerraría instantáneamente al abrirse.
    LaunchedEffect(authState) {
        val state = authState
        if (state is AuthState.Authenticated && !state.userInfo.isGuest) {
            onLoginSuccess()
        }
    }

    // Clear server error when user types
    LaunchedEffect(username, email, password) {
        if (serverError != null) authViewModel.clearError()
    }

    // Clear guest errors when the guest username field changes
    LaunchedEffect(guestUsername) {
        guestUsernameError = null
        guestServerError = null
    }

    // Reset field errors when mode changes
    LaunchedEffect(mode) {
        usernameError = null
        emailError = null
        passwordError = null
        authViewModel.clearError()
    }

    fun validate(): Boolean {
        var valid = true
        usernameError = if (username.length < 3) {
            valid = false; errUsername
        } else null
        passwordError = if (password.length < 6) {
            valid = false; errPassword
        } else null
        if (mode == LoginMode.REGISTER) {
            emailError = if (!email.contains('@') || !email.contains('.')) {
                valid = false; errEmail
            } else null
        }
        return valid
    }

    fun submit() {
        if (!validate()) return
        focusManager.clearFocus()
        scope.launch {
            if (mode == LoginMode.LOGIN) {
                authViewModel.loginWithServer(username.trim(), password, rememberMe)
            } else {
                authViewModel.registerWithServer(username.trim(), email.trim(), password, rememberMe = rememberMe)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Forgot password form ──────────────────────────────────────
        AnimatedVisibility(visible = mode == LoginMode.FORGOT_PASSWORD) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = labelForgotTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(24.dp))

                if (!forgotSent) {
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it; forgotEmailError = null },
                        label = { Text(labelForgotEmailLabel) },
                        isError = forgotEmailError != null,
                        supportingText = forgotEmailError?.let { { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (!forgotEmail.contains('@') || !forgotEmail.contains('.')) {
                                forgotEmailError = errEmail
                                return@Button
                            }
                            forgotEmailError = null
                            focusManager.clearFocus()
                            scope.launch {
                                forgotLoading = true
                                authViewModel.forgotPassword(forgotEmail.trim())
                                forgotLoading = false
                                forgotSent = true
                            }
                        },
                        enabled = !forgotLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (forgotLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Text(labelForgotSend)
                        }
                    }
                } else {
                    Text(
                        text = labelForgotSent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = labelForgotBack,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { mode = LoginMode.LOGIN }
                        .padding(vertical = 4.dp),
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Login / Register tabs ─────────────────────────────────────
        AnimatedVisibility(visible = mode != LoginMode.FORGOT_PASSWORD) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
        Text(
            text = localizedString(Res.string.login_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(16.dp))

        PrimaryTabRow(selectedTabIndex = if (mode == LoginMode.REGISTER) 1 else 0) {
            Tab(
                selected = mode == LoginMode.LOGIN,
                onClick = { mode = LoginMode.LOGIN },
                text = { Text(labelLogin) },
            )
            Tab(
                selected = mode == LoginMode.REGISTER,
                onClick = { mode = LoginMode.REGISTER },
                text = { Text(labelRegister) },
            )
        }

        Spacer(Modifier.height(20.dp))

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; usernameError = null },
            label = { Text(labelUsername) },
            isError = usernameError != null,
            supportingText = usernameError?.let { { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = if (mode == LoginMode.REGISTER) ImeAction.Next else ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier.fillMaxWidth(),
        )

        // Email (register only)
        AnimatedVisibility(visible = mode == LoginMode.REGISTER) {
            Column {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    label = { Text(labelEmail) },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text(labelPassword) },
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it) } },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) TaratiIcons.VisibilityOff else TaratiIcons.Visibility,
                        contentDescription = if (showPassword) labelHidePw else labelShowPw,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            modifier = Modifier.fillMaxWidth(),
        )

        // Link "Forgot password?" — solo en modo LOGIN, debajo del campo de contraseña
        AnimatedVisibility(visible = mode == LoginMode.LOGIN) {
            Text(
                text = labelForgotLink,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        forgotEmail = ""
                        forgotEmailError = null
                        forgotSent = false
                        mode = LoginMode.FORGOT_PASSWORD
                    }
                    .padding(top = 6.dp, bottom = 2.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

        // Server error
        AnimatedVisibility(visible = serverError != null) {
            Text(
                text = serverError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
            )
            Text(
                text = localizedString(Res.string.login_remember_me),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = { submit() },
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
                Text(
                    text = localizedString(
                        if (mode == LoginMode.LOGIN) Res.string.login_button
                        else Res.string.register_button
                    )
                )
            }
        }

        // ── Separador + sección guest ──────────────────────────────
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  ${localizedString(Res.string.or_separator)}  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = guestUsername,
            onValueChange = { guestUsername = it },
            label = { Text(localizedString(Res.string.guest_username_label)) },
            placeholder = {
                Text(
                    localizedString(Res.string.guest_username_hint),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            isError = guestUsernameError != null,
            supportingText = guestUsernameError?.let { { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val name = guestUsername.trim()
                if (name.isNotBlank()) {
                    if (name.length < 3 || name.length > 20 || !name.matches(Regex("[A-Za-z0-9_]+"))) {
                        guestUsernameError = errGuestUsername
                        return@OutlinedButton
                    }
                }
                focusManager.clearFocus()
                guestServerError = null
                scope.launch {
                    val result = authViewModel.loginAsGuest(name.takeIf { it.isNotBlank() })
                    if (result.isFailure) {
                        guestServerError = result.exceptionOrNull()?.message
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(localizedString(Res.string.guest_login))
        }

        AnimatedVisibility(visible = guestServerError != null) {
            Text(
                text = guestServerError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = localizedString(Res.string.guest_login_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(4.dp))

        GuestTermsText()

            } // end Column (LOGIN/REGISTER)
        } // end AnimatedVisibility

        Spacer(Modifier.height(32.dp))
    }
}
