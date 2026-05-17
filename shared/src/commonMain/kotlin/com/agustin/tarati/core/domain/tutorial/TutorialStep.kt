package com.agustin.tarati.core.domain.tutorial


import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C12
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C4
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D2
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.createGameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.tutorial_basic_moves_description
import com.agustin.tarati.shared.generated.resources.tutorial_basic_moves_title
import com.agustin.tarati.shared.generated.resources.tutorial_bridge_description
import com.agustin.tarati.shared.generated.resources.tutorial_bridge_title
import com.agustin.tarati.shared.generated.resources.tutorial_captures_description
import com.agustin.tarati.shared.generated.resources.tutorial_captures_title
import com.agustin.tarati.shared.generated.resources.tutorial_center_description
import com.agustin.tarati.shared.generated.resources.tutorial_center_title
import com.agustin.tarati.shared.generated.resources.tutorial_circumference_description
import com.agustin.tarati.shared.generated.resources.tutorial_circumference_title
import com.agustin.tarati.shared.generated.resources.tutorial_cobs_description
import com.agustin.tarati.shared.generated.resources.tutorial_cobs_title
import com.agustin.tarati.shared.generated.resources.tutorial_completed_description
import com.agustin.tarati.shared.generated.resources.tutorial_completed_title
import com.agustin.tarati.shared.generated.resources.tutorial_dead_piece_description
import com.agustin.tarati.shared.generated.resources.tutorial_dead_piece_title
import com.agustin.tarati.shared.generated.resources.tutorial_domestic_bases_description
import com.agustin.tarati.shared.generated.resources.tutorial_domestic_bases_title
import com.agustin.tarati.shared.generated.resources.tutorial_domestic_capture_description
import com.agustin.tarati.shared.generated.resources.tutorial_domestic_capture_title
import com.agustin.tarati.shared.generated.resources.tutorial_end_conditions_description
import com.agustin.tarati.shared.generated.resources.tutorial_end_conditions_title
import com.agustin.tarati.shared.generated.resources.tutorial_introduction_description
import com.agustin.tarati.shared.generated.resources.tutorial_introduction_title
import com.agustin.tarati.shared.generated.resources.tutorial_pre_adjacency_description
import com.agustin.tarati.shared.generated.resources.tutorial_pre_adjacency_title
import com.agustin.tarati.shared.generated.resources.tutorial_upgrade_description
import com.agustin.tarati.shared.generated.resources.tutorial_upgrade_title
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import com.agustin.tarati.ui.components.game.highlights.sequences.createBridgeAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createCaptureAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createCenterAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createCircumferenceAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createCobsAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createDeadPieceAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createDomesticAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createDomesticCaptureAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createEndConditionsAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createMoveAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createPreAdjacencyAnimations
import com.agustin.tarati.ui.components.game.highlights.sequences.createUpgradeAnimations
import org.jetbrains.compose.resources.StringResource

abstract class TutorialStep(
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val animations: List<List<HighlightAnimation>> = emptyList(),
    val gameState: GameState,
    val autoAdvanceDelay: Long? = null,
    val onStepStart: (() -> Unit)? = null,
)

abstract class InteractiveTutorialStep(
    titleRes: StringResource,
    descriptionRes: StringResource,
    animations: List<List<HighlightAnimation>> = emptyList(),
    gameState: GameState,
    val expectedMoves: List<Move> = listOf(),
    val validateMove: ((Move) -> Boolean) = { true },
    onStepStart: (() -> Unit)? = null,
) : TutorialStep(
    titleRes = titleRes,
    descriptionRes = descriptionRes,
    animations = animations,
    gameState = gameState,
    onStepStart = onStepStart,
) {
    fun isExpectedMove(move: Move): Boolean =
        if (expectedMoves.isNotEmpty()) {
            expectedMoves.contains(move)
        } else {
            validateMove(move)
        }
}

class IntroductionStep :
    TutorialStep(
        titleRes = Res.string.tutorial_introduction_title,
        descriptionRes = Res.string.tutorial_introduction_description,
        animations = emptyList(),
        gameState = createGameState { clearCobs() },
        autoAdvanceDelay = 4000L,
    )

/**
 * Explains the three ways the game can end, using the initial board layout as context.
 *
 * Non-interactive, auto-advances. The animation highlights both home bases and then
 * all 8 starting vertices together to reinforce the idea that controlling all pieces
 * decides the game.
 *
 *  - Mit: the last enemy piece is converted in a single move.
 *  - Stalemit: the opponent has no legal moves (including forced promotions).
 *  - Triple repetition: the player who causes the third repetition loses.
 */
class EndConditionsStep :
    TutorialStep(
        titleRes = Res.string.tutorial_end_conditions_title,
        descriptionRes = Res.string.tutorial_end_conditions_description,
        animations = createEndConditionsAnimations(
            600L
        ),
        gameState = createGameState { initialGameState() },
        autoAdvanceDelay = 7000L,
    )

class CompletedStep :
    TutorialStep(
        titleRes = Res.string.tutorial_completed_title,
        descriptionRes = Res.string.tutorial_completed_description,
        animations = emptyList(),
        gameState = createGameState { initialGameState() },
        autoAdvanceDelay = 4000L,
    )

class CenterStep :
    TutorialStep(
        titleRes = Res.string.tutorial_center_title,
        descriptionRes = Res.string.tutorial_center_description,
        animations = createCenterAnimations(
            600L
        ),
        gameState = createGameState { clearCobs() },
        autoAdvanceDelay = 6000L,
    )

class BridgeStep :
    TutorialStep(
        titleRes = Res.string.tutorial_bridge_title,
        descriptionRes = Res.string.tutorial_bridge_description,
        animations = createBridgeAnimations(
            300L
        ),
        gameState = createGameState { clearCobs() },
        autoAdvanceDelay = 10000L,
    )

class CircumferenceStep :
    TutorialStep(
        titleRes = Res.string.tutorial_circumference_title,
        descriptionRes = Res.string.tutorial_circumference_description,
        animations = createCircumferenceAnimations(
            200L
        ),
        gameState = createGameState { clearCobs() },
        autoAdvanceDelay = 6000L,
    )

class DomesticBasesStep :
    TutorialStep(
        titleRes = Res.string.tutorial_domestic_bases_title,
        descriptionRes = Res.string.tutorial_domestic_bases_description,
        animations = createDomesticAnimations(
            600L
        ),
        gameState = createGameState { clearCobs() },
        autoAdvanceDelay = 6000L,
    )

class CobsStep :
    TutorialStep(
        titleRes = Res.string.tutorial_cobs_title,
        descriptionRes = Res.string.tutorial_cobs_description,
        animations = createCobsAnimations(
            800L
        ),
        gameState =
            createGameState {
                clearCobs()
                setCob(C1, Cob(WHITE, false))
                setCob(C2, Cob(WHITE, false))
                setCob(D1, Cob(WHITE, false))
                setCob(D2, Cob(WHITE, false))
            },
        autoAdvanceDelay = 6000L,
    )

class BasicMovesStep :
    InteractiveTutorialStep(
        titleRes = Res.string.tutorial_basic_moves_title,
        descriptionRes = Res.string.tutorial_basic_moves_description,
        animations = createMoveAnimations(
            600L
        ),
        gameState =
            createGameState {
                clearCobs()
                setCob(C1, Cob(WHITE, false))
                setCob(C2, Cob(WHITE, false))
                setCob(D1, Cob(WHITE, false))
                setCob(D2, Cob(WHITE, false))
            },
        expectedMoves =
            listOf(
                Move(C1 to B1),
                Move(C2 to B1),
                Move(C1 to C12),
                Move(C2 to C3),
            ),
    )

class CapturesStep :
    InteractiveTutorialStep(
        titleRes = Res.string.tutorial_captures_title,
        descriptionRes = Res.string.tutorial_captures_description,
        animations = createCaptureAnimations(
            600L
        ),
        gameState =
            createGameState {
                clearCobs()
                setCob(C1, Cob(WHITE, false))
                setCob(A1, Cob(BLACK, false))
            },
        expectedMoves = listOf(Move(C1 to B1)),
    )

/**
 * Demonstrates the pre-adjacency rule with an interactive move.
 *
 * Board: White Cob at C3, Black Cob at C4 (pre-adjacent — will NOT be captured),
 *        Black Cob at B3 (not pre-adjacent to C3 — WILL be captured).
 *
 * The player moves C3→B2. After the move:
 *   - C4 stays Black (it was adjacent to C3 before the move).
 *   - B3 flips to White (it was only adjacent to the destination B2).
 *
 * This is the most important tactical rule: you must approach from outside.
 */
class PreAdjacencyStep :
    InteractiveTutorialStep(
        titleRes = Res.string.tutorial_pre_adjacency_title,
        descriptionRes = Res.string.tutorial_pre_adjacency_description,
        animations = createPreAdjacencyAnimations(
            600L
        ),
        gameState =
            createGameState {
                clearCobs()
                setCob(C3, Cob(WHITE, false))  // attacker
                setCob(C4, Cob(BLACK, false))  // pre-adjacent — NOT captured
                setCob(B3, Cob(BLACK, false))  // not pre-adjacent — WILL be captured
            },
        expectedMoves = listOf(Move(C3 to B2)),
    )

class UpgradeStep :
    InteractiveTutorialStep(
        titleRes = Res.string.tutorial_upgrade_title,
        descriptionRes = Res.string.tutorial_upgrade_description,
        animations = createUpgradeAnimations(
            600L
        ),
        gameState =
            createGameState {
                clearCobs()
                setCob(B4, Cob(WHITE, false))
            },
        expectedMoves =
            listOf(
                Move(B4 to C7),
                Move(B4 to C8),
            ),
    )

/**
 * Explains dead pieces and forced promotion (§6.3 / §6.4).
 *
 * A cob is "dead" when it has no forward moves — either because it sits on one of
 * the opponent's two outermost home-base vertices (primary dead), or because every
 * forward-adjacent vertex is occupied by another dead cob of the same color (dead by
 * proxy). Roks are never dead.
 *
 * When a player has NO normal moves but has at least one dead cob that would gain
 * moves after promotion, they must promote one of those cobs in-place to a rok.
 * This avoids STALEMIT (§7.1.2) and keeps the game alive.
 *
 * Interactive: the player performs the in-place promotion move (D3 → D3) to
 * experience the mechanic directly. The initial board has a white cob at D3
 * (a primary dead vertex for white) with black cobs at D4 and C7 blocking all exits.
 */
class DeadPieceStep :
    InteractiveTutorialStep(
        titleRes = Res.string.tutorial_dead_piece_title,
        descriptionRes = Res.string.tutorial_dead_piece_description,
        animations = createDeadPieceAnimations(
            600L
        ),
        gameState =
            createGameState {
                clearCobs()
                // White cob at D3 — primary dead vertex for white (deepest inside black base)
                setCob(D3, Cob(WHITE, false))
                // Black cobs seal both exits — D4 and C7 are D3's only neighbors
                setCob(D4, Cob(BLACK, false))
                setCob(C7, Cob(BLACK, false))
            },
        // The only legal move is the in-place promotion: D3 → D3
        expectedMoves = listOf(Move(D3 to D3)),
    )

class DomesticCaptureStep :
    InteractiveTutorialStep(
        titleRes = Res.string.tutorial_domestic_capture_title,
        descriptionRes = Res.string.tutorial_domestic_capture_description,
        animations = createDomesticCaptureAnimations(
            600L
        ),
        gameState =
            createGameState {
                clearCobs()
                setCob(D2, Cob(WHITE, false))
                setCob(C1, Cob(BLACK, false))
            },
        expectedMoves = listOf(Move(D2 to C2), Move(D2 to D1)),
    )