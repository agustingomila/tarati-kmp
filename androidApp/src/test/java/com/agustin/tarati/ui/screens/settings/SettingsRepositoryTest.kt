package com.agustin.tarati.ui.screens.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.features.settings.AndroidSettingsRepository
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.ui.components.game.draw.pieces.ConversionAnimationStyle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {
    @Test
    fun isDarkTheme_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.DARK_THEME_KEY] } returns true
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.isDarkTheme.take(1).toList()[0]

            assertTrue("Dark theme should be enabled", result)
        }

    @Test
    fun isDarkTheme_returnsTrueWhenNotSet() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.DARK_THEME_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.isDarkTheme.take(1).toList()[0]

            assertTrue("Dark theme should be enabled by default", result)
        }

    @Test
    fun difficulty_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.DIFFICULTY_KEY] } returns Difficulty.HARD.ordinal
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.difficulty.take(1).toList()[0]

            assertEquals("Difficulty should be HARD", Difficulty.HARD, result)
        }

    @Test
    fun difficulty_returnsDefaultWhenNotSet() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.DIFFICULTY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.difficulty.take(1).toList()[0]

            assertEquals("Difficulty should be DEFAULT", Difficulty.DEFAULT, result)
        }

    @Test
    fun userName_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.USER_NAME_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.userName.take(1).toList()[0]

            assertEquals("Username should be empty", "", result)
        }

    @Test
    fun userName_returnsDefaultWhenNotSet() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.USER_NAME_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.userName.take(1).toList()[0]

            assertEquals("Username should be empty by default", "", result)
        }

    @Test
    fun language_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.LANGUAGE_KEY] } returns AppLanguage.ENGLISH.name
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.language.take(1).toList()[0]

            assertEquals("Language should be ENGLISH", AppLanguage.ENGLISH, result)
        }

    @Test
    fun language_returnsDefaultWhenNotSet() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.LANGUAGE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.language.take(1).toList()[0]

            assertEquals("Language should be SPANISH by default", AppLanguage.SPANISH, result)
        }

    @Test
    fun labelsVisibility_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.LABELS_VISIBILITY_KEY] } returns true
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.labelsVisibility.take(1).toList()[0]

            assertEquals("Labels should be visible", true, result)
        }

    @Test
    fun labelsVisibility_returnsDefaultWhenNotSet() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.LABELS_VISIBILITY_KEY] } returns true
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.labelsVisibility.take(1).toList()[0]

            assertTrue("Labels should be visible by default", result)
        }

    // ── conversionAnimationStyle ──────────────────────────────────────────────

    @Test
    fun conversionAnimationStyle_returnsTransformation() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.CONVERSION_ANIMATION_STYLE_KEY] } returns
                    ConversionAnimationStyle.TRANSFORMATION.name
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.conversionAnimationStyle.take(1).toList()[0]

            assertEquals(
                "Style should be TRANSFORMATION",
                ConversionAnimationStyle.TRANSFORMATION,
                result,
            )
        }

    @Test
    fun conversionAnimationStyle_returnsFlip() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.CONVERSION_ANIMATION_STYLE_KEY] } returns
                    ConversionAnimationStyle.FLIP.name
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.conversionAnimationStyle.take(1).toList()[0]

            assertEquals("Style should be FLIP", ConversionAnimationStyle.FLIP, result)
        }

    @Test
    fun conversionAnimationStyle_returnsSurpriseByDefault() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.CONVERSION_ANIMATION_STYLE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.conversionAnimationStyle.take(1).toList()[0]

            assertEquals(
                "Style should be SURPRISE by default",
                ConversionAnimationStyle.SURPRISE,
                result,
            )
        }

    @Test
    fun conversionAnimationStyle_returnsSurpriseOnUnknownValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.CONVERSION_ANIMATION_STYLE_KEY] } returns
                    "INVALID_VALUE"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.conversionAnimationStyle.take(1).toList()[0]

            assertEquals(
                "Unknown value should fall back to SURPRISE",
                ConversionAnimationStyle.SURPRISE,
                result,
            )
        }

    @Test
    fun setConversionAnimationStyle_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.CONVERSION_ANIMATION_STYLE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setConversionAnimationStyle(ConversionAnimationStyle.FLIP)

            coVerify { mockDataStore.updateData(any()) }
        }

    // ── Tests de escritura ────────────────────────────────────────────────────

    @Test
    fun setDarkTheme_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.DARK_THEME_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setDarkTheme(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setDifficulty_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.DIFFICULTY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setDifficulty(Difficulty.DEFAULT)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setLabelsVisibility_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.LABELS_VISIBILITY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setLabelsVisibility(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setVerticesVisibility_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.VERTICES_VISIBILITY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setVerticesVisibility(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setEdgesVisibility_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.EDGES_VISIBILITY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setEdgesVisibility(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setRegionsVisibility_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.REGIONS_VISIBILITY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setRegionsVisibility(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setPerimeterVisibility_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PERIMETER_VISIBILITY_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setPerimeterVisibility(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun setAnimateEffects_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.ANIMATE_EFFECTS_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setAnimateEffects(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    // --- tutorialSeen ---

    @Test
    fun tutorialSeen_returnsFalseByDefault() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TUTORIAL_SEEN_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.tutorialSeen.take(1).toList()[0]

            assertFalse("Tutorial should not be seen by default", result)
        }

    @Test
    fun tutorialSeen_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TUTORIAL_SEEN_KEY] } returns true
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.tutorialSeen.take(1).toList()[0]

            assertTrue("Tutorial should be marked as seen", result)
        }

    @Test
    fun setTutorialSeen_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TUTORIAL_SEEN_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setTutorialSeen(true)

            coVerify { mockDataStore.updateData(any()) }
        }

    // --- seasonal theme ---

    @Test
    fun seasonalAutoAppliedDate_returnsEmptyStringByDefault() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.SEASONAL_AUTO_APPLIED_DATE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.seasonalAutoAppliedDate.take(1).toList()[0]

            assertEquals("Seasonal auto applied date should be empty by default", "", result)
        }

    @Test
    fun seasonalAutoAppliedDate_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.SEASONAL_AUTO_APPLIED_DATE_KEY] } returns "10-31"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.seasonalAutoAppliedDate.take(1).toList()[0]

            assertEquals("Seasonal auto applied date should be 10-31", "10-31", result)
        }

    @Test
    fun setSeasonalAutoAppliedDate_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.SEASONAL_AUTO_APPLIED_DATE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setSeasonalAutoAppliedDate("10-31")

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun preSeasonalPalette_returnsEmptyStringByDefault() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_SEASONAL_PALETTE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.preSeasonalPalette.take(1).toList()[0]

            assertEquals("Pre-seasonal palette should be empty by default", "", result)
        }

    @Test
    fun preSeasonalPalette_returnsStoredValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_SEASONAL_PALETTE_KEY] } returns "Classic"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.preSeasonalPalette.take(1).toList()[0]

            assertEquals("Pre-seasonal palette should be Classic", "Classic", result)
        }

    @Test
    fun setPreSeasonalPalette_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_SEASONAL_PALETTE_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setPreSeasonalPalette("Classic")

            coVerify { mockDataStore.updateData(any()) }
        }

    @Test
    fun clearPreSeasonalPalette_removesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_SEASONAL_PALETTE_KEY] } returns "Classic"
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.clearPreSeasonalPalette()

            coVerify { mockDataStore.updateData(any()) }
        }

    // ── timeControl ───────────────────────────────────────────────────────────

    @Test
    fun timeControl_returnsUnlimitedByDefault() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                "Time control should be Unlimited by default",
                TimeControlMode.Unlimited,
                result,
            )
        }

    @Test
    fun timeControl_returnsStoredFischerMode() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns "fischer:180000:2000"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                "Time control should deserialize to Fischer(180000, 2000)",
                TimeControlMode.Fischer(baseMs = 180_000L, incrementMs = 2_000L),
                result,
            )
        }

    @Test
    fun timeControl_returnsStoredSuddenDeathMode() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns "sudden:300000"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                TimeControlMode.SuddenDeath(totalMs = 300_000L),
                result,
            )
        }

    @Test
    fun timeControl_returnsStoredBronsteinMode() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns "bronstein:300000:3000"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                TimeControlMode.Bronstein(baseMs = 300_000L, delayMs = 3_000L),
                result,
            )
        }

    @Test
    fun timeControl_returnsStoredByoyomiMode() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns "byoyomi:300000:30000:3"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                TimeControlMode.Byoyomi(baseMs = 300_000L, periodMs = 30_000L, periods = 3),
                result,
            )
        }

    @Test
    fun timeControl_returnsUnlimitedOnMalformedString() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns "garbage_not_a_mode"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                "Malformed string should fall back to Unlimited",
                TimeControlMode.Unlimited,
                result,
            )
        }

    @Test
    fun timeControl_returnsUnlimitedOnIncompleteFischerString() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            // "fischer" requires two params — missing increment causes parse error.
            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns "fischer:180000"
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.timeControl.take(1).toList()[0]

            assertEquals(
                "Incomplete Fischer string should fall back to Unlimited",
                TimeControlMode.Unlimited,
                result,
            )
        }

    @Test
    fun setTimeControl_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.TIME_CONTROL_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setTimeControl(
                TimeControlMode.Fischer(baseMs = 600_000L, incrementMs = 5_000L),
            )

            coVerify { mockDataStore.updateData(any()) }
        }

    // ── preMovesEnabled ───────────────────────────────────────────────────────

    @Test
    fun preMovesEnabled_returnsTrueByDefault() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_MOVES_ENABLED_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.preMovesEnabled.take(1).toList()[0]

            assertTrue("Pre-moves should be enabled by default", result)
        }

    @Test
    fun preMovesEnabled_returnsStoredFalse() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_MOVES_ENABLED_KEY] } returns false
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.preMovesEnabled.take(1).toList()[0]

            assertFalse("Pre-moves should reflect stored false value", result)
        }

    @Test
    fun preMovesEnabled_returnsStoredTrue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_MOVES_ENABLED_KEY] } returns true
            every { mockDataStore.data } returns flowOf(mockPreferences)

            val repository = AndroidSettingsRepository(mockDataStore)
            val result = repository.preMovesEnabled.take(1).toList()[0]

            assertTrue(result)
        }

    @Test
    fun setPreMovesEnabled_savesValue() =
        runTest {
            val mockDataStore = mockk<DataStore<Preferences>>()
            val mockPreferences = mockk<Preferences>(relaxed = true)

            every { mockPreferences[AndroidSettingsRepository.PRE_MOVES_ENABLED_KEY] } returns null
            every { mockDataStore.data } returns flowOf(mockPreferences)
            coEvery { mockDataStore.updateData(any()) } returns mockk()

            val repository = AndroidSettingsRepository(mockDataStore)
            repository.setPreMovesEnabled(false)

            coVerify { mockDataStore.updateData(any()) }
        }
}