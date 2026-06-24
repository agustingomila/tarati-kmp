package com.agustin.tarati.ui.theme

import androidx.compose.ui.graphics.Color

interface BoardPalette {
    val name: String

    val blackCobBorderColor: Color
    val blackCobColor: Color
    val boardBackground: Color
    val boardEdgeColor: Color
    val boardPerimeterColor: Color
    val boardPatternBorderColor: Color
    val boardPatternColor1: Color
    val boardPatternColor2: Color
    val boardPatternColor3: Color
    val boardVertexColor: Color
    val neutralColor: Color
    val selectionIndicatorColor: Color
    val textColor: Color
    val vertexAdjacentColor: Color
    val vertexOccupiedColor: Color
    val vertexSelectedColor: Color
    val whiteCobBorderColor: Color
    val whiteCobColor: Color

    val whiteCobLightColor: Color
    val blackCobLightColor: Color
    val whiteCobShadowColor: Color
    val blackCobShadowColor: Color

    val whiteCobSelectColor: Color
    val blackCobSelectColor: Color

    val highlightEdge1Color: Color
    val highlightEdge2Color: Color
    val highlightEdge3Color: Color

    val highlightVertexCapture1Color: Color
    val highlightVertexCapture2Color: Color
    val highlightVertexCapture3Color: Color
    val highlightVertexAdjacent1Color: Color
    val highlightVertexAdjacent2Color: Color
    val highlightVertexAdjacent3Color: Color
    val highlightVertexUpgrade1Color: Color
    val highlightVertexUpgrade2Color: Color
    val highlightVertexUpgrade3Color: Color

    val highlightRegion1Color: Color
    val highlightRegion2Color: Color
    val highlightRegion3Color: Color

    val whiteUpgradingWaveColor: Color
    val whiteConvertingWaveColor: Color
    val blackUpgradingWaveColor: Color
    val blackConvertingWaveColor: Color
}

object ClassicPalette : BoardPalette {
    override val name: String = "Classic"

    override val blackCobBorderColor: Color = Color(0xFFCBBFBF)
    override val blackCobColor: Color = Color(0xFF181717)
    override val boardBackground: Color = Color(0xFF867567)
    override val boardEdgeColor: Color = Color(0xFF57381A)
    override val boardPerimeterColor: Color = Color(0xFF968271)
    override val boardPatternBorderColor: Color = Color(0xFF654321)
    override val boardPatternColor1: Color = Color(0xFFC5915B)
    override val boardPatternColor2: Color = Color(0xFFD5A76A)
    override val boardPatternColor3: Color = Color(0xFFECD2A5)
    override val boardVertexColor: Color = Color(0xFF382617)
    override val neutralColor: Color = Color(0xFFB75B3E)
    override val selectionIndicatorColor: Color = Color(0x801894F6)
    override val textColor: Color = Color(0xFF22180A)
    override val vertexAdjacentColor: Color = Color(0xFFC9AD58)
    override val vertexOccupiedColor: Color = Color(0xFF333333)
    override val vertexSelectedColor: Color = Color(0xFF2196F3)
    override val whiteCobBorderColor: Color = Color(0xFF2F2C2C)
    override val whiteCobColor: Color = Color(0xFFDED7D3)

    override val whiteCobLightColor: Color = Color(0xFFDEDAD8)
    override val blackCobLightColor: Color = Color(0xFF2D2C2C)
    override val whiteCobShadowColor: Color = Color(0xFFA29892)
    override val blackCobShadowColor: Color = Color(0xFF171515)

    override val whiteCobSelectColor: Color = Color(0xFF92EE9B)
    override val blackCobSelectColor: Color = Color(0xFF85D3EA)

    override val highlightEdge1Color: Color = Color(0xFF00BFFF)
    override val highlightEdge2Color: Color = Color(0xFFDA70D6)
    override val highlightEdge3Color: Color = Color(0xFF00FFBF)

    override val highlightVertexCapture1Color: Color = Color(0xFF36D8F4)
    override val highlightVertexCapture2Color: Color = Color(0xFF6BF9E4)
    override val highlightVertexCapture3Color: Color = Color(0xFF0FC8E0)
    override val highlightVertexAdjacent1Color: Color = Color(0xFFC2884A)
    override val highlightVertexAdjacent2Color: Color = Color(0xFFBF9B6F)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFC7A43A)
    override val highlightVertexUpgrade1Color: Color = Color(0xFFEF7C2F)
    override val highlightVertexUpgrade2Color: Color = Color(0xFFF9AD6B)
    override val highlightVertexUpgrade3Color: Color = Color(0xFFE0710F)

    override val highlightRegion1Color: Color = Color(0xFF8769F6)
    override val highlightRegion2Color: Color = Color(0xFFA7E8E3)
    override val highlightRegion3Color: Color = Color(0xFF33EAEA)

    override val whiteUpgradingWaveColor: Color = Color(0xFF5533EA)
    override val whiteConvertingWaveColor: Color = Color(0xFF33EABF)
    override val blackUpgradingWaveColor: Color = Color(0xFFEA33C2)
    override val blackConvertingWaveColor: Color = Color(0xFFB3EA33)
}

object DarkPalette : BoardPalette {
    override val name: String = "Dark"

    override val blackCobBorderColor: Color = Color(0xFFA59EAF)
    override val blackCobColor: Color = Color(0xFF192454)
    override val boardBackground: Color = Color(0xFF4E4F98)
    override val boardEdgeColor: Color = Color(0xFF503375)
    override val boardPerimeterColor: Color = Color(0xFF69969B)
    override val boardPatternBorderColor: Color = Color(0xFF1DB2A5)
    override val boardPatternColor1: Color = Color(0xFF6848C5)
    override val boardPatternColor2: Color = Color(0xFF7647C2)
    override val boardPatternColor3: Color = Color(0xFF8456C5)
    override val boardVertexColor: Color = Color(0xFF333757)
    override val neutralColor: Color = Color(0xFF4F7C52)
    override val selectionIndicatorColor: Color = Color(0x80EC5B75)
    override val textColor: Color = Color(0xFFFFFFFF)
    override val vertexAdjacentColor: Color = Color(0xFFC9589C)
    override val vertexOccupiedColor: Color = Color(0xFF16B6A7)
    override val vertexSelectedColor: Color = Color(0xFFD3AA2E)
    override val whiteCobBorderColor: Color = Color(0xFF382750)
    override val whiteCobColor: Color = Color(0xFFA496C0)

    override val whiteCobLightColor: Color = Color(0xFFB0A3C9)
    override val blackCobLightColor: Color = Color(0xFF222F65)
    override val whiteCobShadowColor: Color = Color(0xFF8879A6)
    override val blackCobShadowColor: Color = Color(0xFF141F4F)

    override val whiteCobSelectColor: Color = Color(0xFFF8AC3B)
    override val blackCobSelectColor: Color = Color(0xFFF3D82A)

    override val highlightEdge1Color: Color = Color(0xFFFF00FF)
    override val highlightEdge2Color: Color = Color(0xFF80FF00)
    override val highlightEdge3Color: Color = Color(0xFFFF4040)

    override val highlightVertexCapture1Color: Color = Color(0xFFE736F4)
    override val highlightVertexCapture2Color: Color = Color(0xFFB26BF9)
    override val highlightVertexCapture3Color: Color = Color(0xFFA10FE0)
    override val highlightVertexAdjacent1Color: Color = Color(0xFFCF6679)
    override val highlightVertexAdjacent2Color: Color = Color(0xFFE0919F)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFB5475E)
    override val highlightVertexUpgrade1Color: Color = Color(0xFF85F436)
    override val highlightVertexUpgrade2Color: Color = Color(0xFF90E074)
    override val highlightVertexUpgrade3Color: Color = Color(0xFF0FE032)

    override val highlightRegion1Color: Color = Color(0xFF0396DA)
    override val highlightRegion2Color: Color = Color(0xFF66FFF0)
    override val highlightRegion3Color: Color = Color(0xFF00B3A1)

    override val whiteUpgradingWaveColor: Color = Color(0xFF3395EA)
    override val whiteConvertingWaveColor: Color = Color(0xFFEA33C2)
    override val blackUpgradingWaveColor: Color = Color(0xFF8833EA)
    override val blackConvertingWaveColor: Color = Color(0xFFEAE133)
}

object NaturePalette : BoardPalette {
    override val name: String = "Nature"

    override val blackCobBorderColor: Color = Color(0xFF42322A)
    override val blackCobColor: Color = Color(0xFF27556C)
    override val boardBackground: Color = Color(0xFF5C8498)
    override val boardEdgeColor: Color = Color(0xFF704F45)
    override val boardPerimeterColor: Color = Color(0xFF5D999B)
    override val boardPatternBorderColor: Color = Color(0xFF325921)
    override val boardPatternColor1: Color = Color(0xFF6D914E)
    override val boardPatternColor2: Color = Color(0xFF7CA25E)
    override val boardPatternColor3: Color = Color(0xFFACBD99)
    override val boardVertexColor: Color = Color(0xFF2E404E)
    override val neutralColor: Color = Color(0xFFC9A030)
    override val selectionIndicatorColor: Color = Color(0x80E58A12)
    override val textColor: Color = Color(0xFF216E27)
    override val vertexAdjacentColor: Color = Color(0xFF58C9A9)
    override val vertexOccupiedColor: Color = Color(0xFF1C5B20)
    override val vertexSelectedColor: Color = Color(0xFFF57C00)
    override val whiteCobBorderColor: Color = Color(0xFF4D3528)
    override val whiteCobColor: Color = Color(0xFFDEC8A5)

    override val whiteCobLightColor: Color = Color(0xFFE1CEB0)
    override val blackCobLightColor: Color = Color(0xFF2A4756)
    override val whiteCobShadowColor: Color = Color(0xFFBEA983)
    override val blackCobShadowColor: Color = Color(0xFF19333F)

    override val whiteCobSelectColor: Color = Color(0xFFFF5F5F)
    override val blackCobSelectColor: Color = Color(0xFFFF59C3)

    override val highlightEdge1Color: Color = Color(0xFFFF5100)
    override val highlightEdge2Color: Color = Color(0xFF44FF00)
    override val highlightEdge3Color: Color = Color(0xFF00FFFF)

    override val highlightVertexCapture1Color: Color = Color(0xFFEEF436)
    override val highlightVertexCapture2Color: Color = Color(0xFFDAF96B)
    override val highlightVertexCapture3Color: Color = Color(0xFF89E00F)
    override val highlightVertexAdjacent1Color: Color = Color(0xFF36B8F4)
    override val highlightVertexAdjacent2Color: Color = Color(0xFF6BCAF9)
    override val highlightVertexAdjacent3Color: Color = Color(0xFF0F9DE0)
    override val highlightVertexUpgrade1Color: Color = Color(0xFFF4B536)
    override val highlightVertexUpgrade2Color: Color = Color(0xFFF9A46B)
    override val highlightVertexUpgrade3Color: Color = Color(0xFFE0630F)

    override val highlightRegion1Color: Color = Color(0xFFE14EC9)
    override val highlightRegion2Color: Color = Color(0xFFEC59E5)
    override val highlightRegion3Color: Color = Color(0xFFE54297)

    override val whiteUpgradingWaveColor: Color = Color(0xFFEAD833)
    override val whiteConvertingWaveColor: Color = Color(0xFFEA3367)
    override val blackUpgradingWaveColor: Color = Color(0xFFEA9233)
    override val blackConvertingWaveColor: Color = Color(0xFF6433EA)
}

object GrayscalePalette : BoardPalette {
    override val name: String = "Grayscale"

    override val blackCobBorderColor: Color = Color(0xFFB9B9B9)
    override val blackCobColor: Color = Color(0xFF1A1A1A)
    override val boardBackground: Color = Color(0xFF808080)
    override val boardEdgeColor: Color = Color(0xFF404040)
    override val boardPerimeterColor: Color = Color(0xFF989898)
    override val boardPatternBorderColor: Color = Color(0xFF606060)
    override val boardPatternColor1: Color = Color(0xFF858585)
    override val boardPatternColor2: Color = Color(0xFFB0B0B0)
    override val boardPatternColor3: Color = Color(0xFFD0D0D0)
    override val boardVertexColor: Color = Color(0xFF303030)
    override val neutralColor: Color = Color(0xFF707070)
    override val selectionIndicatorColor: Color = Color(0x9CECB20C)
    override val textColor: Color = Color(0xFF101010)
    override val vertexAdjacentColor: Color = Color(0xFF6DF6DB)
    override val vertexOccupiedColor: Color = Color(0xFF202020)
    override val vertexSelectedColor: Color = Color(0xFF505050)
    override val whiteCobBorderColor: Color = Color(0xFF505050)
    override val whiteCobColor: Color = Color(0xFFE8E8E8)

    override val whiteCobLightColor: Color = Color(0xFFF6F6F6)
    override val blackCobLightColor: Color = Color(0xFF383838)
    override val whiteCobShadowColor: Color = Color(0xFFDAD0D0)
    override val blackCobShadowColor: Color = Color(0xFF181717)

    override val whiteCobSelectColor: Color = Color(0xFFFFBF94)
    override val blackCobSelectColor: Color = Color(0xFFF8AB98)

    override val highlightEdge1Color: Color = Color(0xFF00FFEA)
    override val highlightEdge2Color: Color = Color(0xFFBBFF00)
    override val highlightEdge3Color: Color = Color(0xFFFF1FE1)

    override val highlightVertexCapture1Color: Color = Color(0xFFFF0040)
    override val highlightVertexCapture2Color: Color = Color(0xFFFF00E6)
    override val highlightVertexCapture3Color: Color = Color(0xFFFF7B00)
    override val highlightVertexAdjacent1Color: Color = Color(0xFF00FF8C)
    override val highlightVertexAdjacent2Color: Color = Color(0xFF00B8FF)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFE6FF00)
    override val highlightVertexUpgrade1Color: Color = Color(0xFF7DFF00)
    override val highlightVertexUpgrade2Color: Color = Color(0xFF00FFD5)
    override val highlightVertexUpgrade3Color: Color = Color(0xFFFF3D00)

    override val highlightRegion1Color: Color = Color(0xFF84EC0E)
    override val highlightRegion2Color: Color = Color(0xFFFFEA00)
    override val highlightRegion3Color: Color = Color(0xFF00FF77)

    override val whiteUpgradingWaveColor: Color = Color(0xFF33EAEA)
    override val whiteConvertingWaveColor: Color = Color(0xFFB933EA)
    override val blackUpgradingWaveColor: Color = Color(0xFF2989FF)
    override val blackConvertingWaveColor: Color = Color(0xFFEA3367)
}

/**
 * Paleta navideña — "Noche de Navidad en el bosque".
 *
 * ## Diseño del tablero
 * Aprovecha el orden de dibujo de [DrawBoard]:
 *
 * ```
 * Centro        → color3 (pino brillante) alternado con color2 (rojo navideño)
 * Circunferencia → color3 (pino brillante) alternado con color1 (pino oscuro)
 * Doméstico     → color1 (pino oscuro, color sólido)
 * ```
 *
 * El resultado es el ajedrezado rojo/verde propio de la navidad en el corazón
 * del tablero, con una circunferencia en dos verdes contrastados y las regiones
 * domésticas en el verde pino más profundo.
 *
 * ## UI Material 3
 * - `neutralColor` rojo navideño → topbar y botones primarios en rojo ✓
 * - `boardPatternColor3` pino medio → background M3 en sage verde navideño ✓
 *
 * ## Piezas
 * - Blancas: marfil nieve con borde dorado (adornos de porcelana)
 * - Negras: rojo acebo profundo — completamente distintas del tablero verde
 */
object ChristmasPalette : BoardPalette {
    override val name: String = "Christmas"

    // ── Piezas ────────────────────────────────────────────────────────────────

    // Blancas: marfil cálido, borde dorado — como adornos de porcelana navideños
    override val whiteCobColor: Color = Color(0xFFF5EED5)  // Marfil nieve cálido
    override val whiteCobBorderColor: Color = Color(0xFFD4AF37)  // Dorado clásico
    override val whiteCobLightColor: Color = Color(0xFFFFFFF0)  // Nieve pura
    override val whiteCobShadowColor: Color = Color(0xFFB0C0CC)  // Azul hielo tenue
    override val whiteCobSelectColor: Color = Color(0xFFFFD700)  // Dorado brillante al seleccionar

    // Negras: rojo acebo intenso — bayas de acebo (holly berries)
    // Color completamente distinto del tablero → ambos bandos claramente diferenciados
    override val blackCobColor: Color = Color(0xFF921010)  // Rojo acebo profundo
    override val blackCobBorderColor: Color = Color(0xFF3D0808)  // Bordeaux muy oscuro
    override val blackCobLightColor: Color = Color(0xFFB51818)  // Rojo acebo brillante
    override val blackCobShadowColor: Color = Color(0xFF5C0606)  // Rojo noche
    override val blackCobSelectColor: Color = Color(0xFF7CFC00)  // Verde lima al seleccionar

    // ── Tablero ───────────────────────────────────────────────────────────────

    // Fondo: verde bosque nocturno — profundo como ver el pino de noche
    override val boardBackground: Color = Color(0xFF0B2C1A)  // Verde noche

    // Perímetro: guirnalda dorada brillante (tinsel)
    override val boardPerimeterColor: Color = Color(0xFFE8C840)  // Dorado navideño
    override val boardEdgeColor: Color = Color(0xFF7A1010)  // Rojo acebo profundo
    override val boardPatternBorderColor: Color = Color(0xFF1E3D18) // Verde bosque oscuro

    // Regiones del tablero — el orden importa:
    //   color3 (DOMINANTE): pino brillante, usado en centro Y circunferencia como color base
    //   color2: rojo navideño, alterna con color3 en el CENTRO → ajedrezado rojo/verde ✓
    //   color1: pino oscuro, alterna con color3 en CIRCUNFERENCIA y es único en DOMÉSTICO
    override val boardPatternColor3: Color = Color(0xFF3A8C50)  // Pino brillante (dominante, también define BG M3)
    override val boardPatternColor2: Color = Color(0xFFAD1818)  // Rojo navideño (centro alterno)
    override val boardPatternColor1: Color = Color(0xFF1C5230)  // Pino oscuro (circunferencia + doméstico)

    // Vértices: dorado oscuro para los puntos de intersección
    // También es el color onBackground en modo claro (texto sobre fondo sage)
    override val boardVertexColor: Color = Color(0xFF280112)  // Ámbar muy oscuro

    // ── UI / M3 ───────────────────────────────────────────────────────────────

    // Rojo navideño → topbar, primary buttons, FAB — la firma cromática de la paleta
    override val neutralColor: Color = Color(0xFFCC1A1A)  // Rojo navideño
    override val selectionIndicatorColor: Color = Color(0x80FFD700) // Dorado semi-transparente
    override val textColor: Color = Color(0xFF1A0A0A)  // Casi negro con tinte rojo

    override val vertexOccupiedColor: Color = Color(0xFF0F2018)  // Verde muy oscuro
    override val vertexAdjacentColor: Color = Color(0xFFFFD700)  // Dorado (movimientos válidos)
    override val vertexSelectedColor: Color = Color(0xFFFF3333)  // Rojo brillante (vértice seleccionado)

    // ── Highlights de bordes — luces navideñas ────────────────────────────────
    override val highlightEdge1Color: Color = Color(0xFFFFD700)  // Dorado
    override val highlightEdge2Color: Color = Color(0xFFFF4500)  // Naranja vela
    override val highlightEdge3Color: Color = Color(0xFF90EE90)  // Verde menta suave

    // ── Highlights de vértices ────────────────────────────────────────────────

    // Capturas: rojo fuego (sangre de acebo)
    override val highlightVertexCapture1Color: Color = Color(0xFFFF2424)
    override val highlightVertexCapture2Color: Color = Color(0xFFFF5555)
    override val highlightVertexCapture3Color: Color = Color(0xFFC80000)

    // Adyacentes: dorado cálido (como las luces del árbol)
    override val highlightVertexAdjacent1Color: Color = Color(0xFFFFD700)
    override val highlightVertexAdjacent2Color: Color = Color(0xFFFFB800)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFFFA000)

    // Mejoras: verde esmeralda (brillo del árbol de navidad)
    override val highlightVertexUpgrade1Color: Color = Color(0xFF00C853)
    override val highlightVertexUpgrade2Color: Color = Color(0xFF69F0AE)
    override val highlightVertexUpgrade3Color: Color = Color(0xFF00E676)

    // ── Highlights de región — adornos dorados ────────────────────────────────
    override val highlightRegion1Color: Color = Color(0xFFFFD700)  // Dorado brillante
    override val highlightRegion2Color: Color = Color(0xFFFFAA00)  // Ámbar cálido
    override val highlightRegion3Color: Color = Color(0xFFFF8C00)  // Naranja dorado

    // ── Ondas de conversión ───────────────────────────────────────────────────
    override val whiteUpgradingWaveColor: Color = Color(0xFFFFD700)  // Oro mágico
    override val whiteConvertingWaveColor: Color = Color(0xFF00E676)  // Verde esmeralda
    override val blackUpgradingWaveColor: Color = Color(0xFFFF2020)  // Rojo ardiente
    override val blackConvertingWaveColor: Color = Color(0xFFE8C840)  // Dorado navideño
}

object HalloweenPalette : BoardPalette {
    override val name: String = "Halloween"

    // ── Piezas ────────────────────────────────────────────────────────────────
    // Blancas: hueso antiguo, borde índigo profundo
    override val whiteCobColor: Color = Color(0xFFE7D0AB)  // Hueso antiguo
    override val whiteCobBorderColor: Color = Color(0xFF4B0082)  // Índigo oscuro
    override val whiteCobLightColor: Color = Color(0xFFF5F5DC)  // Beige hueso
    override val whiteCobShadowColor: Color = Color(0xFFC4B9A8)  // Hueso oscuro
    override val whiteCobSelectColor: Color = Color(0xFF39FF14)  // Verde neón al seleccionar

    // Negras: negro medianoche, borde naranja calabaza
    override val blackCobColor: Color = Color(0xFF1C1C1C)  // Negro medianoche
    override val blackCobBorderColor: Color = Color(0xFFFF7518)  // Naranja calabaza
    override val blackCobLightColor: Color = Color(0xFF2D2D2D)  // Gris oscuro
    override val blackCobShadowColor: Color = Color(0xFF0D0D0D)  // Negro puro
    override val blackCobSelectColor: Color = Color(0xFFFF5F1F)  // Naranja neón al seleccionar

    // ── Tablero ───────────────────────────────────────────────────────────────

    // Fondo: púrpura de medianoche
    override val boardBackground: Color = Color(0xFF2E1A47)  // Púrpura profundo

    // Perímetro: naranja calabaza brillante — borde de jack-o-lantern
    // Antes era un púrpura genérico que se fundía con el tablero.
    override val boardPerimeterColor: Color = Color(0xFFCC5500)  // Naranja calabaza

    // Borde: púrpura profundo (antes índigo azulado, ahora más cálido)
    override val boardEdgeColor: Color = Color(0xFF360054)  // Púrpura profundo

    // Borde entre regiones: púrpura muy oscuro en lugar del verde bosque anterior.
    // El verde brillante era completamente discordante con el esquema Halloween.
    override val boardPatternBorderColor: Color = Color(0xFF1C0830)  // Púrpura casi negro

    // Regiones del tablero — el orden importa:
    //   color3 (DOMINANTE): púrpura medio, usado en centro Y circunferencia como base
    //   color2: naranja calabaza oscuro, alterna con color3 en el CENTRO
    //           → crea el clásico ajedrezado naranja/púrpura de Halloween ✓
    //   color1: púrpura oscuro, alterna con color3 en CIRCUNFERENCIA y es único en DOMÉSTICO
    override val boardPatternColor3: Color = Color(0xFF8A4FBF)  // Púrpura claro (dominante)
    override val boardPatternColor2: Color = Color(0xFF7A3800)  // Naranja calabaza oscuro (centro alterno)
    override val boardPatternColor1: Color = Color(0xFF4A2C6A)  // Púrpura oscuro (circunf + doméstico)

    // Vértices: púrpura muy oscuro (puntos de intersección + onBackground en light mode)
    // Antes tenía un tono azul; ahora puramente púrpura para más cohesión.
    override val boardVertexColor: Color = Color(0xFF160A22)  // Púrpura casi negro

    // ── UI / M3 ───────────────────────────────────────────────────────────────
    // Violeta neón → topbar y botones primarios — la firma visual de Halloween
    override val neutralColor: Color = Color(0xFF9F00FF)  // Violeta neón
    override val selectionIndicatorColor: Color = Color(0x809F00FF)  // Violeta semi-transparente
    override val textColor: Color = Color(0xFFE8DCC8)  // Hueso (texto sobre fondo oscuro)

    override val vertexOccupiedColor: Color = Color(0xFF0F0F23)  // Azul noche
    override val vertexAdjacentColor: Color = Color(0xFF63C328)  // Verde tóxico (movimientos válidos)
    override val vertexSelectedColor: Color = Color(0xFFFF7518)  // Naranja calabaza (selección)

    // ── Highlights de bordes ──────────────────────────────────────────────────
    override val highlightEdge1Color: Color = Color(0xFFFF7518)  // Naranja calabaza
    override val highlightEdge2Color: Color = Color(0xFF9F00FF)  // Violeta neón
    override val highlightEdge3Color: Color = Color(0xFF39FF14)  // Verde tóxico

    // ── Highlights de vértices ────────────────────────────────────────────────

    // Capturas: carmesí sangre
    override val highlightVertexCapture1Color: Color = Color(0xFFDC143C)
    override val highlightVertexCapture2Color: Color = Color(0xFFFF1493)
    override val highlightVertexCapture3Color: Color = Color(0xFF8B0000)

    // Adyacentes: naranja calabaza (como brasas)
    override val highlightVertexAdjacent1Color: Color = Color(0xFFFFA500)
    override val highlightVertexAdjacent2Color: Color = Color(0xFFFF8C00)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFFF4500)

    // Mejoras: verde lima (veneno de bruja)
    override val highlightVertexUpgrade1Color: Color = Color(0xFF32CD32)
    override val highlightVertexUpgrade2Color: Color = Color(0xFF00FF00)
    override val highlightVertexUpgrade3Color: Color = Color(0xFF228B22)

    // ── Highlights de región — magia y oscuridad ──────────────────────────────
    override val highlightRegion1Color: Color = Color(0xFF9400D3)  // Violeta oscuro
    override val highlightRegion2Color: Color = Color(0xFF4B0082)  // Índigo
    override val highlightRegion3Color: Color = Color(0xFF191970)  // Azul medianoche

    // ── Ondas de conversión — efectos espectrales ─────────────────────────────
    override val whiteUpgradingWaveColor: Color = Color(0xFF39FF14)  // Verde espectral
    override val whiteConvertingWaveColor: Color = Color(0xFFFF7518)  // Naranja fantasma
    override val blackUpgradingWaveColor: Color = Color(0xFF9F00FF)  // Púrpura mágico
    override val blackConvertingWaveColor: Color = Color(0xFF00FFFF)  // Cian espectral
}

// ═════════════════════════════════════════════════════════════════════════════
// Paletas especiales — desbloqueables
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Paleta "Gilded" — Manuscrito iluminado / dorado envejecido.
 *
 * ## Diseño del tablero
 * ```
 * Centro        → ámbar dorado (color3) ↔ bronce oscuro (color2) — ajedrezado antiguo
 * Circunferencia → ámbar dorado (color3) ↔ sepia oscuro (color1)
 * Doméstico     → sepia oscuro (color1)
 * ```
 * Fondo cuero envejecido. Perímetro en oro antiguo brillante.
 *
 * ## Piezas
 * - Blancas: marfil quemado con borde oro — porcelana de coleccionista.
 * - Negras: ébano cálido con borde bronce — madera noble oscura.
 *
 * ## UI M3
 * `neutralColor = 0xFFB8860B` (dark goldenrod) → topbar y botones en dorado sobrio.
 */
object GildedPalette : BoardPalette {
    override val name: String = "Gilded"

    // ── Piezas ────────────────────────────────────────────────────────────────
    override val whiteCobColor: Color = Color(0xFFF2E0B0)  // Marfil quemado
    override val whiteCobBorderColor: Color = Color(0xFFE0A81E)  // Oro antiguo
    override val whiteCobLightColor: Color = Color(0xFFFFF8E7)  // Pergamino claro
    override val whiteCobShadowColor: Color = Color(0xFFB89640)  // Oro oscuro
    override val whiteCobSelectColor: Color = Color(0xFF8B008B)  // Púrpura tinta — contrasta con marfil

    override val blackCobColor: Color = Color(0xFF3D2200)  // Ébano cálido
    override val blackCobBorderColor: Color = Color(0xFF8B6914)  // Bronce oscuro
    override val blackCobLightColor: Color = Color(0xFF5C3A14)  // Madera clara
    override val blackCobShadowColor: Color = Color(0xFF1A0D00)  // Madera noche
    override val blackCobSelectColor: Color = Color(0xFFFFD700)  // Oro brillante — contrasta con ébano

    // ── Tablero ───────────────────────────────────────────────────────────────
    override val boardBackground: Color = Color(0xFF2A1A08)  // Cuero envejecido
    override val boardPerimeterColor: Color = Color(0xFFC99718)  // Oro antiguo brillante
    override val boardEdgeColor: Color = Color(0xFF3D2008)  // Madera oscura
    override val boardPatternBorderColor: Color = Color(0xFF1A0F05) // Línea casi negra

    // Centro: ámbar (dominante) ↔ bronce oscuro → evoca tablero de ajedrez antiguo
    // Circunferencia: ámbar ↔ sepia oscuro
    // Doméstico: sepia oscuro
    override val boardPatternColor3: Color = Color(0xFFB8860B)  // Ámbar dorado (dominante)
    override val boardPatternColor2: Color = Color(0xFF6B4500)  // Bronce oscuro (centro alterno)
    override val boardPatternColor1: Color = Color(0xFF4A2E05)  // Sepia oscuro (circunf + doméstico)

    override val boardVertexColor: Color = Color(0xFF1A0F05)  // Casi negro cálido

    // ── UI / M3 ───────────────────────────────────────────────────────────────
    override val neutralColor: Color = Color(0xFFB8860B)  // Dark goldenrod → topbar dorada
    override val selectionIndicatorColor: Color = Color(0x80DAA520)  // Oro semi-transparente
    override val textColor: Color = Color(0xFF1A0A00)  // Marrón muy oscuro

    override val vertexOccupiedColor: Color = Color(0xFF2A1500)  // Madera oscura
    override val vertexAdjacentColor: Color = Color(0xFFFFD700)  // Oro brillante (movimientos válidos)
    override val vertexSelectedColor: Color = Color(0xFFFF8C00)  // Naranja ámbar (selección)

    // ── Highlights de bordes — oro y ámbar ───────────────────────────────────
    override val highlightEdge1Color: Color = Color(0xFFFFD700)  // Oro puro
    override val highlightEdge2Color: Color = Color(0xFFDA5520)  // Dorado
    override val highlightEdge3Color: Color = Color(0xFFDEA315)  // Ámbar profundo

    // ── Highlights de vértices ────────────────────────────────────────────────
    // Capturas: escarlata (tinta sobre pergamino)
    override val highlightVertexCapture1Color: Color = Color(0xFFFF4500)
    override val highlightVertexCapture2Color: Color = Color(0xFFFF6347)
    override val highlightVertexCapture3Color: Color = Color(0xFFB22222)

    // Adyacentes: ámbar cálido
    override val highlightVertexAdjacent1Color: Color = Color(0xFFDAA520)
    override val highlightVertexAdjacent2Color: Color = Color(0xFFCD853F)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFD2691E)

    // Mejoras: índigo tinta (tinta medieval)
    override val highlightVertexUpgrade1Color: Color = Color(0xFF8B008B)
    override val highlightVertexUpgrade2Color: Color = Color(0xFF9400D3)
    override val highlightVertexUpgrade3Color: Color = Color(0xFF6A0DAD)

    // ── Highlights de región — dorado iluminado ───────────────────────────────
    override val highlightRegion1Color: Color = Color(0xFFFFD700)
    override val highlightRegion2Color: Color = Color(0xFFDAA520)
    override val highlightRegion3Color: Color = Color(0xFFB8860B)

    // ── Ondas de conversión ───────────────────────────────────────────────────
    override val whiteUpgradingWaveColor: Color = Color(0xFFFFD700)  // Oro mágico
    override val whiteConvertingWaveColor: Color = Color(0xFF8B4513)  // Caoba (tinta)
    override val blackUpgradingWaveColor: Color = Color(0xFFFF6347)  // Escarlata
    override val blackConvertingWaveColor: Color = Color(0xFF9400D3)  // Violeta tinta
}

/**
 * Paleta "Ember" — Metal en brasa / Forja.
 *
 * ## Diseño del tablero
 * ```
 * Centro        → óxido naranja (color3) ↔ naranja fundido (color2) — metal calentado
 * Circunferencia → óxido naranja (color3) ↔ carbón oscuro (color1)
 * Doméstico     → carbón oscuro (color1)
 * ```
 * Fondo carbón casi negro. Perímetro en cobre quemado.
 *
 * ## Piezas
 * - Blancas: acero caliente — marfil con tinte cálido.
 * - Negras: hierro frío — azul gris oscuro, contrasta con el calor del tablero.
 *
 * ## UI M3
 * `neutralColor = 0xFFCC4400` (brasa naranja) → topbar y botones en naranja forja.
 */
object EmberPalette : BoardPalette {
    override val name: String = "Ember"

    // ── Piezas ────────────────────────────────────────────────────────────────
    // Blancas: acero caliente — color marfil con tinte cálido
    override val whiteCobColor: Color = Color(0xFFDDC8A8)  // Acero calentado
    override val whiteCobBorderColor: Color = Color(0xFFC58E31)  // Cobre quemado
    override val whiteCobLightColor: Color = Color(0xFFEED8B8)  // Metal blanco
    override val whiteCobShadowColor: Color = Color(0xFFA08060)  // Sombra cobre
    override val whiteCobSelectColor: Color = Color(0xFF00E5FF)  // Cyan eléctrico — chispa sobre metal

    // Negras: hierro frío — contrasta fuertemente con el calor del tablero
    override val blackCobColor: Color = Color(0xFF1E2028)  // Hierro frío / acero azul
    override val blackCobBorderColor: Color = Color(0xFF8B4513)  // Óxido oscuro
    override val blackCobLightColor: Color = Color(0xFF2C303A)  // Acero reflejante
    override val blackCobShadowColor: Color = Color(0xFF0F1015)  // Sombra hierro
    override val blackCobSelectColor: Color = Color(0xFFFF6B00)  // Naranja fundido — brasa sobre hierro

    // ── Tablero ───────────────────────────────────────────────────────────────
    override val boardBackground: Color = Color(0xFF28150F)  // Carbón profundo
    override val boardPerimeterColor: Color = Color(0xFFB87333)  // Cobre quemado
    override val boardEdgeColor: Color = Color(0xFF5C1800)  // Óxido profundo
    override val boardPatternBorderColor: Color = Color(0xFF8B2500) // Línea brasa

    // Centro: óxido naranja (dominante) ↔ naranja fundido → metal calentado
    // Circunferencia: óxido naranja ↔ carbón oscuro
    // Doméstico: carbón oscuro
    override val boardPatternColor3: Color = Color(0xFF4A1C0A)  // Óxido naranja (dominante)
    override val boardPatternColor2: Color = Color(0xFFB03000)  // Naranja fundido (centro alterno)
    override val boardPatternColor1: Color = Color(0xFF2A1205)  // Carbón oscuro (circunf + doméstico)

    override val boardVertexColor: Color = Color(0xFF100806)  // Carbón casi negro

    // ── UI / M3 ───────────────────────────────────────────────────────────────
    override val neutralColor: Color = Color(0xFFCC4400)  // Brasa naranja → topbar forja
    override val selectionIndicatorColor: Color = Color(0x80FF4500)  // Fuego semi-transparente
    override val textColor: Color = Color(0xFF0A0804)  // Carbón

    override val vertexOccupiedColor: Color = Color(0xFF140C08)  // Hierro oscuro
    override val vertexAdjacentColor: Color = Color(0xFFFF6600)  // Brasa brillante (movimientos)
    override val vertexSelectedColor: Color = Color(0xFFFFAA00)  // Amarillo fundido (selección)

    // ── Highlights de bordes — fuego ─────────────────────────────────────────
    override val highlightEdge1Color: Color = Color(0xFFFFBE05)  // Ámbar caliente
    override val highlightEdge2Color: Color = Color(0xFFE73B07)  // Naranja fuego
    override val highlightEdge3Color: Color = Color(0xFFFFE200)  // Plasma amarillo

    // ── Highlights de vértices ────────────────────────────────────────────────
    // Capturas: fuego intenso
    override val highlightVertexCapture1Color: Color = Color(0xFFFF2400)
    override val highlightVertexCapture2Color: Color = Color(0xFFFF6600)
    override val highlightVertexCapture3Color: Color = Color(0xFFCC1100)

    // Adyacentes: brasa
    override val highlightVertexAdjacent1Color: Color = Color(0xFFFF8C00)
    override val highlightVertexAdjacent2Color: Color = Color(0xFFFF6B00)
    override val highlightVertexAdjacent3Color: Color = Color(0xFFDD5500)

    // Mejoras: metal al rojo blanco (plasma)
    override val highlightVertexUpgrade1Color: Color = Color(0xFFFFFFAA)
    override val highlightVertexUpgrade2Color: Color = Color(0xFFFFDD88)
    override val highlightVertexUpgrade3Color: Color = Color(0xFFFFBB44)

    // ── Highlights de región — magma ──────────────────────────────────────────
    override val highlightRegion1Color: Color = Color(0xFFFF4500)
    override val highlightRegion2Color: Color = Color(0xFFCC2200)
    override val highlightRegion3Color: Color = Color(0xFF881100)

    // ── Ondas de conversión ───────────────────────────────────────────────────
    override val whiteUpgradingWaveColor: Color = Color(0xFFFFCC44)  // Oro fundido
    override val whiteConvertingWaveColor: Color = Color(0xFF00DDFF)  // Chispa cyan (contraste)
    override val blackUpgradingWaveColor: Color = Color(0xFFFF3300)  // Llama
    override val blackConvertingWaveColor: Color = Color(0xFFFFAA00)  // Brasa
}

/**
 * Paleta "Aurora" — Luces del norte.
 *
 * ## Diseño del tablero
 * ```
 * Centro        → verde ártico (color3) ↔ púrpura aurora (color2) — cielo polar
 * Circunferencia → verde ártico (color3) ↔ azul noche (color1)
 * Doméstico     → azul noche (color1)
 * ```
 * Fondo negro ártico casi puro. Perímetro en turquesa aurora brillante.
 *
 * ## Piezas
 * - Blancas: hielo cristalino — blanco frío con borde turquesa.
 * - Negras: cosmos profundo — casi negro con tinte añil, borde púrpura aurora.
 *
 * ## UI M3
 * `neutralColor = 0xFF00B4D8` (cian ártico) → topbar y botones en turquesa polar.
 */
object AuroraPalette : BoardPalette {
    override val name: String = "Aurora"

    // ── Piezas ────────────────────────────────────────────────────────────────
    // Blancas: hielo cristalino — frío y transparente
    override val whiteCobColor: Color = Color(0xFFD8EEF8)  // Hielo/escarcha
    override val whiteCobBorderColor: Color = Color(0xFF40E0D0)  // Turquesa aurora
    override val whiteCobLightColor: Color = Color(0xFFF0F8FF)  // Alice blue / nieve
    override val whiteCobShadowColor: Color = Color(0xFF88B8D0)  // Hielo en sombra
    override val whiteCobSelectColor: Color = Color(0xFFFF00FF)  // Magenta aurora — contrasta con hielo

    // Negras: cosmos — azul noche profundo
    override val blackCobColor: Color = Color(0xFF0C0818)  // Cosmos profundo
    override val blackCobBorderColor: Color = Color(0xFF8A2BE2)  // Violeta aurora
    override val blackCobLightColor: Color = Color(0xFF181028)  // Nebulosa
    override val blackCobShadowColor: Color = Color(0xFF060410)  // Vacío
    override val blackCobSelectColor: Color = Color(0xFF00FF88)  // Verde aurora — contrasta con cosmos

    // ── Tablero ───────────────────────────────────────────────────────────────
    override val boardBackground: Color = Color(0xFF041526)  // Noche ártica
    override val boardPerimeterColor: Color = Color(0xFF1AA199)  // Turquesa aurora brillante
    override val boardEdgeColor: Color = Color(0xFF1A0A40)  // Azul púrpura profundo
    override val boardPatternBorderColor: Color = Color(0xFF0A3028) // Verde abismo

    // Centro: verde ártico (dominante) ↔ púrpura aurora → cielo polar
    // Circunferencia: verde ártico ↔ azul noche
    // Doméstico: azul noche
    override val boardPatternColor3: Color = Color(0xFF0D4A3A)  // Verde ártico (dominante)
    override val boardPatternColor2: Color = Color(0xFF3A1A6B)  // Púrpura aurora (centro alterno)
    override val boardPatternColor1: Color = Color(0xFF0A2A38)  // Azul noche (circunf + doméstico)

    override val boardVertexColor: Color = Color(0xFF050A12)  // Casi negro con tinte azul

    // ── UI / M3 ───────────────────────────────────────────────────────────────
    override val neutralColor: Color = Color(0xFF00B4D8)  // Cian ártico → topbar polar
    override val selectionIndicatorColor: Color = Color(0x8040E0D0)  // Turquesa semi-transparente
    override val textColor: Color = Color(0xFF030810)  // Azul noche casi negro

    override val vertexOccupiedColor: Color = Color(0xFF060C14)  // Espacio
    override val vertexAdjacentColor: Color = Color(0xFF7FFFD4)  // Aquamarine (movimientos)
    override val vertexSelectedColor: Color = Color(0xFF00FF7F)  // Verde aurora (selección)

    // ── Highlights de bordes — bandas de aurora ───────────────────────────────
    override val highlightEdge1Color: Color = Color(0xFF00FF9F)  // Verde aurora
    override val highlightEdge2Color: Color = Color(0xFFFF00FF)  // Magenta aurora
    override val highlightEdge3Color: Color = Color(0xFF14FFEC)  // Cian aurora

    // ── Highlights de vértices ────────────────────────────────────────────────
    // Capturas: turquesa vívido
    override val highlightVertexCapture1Color: Color = Color(0xFF00FFCC)
    override val highlightVertexCapture2Color: Color = Color(0xFF00DDFF)
    override val highlightVertexCapture3Color: Color = Color(0xFF0099DD)

    // Adyacentes: turquesa suave
    override val highlightVertexAdjacent1Color: Color = Color(0xFF7FFFD4)
    override val highlightVertexAdjacent2Color: Color = Color(0xFF48D1CC)
    override val highlightVertexAdjacent3Color: Color = Color(0xFF40E0D0)

    // Mejoras: púrpura cósmico
    override val highlightVertexUpgrade1Color: Color = Color(0xFFBF5FFF)
    override val highlightVertexUpgrade2Color: Color = Color(0xFFDA70D6)
    override val highlightVertexUpgrade3Color: Color = Color(0xFF9400D3)

    // ── Highlights de región — cosmos ─────────────────────────────────────────
    override val highlightRegion1Color: Color = Color(0xFF7B68EE)  // Slate blue
    override val highlightRegion2Color: Color = Color(0xFF6A5ACD)  // Medium slate blue
    override val highlightRegion3Color: Color = Color(0xFF483D8B)  // Dark slate blue

    // ── Ondas de conversión — magia ártica ────────────────────────────────────
    override val whiteUpgradingWaveColor: Color = Color(0xFF00CED1)  // Turquesa oscuro
    override val whiteConvertingWaveColor: Color = Color(0xFFBF5FFF)  // Púrpura
    override val blackUpgradingWaveColor: Color = Color(0xFF00FF9F)  // Verde aurora
    override val blackConvertingWaveColor: Color = Color(0xFFFF00CC)  // Magenta polar
}

val availablePalettes: List<BoardPalette> =
    listOf(
        ClassicPalette,
        DarkPalette,
        NaturePalette,
        GrayscalePalette,
        ChristmasPalette,
        HalloweenPalette,
        // Paletas de eventos especiales — visibles en Settings solo tras desbloquearlas.
        // SettingsViewModel las filtra vía SeasonalThemeManager.isPaletteAvailable.
        AuroraPalette,
        EmberPalette,
        // Paleta premium — visible siempre en el selector (bloqueada hasta la compra IAP).
        // SettingsViewModel la incluye en allPalettesForSelector y la filtra de
        // availablePalettes hasta que PaletteProducts.GILDED esté en purchasedProductIds.
        GildedPalette,
    )

data class PaletteList(val items: List<BoardPalette>)