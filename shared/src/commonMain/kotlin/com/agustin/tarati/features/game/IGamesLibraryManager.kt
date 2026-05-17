package com.agustin.tarati.features.game

import com.agustin.tarati.core.data.database.dto.MatchDto

interface IGamesLibraryManager {
    fun importGameFromMatchDto(matchDto: MatchDto)

    /**
     * Exporta el estado actual de la partida a un [MatchDto] listo para persistir.
     *
     * Los labels de jugador son computados por el caller (típicamente [GameScreen])
     * para separar la lógica de presentación de la lógica de dominio del ViewModel.
     * El formato recomendado por el caller es:
     * - Humano con nombre → el nombre del usuario (ej. `"Agustín"`)
     * - Humano sin nombre → string localizado `player_human` (ej. `"Humano"`)
     * - IA                → `"IA (Nivel)"` (ej. `"IA (Fácil)"`)
     *
     * @param whiteLabel Etiqueta descriptiva para el jugador blanco.
     * @param blackLabel Etiqueta descriptiva para el jugador negro.
     */
    fun exportGameToMatchDto(whiteLabel: String, blackLabel: String): MatchDto
}