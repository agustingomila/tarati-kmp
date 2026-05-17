package com.agustin.tarati.core.domain.game.board

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val vertices: List<Vertex>,
)
