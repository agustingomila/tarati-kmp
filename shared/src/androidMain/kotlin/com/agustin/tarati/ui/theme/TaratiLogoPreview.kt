package com.agustin.tarati.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.shared.R

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TaratiLogoPreview() {
    TaratiTheme {
        TaratiLogo(size = 160.dp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
private fun TaratiLogoDarkPreview() {
    TaratiTheme(darkTheme = true) {
        TaratiLogo(size = 160.dp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TaratiLogoXmlPreview() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "XML logo reference",
        modifier = Modifier.size(160.dp),
    )
}
