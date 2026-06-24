package com.agustin.tarati.core.domain.game.board

import kotlinx.serialization.Serializable

@Serializable
data class Edge(
    val pair: Pair<Vertex, Vertex>,
) {
    val from: Vertex get() = this.pair.first
    val to: Vertex get() = this.pair.second
    val name: String get() = "${from.name}-${to.name}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Edge) return false

        return (this.from == other.from && this.to == other.to) ||
                (this.from == other.to && this.to == other.from)
    }

    override fun hashCode(): Int = from.hashCode() + to.hashCode()
}
