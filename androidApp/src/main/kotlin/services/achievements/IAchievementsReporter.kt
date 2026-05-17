package com.agustin.tarati.services.achievements

/**
 * Abstracción sobre el mecanismo de entrega de logros a Play Games.
 *
 * Separar la entrega de la lógica de routing en [AchievementsManager]
 * permite testear qué logros se activan dado un evento de juego, sin
 * necesidad de una Activity ni conexión real a Play Games.
 */
interface IAchievementsReporter {
    /** Desbloquea un logro one-shot identificado por su resource ID. */
    fun unlock(achievementResId: Int)

    /**
     * Actualiza el progreso de un logro incremental.
     * @return true si el envío a Play Games se realizó, false si fue omitido
     *         (sin Activity disponible u otro error). El caller solo debe
     *         actualizar el caché local cuando el retorno sea true.
     */
    fun setSteps(achievementResId: Int, steps: Int): Boolean

    /**
     * Consulta el progreso actual de todos los logros desde el servidor de Play Games.
     * Devuelve [AchievementSnapshot]s: copias seguras de los campos necesarios, extraídas
     * del buffer del SDK antes de que este se cierre automáticamente.
     * [onResult] se invoca si la llamada tiene éxito.
     * [onFailure] se invoca si Play Games devuelve un error (sin Activity, red, etc.).
     */
    fun loadAchievements(
        onResult: (List<AchievementSnapshot>) -> Unit,
        onFailure: (Exception) -> Unit,
    )
}