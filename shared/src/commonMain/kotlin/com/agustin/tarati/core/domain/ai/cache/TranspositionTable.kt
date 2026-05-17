package com.agustin.tarati.core.domain.ai.cache

import com.agustin.tarati.core.domain.ai.evaluator.MoveEval

/**
 * Tabla de transposición para el motor Minimax.
 *
 * ## Propósito
 * En Tarati, el mismo estado del tablero puede alcanzarse por distintas
 * secuencias de movimientos (transposiciones). Sin esta tabla, el árbol Minimax
 * evaluaría el mismo nodo repetidamente. La tabla actúa como una caché de
 * resultados de búsqueda indexados por hash de posición.
 *
 * ## Condición de validez por profundidad
 * Una entrada solo se usa si fue calculada a una profundidad **mayor o igual**
 * a la profundidad actual de búsqueda (`it.depth >= depth`). Una entrada de
 * profundidad menor sería una aproximación menos precisa y podría corromper
 * el resultado — especialmente en profundización iterativa, donde la misma
 * posición se busca a profundidades crecientes.
 *
 * ## Limpieza entre dificultades
 * La tabla se limpia cuando cambia la dificultad (ver [TaratiAI.setConfig]).
 * Las entradas son profundidad-dependientes: un resultado calculado a depth=3
 * (dificultad Easy) no es válido para depth=7 (dificultad Champion), y
 * reutilizarlo silenciosamente degradaría la calidad del juego.
 *
 * ## LRU mediante LruCache
 * Migrado de LinkedHashMap (JVM) a LruCache (KMP): capacidad máxima con evicción
 * del elemento menos recientemente usado. Mantiene el uso de memoria acotado
 * en dispositivos con RAM limitada sin lógica adicional.
 */
class TranspositionTable(
    private val maxSize: Int = 10000,
) {
    private val table = LruCache<String, TranspositionEntry>(maxSize)

    fun size(): Int = table.size

    fun get(
        key: String,
        depth: Int,
    ): MoveEval? = table[key]?.takeIf { it.depth >= depth }?.result

    fun put(
        key: String,
        depth: Int,
        result: MoveEval,
    ) {
        table[key] = TranspositionEntry(depth, result)
    }

    fun clear() {
        table.clear()
    }

    private data class TranspositionEntry(
        val depth: Int,
        val result: MoveEval,
    )
}