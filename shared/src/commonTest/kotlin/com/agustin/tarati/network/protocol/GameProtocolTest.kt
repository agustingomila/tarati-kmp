package com.agustin.tarati.network.protocol

import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.description
import com.agustin.tarati.core.domain.game.play.GameEndReason
import com.agustin.tarati.core.domain.game.play.GameResult
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.time.TimeControl
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests para el protocolo de comunicación
 *
 * Verifica que:
 * - Los mensajes se serialicen correctamente a JSON
 * - Los mensajes deserializados sean equivalentes a los originales
 * - El formato JSON sea válido
 *
 * Estos tests se ejecutan en commonTest, por lo que funcionan
 * en todas las plataformas (Android, iOS, Desktop, Web).
 */
class GameProtocolTest {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
        allowStructuredMapKeys = true
    }

    // ============ ClientMessage Tests ============

    @Test
    fun `serialize and deserialize Heartbeat`() {
        val original = ClientMessage.Heartbeat
        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("Heartbeat"))
    }

    @Test
    fun `serialize and deserialize JoinMatchmaking`() {
        val original = ClientMessage.JoinMatchmaking(
            timeControl = TimeControl.BLITZ.key,
            rated = true
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains(TimeControl.BLITZ.key))
        assertTrue(jsonText.contains("rated"))
    }

    @Test
    fun `serialize and deserialize CancelMatchmaking`() {
        val original = ClientMessage.CancelMatchmaking(
            ticketId = "ticket_12345"
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("ticket_12345"))
    }

    @Test
    fun `serialize and deserialize MakeMove`() {
        val move = Move(GameBoard.C3 to GameBoard.B2)
        val original = ClientMessage.MakeMove(
            gameId = "game_abc",
            move = move
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("game_abc"))
    }

    @Test
    fun `serialize and deserialize Resign`() {
        val original = ClientMessage.Resign(
            gameId = "game_xyz"
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("game_xyz"))
    }

    @Test
    fun `serialize and deserialize OfferDraw`() {
        val original = ClientMessage.OfferDraw(
            gameId = "game_123"
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
    }

    @Test
    fun `serialize and deserialize RespondToDraw`() {
        val original = ClientMessage.RespondToDraw(
            gameId = "game_456",
            accept = true
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("true"))
    }

    // ============ ServerMessage Tests ============

    @Test
    fun `serialize and deserialize HeartbeatAck`() {
        val original = ServerMessage.HeartbeatAck
        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
    }

    @Test
    fun `serialize and deserialize MatchmakingStarted`() {
        val original = ServerMessage.MatchmakingStarted(
            ticketId = "ticket_abc123",
            estimatedWaitTime = 45
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("ticket_abc123"))
        assertTrue(jsonText.contains("45"))
    }

    @Test
    fun `serialize and deserialize MatchFound`() {
        val playerInfo = PlayerInfo(
            userId = "user_123",
            username = "TestPlayer",
            rating = 1500,
            country = "AR",
            title = null
        )

        val timeControl = TimeControlInfo(
            initial = 180,
            increment = 2,
            label = "3+2 Blitz"
        )

        val original = ServerMessage.MatchFound(
            gameId = "game_xyz",
            opponentInfo = playerInfo,
            yourColor = WHITE.description,
            timeControl = timeControl
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("TestPlayer"))
        assertTrue(jsonText.contains("white"))
        assertTrue(jsonText.contains("1500"))
    }

    @Test
    fun `serialize and deserialize GameEnded`() {
        val ratingUpdate = RatingUpdate(
            oldRating = 1500,
            newRating = 1515,
            change = 15
        )

        val original = ServerMessage.GameEnded(
            gameId = "game_123",
            result = GameResult.WHITE_WIN.key,
            reason = GameEndReason.MIT.key,
            newRatings = ratingUpdate
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains(GameResult.WHITE_WIN.key))
        assertTrue(jsonText.contains(GameEndReason.MIT.key))
        assertTrue(jsonText.contains("1515"))
    }

    @Test
    fun `serialize and deserialize OpponentDisconnected`() {
        val original = ServerMessage.OpponentDisconnected(
            gameId = "game_abc",
            gracePeriod = 60
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("60"))
    }

    @Test
    fun `serialize and deserialize ChatMessage`() {
        val original = ServerMessage.ChatMessage(
            roomId = "room_123",
            senderId = "user_456",
            senderName = "ChatUser",
            message = "Hello, world!",
            timestamp = 1234567890L
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("Hello, world!"))
        assertTrue(jsonText.contains("ChatUser"))
    }

    @Test
    fun `serialize and deserialize InvalidMove`() {
        val original = ServerMessage.InvalidMove(
            gameId = "game_xyz",
            reason = "Piece cannot move to that position"
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("Piece cannot move"))
    }

    @Test
    fun `serialize and deserialize Error`() {
        val original = ServerMessage.Error(
            code = "invalid_game_state",
            message = "Game is not in progress"
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("invalid_game_state"))
    }

    // ============ Data Classes Tests ============

    @Test
    fun `serialize and deserialize PlayerInfo`() {
        val original = PlayerInfo(
            userId = "user_789",
            username = "GrandMaster",
            rating = 2500,
            country = "US",
            title = "GM"
        )

        val jsonText = json.encodeToString(original)
        val decoded = json.decodeFromString<PlayerInfo>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("GrandMaster"))
        assertTrue(jsonText.contains("GM"))
    }

    @Test
    fun `serialize and deserialize TimeControlInfo`() {
        val original = TimeControlInfo(
            initial = 600,
            increment = 5,
            label = "10+5 Rapid"
        )

        val jsonText = json.encodeToString(original)
        val decoded = json.decodeFromString<TimeControlInfo>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("600"))
        assertTrue(jsonText.contains("Rapid"))
    }

    @Test
    fun `serialize and deserialize TimeRemaining`() {
        val original = TimeRemaining(
            whiteMs = 180000L,
            blackMs = 175000L
        )

        val jsonText = json.encodeToString(original)
        val decoded = json.decodeFromString<TimeRemaining>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("180000"))
        assertTrue(jsonText.contains("175000"))
    }

    @Test
    fun `serialize and deserialize RatingUpdate`() {
        val original = RatingUpdate(
            oldRating = 1400,
            newRating = 1420,
            change = 20
        )

        val jsonText = json.encodeToString(original)
        val decoded = json.decodeFromString<RatingUpdate>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("1400"))
        assertTrue(jsonText.contains("1420"))
    }

    // ============ Edge Cases ============

    @Test
    fun `PlayerInfo with null optional fields`() {
        val original = PlayerInfo(
            userId = "user_000",
            username = "Anonymous",
            rating = 1200,
            country = null,
            title = null
        )

        val jsonText = json.encodeToString(original)
        val decoded = json.decodeFromString<PlayerInfo>(jsonText)

        assertEquals(original, decoded)
        assertNotNull(decoded.username)
        assertEquals(1200, decoded.rating)
    }

    @Test
    fun `GameEnded with null rating update for casual game`() {
        val original = ServerMessage.GameEnded(
            gameId = "casual_game_123",
            result = GameResult.DRAW.key,
            reason = GameEndReason.DRAW_AGREEMENT.key,
            newRatings = null
        )

        val jsonText = json.encodeToString<ServerMessage>(original)
        val decoded = json.decodeFromString<ServerMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains(GameResult.DRAW.key))
    }

    @Test
    fun `RespondToDraw with false`() {
        val original = ClientMessage.RespondToDraw(
            gameId = "game_test",
            accept = false
        )

        val jsonText = json.encodeToString<ClientMessage>(original)
        val decoded = json.decodeFromString<ClientMessage>(jsonText)

        assertEquals(original, decoded)
        assertTrue(jsonText.contains("false"))
    }
}