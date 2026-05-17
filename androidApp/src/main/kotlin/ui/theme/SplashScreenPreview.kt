package com.agustin.tarati.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.ui.splash.SplashScreen


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSplashScreen() {
    TaratiTheme {
        SplashScreen()
    }
}