package com.agustin.tarati.ui.components.game.highlights.sequences

import com.agustin.tarati.core.domain.game.board.Edge
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.B5
import com.agustin.tarati.core.domain.game.board.GameBoard.B6
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C10
import com.agustin.tarati.core.domain.game.board.GameBoard.C11
import com.agustin.tarati.core.domain.game.board.GameBoard.C12
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C4
import com.agustin.tarati.core.domain.game.board.GameBoard.C5
import com.agustin.tarati.core.domain.game.board.GameBoard.C6
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.board.GameBoard.C9
import com.agustin.tarati.core.domain.game.board.GameBoard.adjacencyMap
import com.agustin.tarati.core.domain.game.board.GameBoard.centralRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.circumferenceRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.domesticRegions
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import com.agustin.tarati.ui.components.game.highlights.createElectricHighlight
import com.agustin.tarati.ui.components.game.highlights.createForceArcHighlight
import com.agustin.tarati.ui.components.game.highlights.createHighlight
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.CENTRAL_PAIR_SEQUENCE
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.CENTRAL_RAY_SEQUENCE
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.CIRCUMFERENCE_PAIR_SEQUENCE
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.DOMESTIC_SEQUENCE
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.FINAL_RAYS
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.GAME_OVER_FLASH
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.WAVE_EXPANSION

fun createAlternativeGameOverSequence(matchState: MatchState): List<List<HighlightAnimation>> {
    val winner = matchState.winner ?: return emptyList()
    val sequences = mutableListOf<List<HighlightAnimation>>()

    // Secuencia 1: Destello inicial desde el centro
    sequences.addAll(createConcentricFlashSequence(GAME_OVER_FLASH))

    // Secuencia 2: Iluminación total del tablero del ganador
    sequences.addAll(createBoardIlluminationSequence(winner, matchState.gameState))

    // Secuencia 3: Domestic regions simultáneamente
    sequences.add(domesticRegions.map { it.createHighlight(DOMESTIC_SEQUENCE) })

    // Secuencia 4: Circumference regions en pares opuestos
    sequences.addAll(createCircumferencePairSequence())

    // Secuencia 5: Central regions en pares opuestos (vórtice)
    sequences.addAll(createCentralRegionPairSequence())

    // Secuencia 6: Rayos centrales con dos edges consecutivos simultáneos
    sequences.addAll(createCentralRaysSequence())

    // Secuencia 7: Rayos finales
    sequences.addAll(createRaysFinalSequence(A1, FINAL_RAYS))

    return sequences
}

/**
 * Secuencia de regiones centrales en pares enfrentados (vórtice)
 */
private fun createCentralRegionPairSequence(): List<List<HighlightAnimation>> {
    val sequences = mutableListOf<List<HighlightAnimation>>()

    // Pares opuestos de regiones centrales
    val centralPairs =
        listOf(
            listOf(centralRegions[0], centralRegions[3]), // Regiones opuestas
            listOf(centralRegions[1], centralRegions[4]),
            listOf(centralRegions[2], centralRegions[5]),
        )

    // Animamos cada par de regiones centrales
    centralPairs.forEach { pair ->
        val pairAnimations =
            pair.map { region ->
                region.createHighlight(CENTRAL_PAIR_SEQUENCE)
            }
        sequences.add(pairAnimations)
    }

    return sequences
}

/**
 * Secuencia de rayos centrales - dos paths simultáneos para el efecto trébol
 */
private fun createCentralRaysSequence(): List<List<HighlightAnimation>> {
    val sequences = mutableListOf<List<HighlightAnimation>>()

    // Definimos los dos caminos de los rayos que ocurrirán simultáneamente
    val rayPaths = listOf(
        // Primer trébol
        listOf(
            A1, B2, C4, C5, B3,
            A1, B6, C12, C1, B1,
            A1, B4, C8, C9, B5,
            A1,
        ),
        // Segundo trébol (simultáneo)
        listOf(
            A1, B5, C10, C11, B6,
            A1, B3, C6, C7, B4,
            A1, B1, C2, C3, B2,
            A1,
        ),
    )

    // Creamos animaciones para ambos paths en paralelo
    sequences.addAll(createSimultaneousRaysSequence(rayPaths))

    return sequences
}

/**
 * Crea una secuencia donde múltiples rayos se animan simultáneamente
 */
private fun createSimultaneousRaysSequence(paths: List<List<Vertex>>): List<List<HighlightAnimation>> {
    val sequences = mutableListOf<List<HighlightAnimation>>()

    // Encontrar la longitud máxima entre todos los paths
    val maxLength = paths.maxOf { it.size }

    // Animamos todos los vértices iniciales simultáneamente
    val initialAnimations = paths.map { it[0].createHighlight(CENTRAL_RAY_SEQUENCE, pulse = true) }
    sequences.add(initialAnimations)

    // Animamos pares de edges consecutivos para todos los paths simultáneamente
    for (i in 0 until maxLength - 2) {
        val currentStep = mutableListOf<HighlightAnimation>()

        paths.forEach { path ->
            if (i < path.size - 2) {
                // Primer edge del par consecutivo
                val firstEdge = Edge(path[i] to path[i + 1])
                currentStep.add(
                    firstEdge.createElectricHighlight(
                        CENTRAL_RAY_SEQUENCE,
                        pulse = true
                    )
                )

                // Segundo edge del par consecutivo
                val secondEdge = Edge(path[i + 1] to path[i + 2])
                currentStep.add(
                    secondEdge.createElectricHighlight(
                        CENTRAL_RAY_SEQUENCE,
                        pulse = true
                    )
                )

                // Vértice intermedio entre los dos edges
                currentStep.add(path[i + 1].createHighlight(CENTRAL_RAY_SEQUENCE, pulse = true))
            }
        }

        sequences.add(currentStep)
    }

    // Animamos los últimos segmentos para todos los paths
    val finalStep = mutableListOf<HighlightAnimation>()
    paths.forEach { path ->
        if (path.size >= 2) {
            val lastEdge = Edge(path[path.size - 2] to path[path.size - 1])
            finalStep.add(lastEdge.createElectricHighlight(CENTRAL_RAY_SEQUENCE, pulse = true))
            finalStep.add(path.last().createHighlight(CENTRAL_RAY_SEQUENCE, pulse = true))
        }
    }
    sequences.add(finalStep)

    return sequences
}

private fun createCircumferencePairSequence(): List<List<HighlightAnimation>> {
    val sequences = mutableListOf<List<HighlightAnimation>>()

    // Pares opuestos de regiones de la circunferencia
    val circumferencePairs =
        listOf(
            listOf(circumferenceRegions[0], circumferenceRegions[6]), // [B1,C1,C2]    vs [B4,C7,C8]
            listOf(circumferenceRegions[1], circumferenceRegions[7]), // [B1,C2,C3,B2] vs [B4,C8,C9,B5]
            listOf(circumferenceRegions[2], circumferenceRegions[8]), // [B2,C3,C4]    vs [B5,C9,C10]
            listOf(circumferenceRegions[3], circumferenceRegions[9]), // [B2,C4,C5,B3] vs [B5,C10,C11,B6]
            listOf(circumferenceRegions[4], circumferenceRegions[10]), // [B3,C5,C6]    vs [B6,C11,C12]
            listOf(circumferenceRegions[5], circumferenceRegions[11]), // [B3,C6,C7,B4] vs [B6,C12,C1,B1]
        )

    // Creamos animaciones para cada par
    circumferencePairs.forEach { pair ->
        val pairAnimations =
            pair.map { region ->
                region.createHighlight(CIRCUMFERENCE_PAIR_SEQUENCE)
            }
        sequences.add(pairAnimations)
    }

    return sequences
}

private fun createBoardIlluminationSequence(
    winner: CobColor,
    gameState: GameState,
    duration: Long = WAVE_EXPANSION,
): List<List<HighlightAnimation>> {
    val winnerVertices =
        gameState.cobs.entries
            .filter { it.value.color == winner }
            .map { it.key }
            .toList()

    // Paso 1: Encontrar todos los componentes conectados
    val components = findConnectedComponents(winnerVertices)

    // Paso 2: Si hay múltiples componentes, conectarlos todos
    val edges =
        if (components.size > 1) {
            connectAllComponents(components)
        } else {
            // Si solo hay un componente, conectar cada vértice con sus más cercanos dentro del componente
            val singleComponentEdges = mutableSetOf<Edge>()
            winnerVertices.forEach { vertex ->
                val closestVertices = findAllClosestVertices(vertex, winnerVertices)
                closestVertices.forEach { closestVertex ->
                    singleComponentEdges.add(Edge(vertex to closestVertex))
                }
            }
            singleComponentEdges
        }

    // Iluminación simultánea de todos los edges
    val edgeAnimations = mutableListOf<HighlightAnimation>()
    edges.forEach { edge ->
        edgeAnimations.add(
            edge.createForceArcHighlight(duration)
        )
    }

    // Efecto de pulso rítmico en todos los edges
    val pulseAnimations = mutableListOf<HighlightAnimation>()
    repeat(4) { _ ->
        edges.forEach { edge ->
            pulseAnimations.add(
                edge.createForceArcHighlight(duration, pulse = true)
            )
        }
    }

    return listOf(edgeAnimations, pulseAnimations)
}

/**
 * Encuentra todos los componentes conectados en el conjunto de vértices ganadores
 */
private fun findConnectedComponents(vertices: List<Vertex>): List<Set<Vertex>> {
    val visited = mutableSetOf<Vertex>()
    val components = mutableListOf<Set<Vertex>>()

    vertices.forEach { vertex ->
        if (vertex !in visited) {
            val component = mutableSetOf<Vertex>()
            val queue = ArrayDeque<Vertex>()
            queue.add(vertex)
            visited.add(vertex)
            component.add(vertex)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()

                // Encontrar vértices conectados (a distancia 1)
                adjacencyMap[current]?.forEach { neighbor ->
                    if (neighbor in vertices && neighbor !in visited) {
                        visited.add(neighbor)
                        component.add(neighbor)
                        queue.add(neighbor)
                    }
                }
            }

            components.add(component)
        }
    }

    return components
}

/**
 * Conecta todos los componentes mediante sus vértices más cercanos
 */
private fun connectAllComponents(components: List<Set<Vertex>>): Set<Edge> {
    val allEdges = mutableSetOf<Edge>()
    val connectedComponents = mutableListOf<MutableSet<Vertex>>()

    // Inicializar cada componente como un conjunto separado
    components.forEach { component ->
        connectedComponents.add(component.toMutableSet())

        // Conectar vértices dentro de cada componente
        component.forEach { vertex ->
            val closestVertices = findAllClosestVertices(vertex, component.toList())
            closestVertices.forEach { closestVertex ->
                allEdges.add(Edge(vertex to closestVertex))
            }
        }
    }

    // Conectar componentes entre sí hasta que todos estén conectados
    while (connectedComponents.size > 1) {
        var minDistance = Int.MAX_VALUE
        var bestEdge: Edge? = null
        var component1Index = -1
        var component2Index = -1

        // Encontrar la conexión más corta entre cualquier par de componentes
        for (i in connectedComponents.indices) {
            for (j in i + 1 until connectedComponents.size) {
                val component1 = connectedComponents[i]
                val component2 = connectedComponents[j]

                // Encontrar la distancia mínima entre estos dos componentes
                component1.forEach { vertex1 ->
                    component2.forEach { vertex2 ->
                        val distance = findDistance(vertex1, vertex2)
                        if (distance < minDistance) {
                            minDistance = distance
                            bestEdge = Edge(vertex1 to vertex2)
                            component1Index = i
                            component2Index = j
                        }
                    }
                }
            }
        }

        // Agregar la mejor conexión encontrada
        if (bestEdge != null) {
            allEdges.add(bestEdge)

            // Fusionar los dos componentes
            val mergedComponent = connectedComponents[component1Index]
            mergedComponent.addAll(connectedComponents[component2Index])
            connectedComponents.removeAt(component2Index)
        }
    }

    return allEdges
}

/**
 * Encuentra la distancia entre dos vértices en el grafo
 */
private fun findDistance(
    source: Vertex,
    target: Vertex,
): Int {
    if (source == target) return 0

    val queue = ArrayDeque<Vertex>()
    val visited = mutableSetOf<Vertex>()
    val distances = mutableMapOf<Vertex, Int>()

    queue.add(source)
    visited.add(source)
    distances[source] = 0

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val currentDistance = distances[current] ?: 0

        if (current == target) {
            return currentDistance
        }

        adjacencyMap[current]?.forEach { neighbor ->
            if (neighbor !in visited) {
                visited.add(neighbor)
                distances[neighbor] = currentDistance + 1
                queue.add(neighbor)
            }
        }
    }

    return Int.MAX_VALUE // No se encontró camino
}

/**
 * Encuentra todos los vértices más cercanos a un vértice dado dentro de una lista de vértices
 */
private fun findAllClosestVertices(
    source: Vertex,
    targets: List<Vertex>,
): Set<Vertex> {
    if (targets.isEmpty() || (targets.size == 1 && targets[0] == source)) {
        return emptySet()
    }

    // Excluir el vértice fuente de los targets
    val validTargets = targets.filter { it != source }.toSet()
    if (validTargets.isEmpty()) {
        return emptySet()
    }

    // Usar BFS para encontrar las distancias a todos los targets
    val distances = mutableMapOf<Vertex, Int>()
    val queue = ArrayDeque<Vertex>()
    val visited = mutableSetOf<Vertex>()

    queue.add(source)
    distances[source] = 0
    visited.add(source)

    var minDistance = Int.MAX_VALUE
    val closestVertices = mutableSetOf<Vertex>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val currentDistance = distances[current] ?: 0

        // Si encontramos un target, verificar si es el más cercano
        if (current != source && current in validTargets) {
            if (currentDistance < minDistance) {
                // Encontramos una distancia menor, reiniciamos la lista
                minDistance = currentDistance
                closestVertices.clear()
                closestVertices.add(current)
            } else if (currentDistance == minDistance) {
                // Misma distancia mínima, agregamos a la lista
                closestVertices.add(current)
            }
        }

        // Si ya encontramos targets y la distancia actual es mayor que la mínima,
        // no necesitamos explorar más allá (optimización)
        if (currentDistance > minDistance) {
            continue
        }

        // Explorar vecinos
        adjacencyMap[current]?.forEach { neighbor ->
            if (neighbor !in visited) {
                visited.add(neighbor)
                distances[neighbor] = currentDistance + 1
                queue.add(neighbor)
            }
        }
    }

    return closestVertices
}