package com.agustin.tarati.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.services.localization.LocalAppLanguage
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.delete
import com.agustin.tarati.shared.generated.resources.profile_accept_challenges
import com.agustin.tarati.shared.generated.resources.profile_bio
import com.agustin.tarati.shared.generated.resources.profile_bio_placeholder
import com.agustin.tarati.shared.generated.resources.profile_visible_online
import com.agustin.tarati.shared.generated.resources.save
import com.agustin.tarati.shared.generated.resources.settings_delete_account
import com.agustin.tarati.shared.generated.resources.settings_delete_account_body
import com.agustin.tarati.shared.generated.resources.settings_delete_account_title
import com.agustin.tarati.shared.generated.resources.settings_online
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineSettingsScreen(
    authViewModel: IAuthViewModel = koinInject(),
    onNavigateBack: () -> Unit = {},
) {
    val currentLanguage = LocalAppLanguage.current
    key(currentLanguage) {
        OnlineSettingsContent(authViewModel = authViewModel, onNavigateBack = onNavigateBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnlineSettingsContent(
    authViewModel: IAuthViewModel,
    onNavigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val profileData by authViewModel.profileData.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(localizedString(Res.string.settings_delete_account_title)) },
            text = { Text(localizedString(Res.string.settings_delete_account_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch { authViewModel.deleteAccount() }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text(localizedString(Res.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(localizedString(Res.string.cancel))
                }
            },
        )
    }

    LaunchedEffect(Unit) {
        authViewModel.fetchProfile()
    }

    // Estado local optimista: los toggles responden inmediatamente.
    // remember(key) sincroniza con el servidor cuando profileData cambia.
    var localIsVisible by remember(profileData?.isVisible) {
        mutableStateOf(profileData?.isVisible ?: true)
    }
    var localChallengesEnabled by remember(profileData?.challengesEnabled) {
        mutableStateOf(profileData?.challengesEnabled ?: true)
    }

    // Bio desacoplada del key de remember para no pisar ediciones en curso.
    // bioEditing previene que una actualización de profileData (p. ej. al guardar
    // un toggle) sobreescriba lo que el usuario está escribiendo.
    var bioText by remember { mutableStateOf(profileData?.bio ?: "") }
    var bioEditing by remember { mutableStateOf(false) }
    LaunchedEffect(profileData?.bio) {
        if (!bioEditing) bioText = profileData?.bio ?: ""
    }

    Scaffold(
        topBar = {
            TaratiTopBar(
                title = localizedString(Res.string.settings_online),
                navigationType = TopBarNavigationType.Back,
                onNavigationClick = onNavigateBack,
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    Text(
                        text = localizedString(Res.string.profile_bio),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    TextField(
                        value = bioText,
                        onValueChange = {
                            if (it.length <= 200) {
                                bioText = it; bioEditing = true
                            }
                        },
                        placeholder = {
                            Text(
                                localizedString(Res.string.profile_bio_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                    )
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { scope.launch { authViewModel.updateProfile(bio = bioText); bioEditing = false } },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(localizedString(Res.string.save))
                    }
                }

                ToggleSetting(
                    icon = TaratiIcons.Visibility,
                    title = Res.string.profile_visible_online,
                    checked = localIsVisible,
                    onCheckedChange = { visible ->
                        localIsVisible = visible
                        scope.launch { authViewModel.updateProfile(isVisible = visible) }
                    },
                )

                ToggleSetting(
                    icon = TaratiIcons.AccountCircle,
                    title = Res.string.profile_accept_challenges,
                    checked = localChallengesEnabled,
                    onCheckedChange = { enabled ->
                        localChallengesEnabled = enabled
                        scope.launch { authViewModel.updateProfile(challengesEnabled = enabled) }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 12.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(localizedString(Res.string.settings_delete_account))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
