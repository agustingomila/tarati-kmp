package com.agustin.tarati.ui.components.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.localization.LocalizedText
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.back
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.editing
import com.agustin.tarati.shared.generated.resources.menu
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun TaratiTopBar(
    title: String,
    isEditing: Boolean = false,
    navigationType: TopBarNavigationType = TopBarNavigationType.Menu,
    onNavigationClick: (() -> Unit)? = null,
    drawerState: DrawerState? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    val scope = rememberCoroutineScope()

    val navigationIcon: @Composable (() -> Unit) =
        when (navigationType) {
            TopBarNavigationType.Menu -> {
                {
                    IconButton(
                        onClick = {
                            drawerState?.let {
                                scope.launch {
                                    if (it.isClosed) it.open() else it.close()
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = TaratiIcons.Menu,
                            contentDescription = localizedString(Res.string.menu),
                        )
                    }
                }
            }

            TopBarNavigationType.Back -> {
                {
                    IconButton(onClick = { onNavigationClick?.invoke() }) {
                        Icon(
                            imageVector = TaratiIcons.ArrowBack,
                            contentDescription = localizedString(Res.string.back),
                        )
                    }
                }
            }

            TopBarNavigationType.Close -> {
                {
                    IconButton(onClick = { onNavigationClick?.invoke() }) {
                        Icon(
                            imageVector = TaratiIcons.Cancel,
                            contentDescription = localizedString(Res.string.cancel),
                        )
                    }
                }
            }
        }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                if (isEditing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    LocalizedText(
                        resource = Res.string.editing,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

sealed class TopBarNavigationType {
    object Menu : TopBarNavigationType()

    object Back : TopBarNavigationType()

    object Close : TopBarNavigationType()
}
