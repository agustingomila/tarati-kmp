package com.agustin.tarati.core.domain.ai.cache

/**
 * Implementación LRU (Least Recently Used) cache para Kotlin Multiplatform.
 *
 * ## Propósito
 * En KMP, LinkedHashMap no tiene el constructor con accessOrder ni permite
 * sobrescribir removeEldestEntry(). Esta clase replica ese comportamiento:
 * mantiene un límite de capacidad y elimina automáticamente el elemento
 * menos recientemente usado cuando se alcanza el límite.
 *
 * ## Uso
 * ```kotlin
 * val cache = LruCache<String, Double>(maxSize = 100)
 * cache.put("key", 42.0)
 * val value = cache.get("key")  // Marca "key" como recientemente usado
 * ```
 *
 * @param maxSize Capacidad máxima del cache. Cuando se excede, se elimina
 *                el elemento menos recientemente usado.
 */
class LruCache<K, V>(
    private val maxSize: Int
) {
    // LinkedHashMap mantiene orden de inserción
    private val map = LinkedHashMap<K, V>()

    /**
     * Lista que mantiene el orden de acceso: el último elemento es el más
     * recientemente usado, el primero es el más antiguo (candidato a evicción).
     */
    private val accessOrder = mutableListOf<K>()

    val size: Int
        get() = map.size

    /**
     * Obtiene un valor y lo marca como recientemente usado.
     * Si la key no existe, retorna null.
     */
    operator fun get(key: K): V? {
        val value = map[key] ?: return null

        // Mover al final (más reciente)
        accessOrder.remove(key)
        accessOrder.add(key)

        return value
    }

    /**
     * Inserta o actualiza un valor. Si se alcanza maxSize, elimina
     * el elemento menos recientemente usado.
     */
    fun put(key: K, value: V) {
        // Si la key ya existe, actualizarla sin afectar el tamaño
        if (map.containsKey(key)) {
            map[key] = value
            // Mover al final (más reciente)
            accessOrder.remove(key)
            accessOrder.add(key)
            return
        }

        // Nueva entrada: verificar capacidad
        if (map.size >= maxSize) {
            // Eliminar el elemento menos recientemente usado (primero en la lista)
            val eldest = accessOrder.removeFirstOrNull()
            if (eldest != null) {
                map.remove(eldest)
            }
        }

        // Agregar nueva entrada
        map[key] = value
        accessOrder.add(key)
    }

    /**
     * Permite usar sintaxis de corchetes: cache[key] = value
     */
    operator fun set(key: K, value: V) {
        put(key, value)
    }

    /**
     * Elimina todas las entradas del cache.
     */
    fun clear() {
        map.clear()
        accessOrder.clear()
    }

    /**
     * Verifica si una key existe en el cache (sin marcarla como accedida).
     */
    fun containsKey(key: K): Boolean = map.containsKey(key)

    /**
     * Elimina una key específica del cache.
     */
    fun remove(key: K): V? {
        accessOrder.remove(key)
        return map.remove(key)
    }
}