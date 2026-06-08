package com.agustin.tarati.features.online.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
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
import com.agustin.tarati.shared.generated.resources.register_button
import com.agustin.tarati.shared.generated.resources.register_tab
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.TaratiIcons
import com.agustin.tarati.ui.theme.TaratiLogo
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class LoginMode { LOGIN, REGISTER }

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
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

    // Navigate out on successful auth
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onLoginSuccess()
    }

    // Clear server error when user types
    LaunchedEffect(username, email, password) {
        if (serverError != null) authViewModel.clearError()
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

    TaratiBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                TaratiLogo(modifier = Modifier.size(80.dp))

                Spacer(Modifier.height(8.dp))

                Text(
                    text = localizedString(Res.string.login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.height(24.dp))

                PrimaryTabRow(selectedTabIndex = mode.ordinal) {
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

                Spacer(Modifier.height(32.dp))
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            ) {
                Icon(
                    imageVector = TaratiIcons.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
