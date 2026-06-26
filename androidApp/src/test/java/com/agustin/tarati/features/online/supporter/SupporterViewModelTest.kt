@file:OptIn(ExperimentalCoroutinesApi::class)

package com.agustin.tarati.features.online.supporter

import com.agustin.tarati.services.billing.EntitlementsRepository
import com.agustin.tarati.services.billing.SupporterInterval
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests de [SupporterViewModel] — flujo de pago Supporter (C3).
 *
 * Mockea [EntitlementsRepository] con MockK. `stripeAvailable` se inyecta en true para
 * poder testear el checkout aunque el actual de Android sea false.
 */
class SupporterViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: EntitlementsRepository = mockk(relaxed = true)
    private val entitlements = MutableStateFlow<Set<String>>(emptySet())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        io.mockk.every { repository.entitlements } returns entitlements
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun vm(available: Boolean = true) =
        SupporterViewModel(entitlementsRepository = repository, stripeAvailable = available)

    @Test
    fun `parseDollarsToCents parses dollars and rejects garbage`() {
        assertEquals(500, SupporterViewModel.parseDollarsToCents("5"))
        assertEquals(550, SupporterViewModel.parseDollarsToCents("5.50"))
        assertEquals(550, SupporterViewModel.parseDollarsToCents("5,50"))
        assertNull(SupporterViewModel.parseDollarsToCents(""))
        assertNull(SupporterViewModel.parseDollarsToCents("abc"))
        assertNull(SupporterViewModel.parseDollarsToCents("0"))
    }

    @Test
    fun `selectPreset updates amount`() {
        val model = vm()
        model.selectPreset(1000)
        assertEquals(1000, model.uiState.value.amountCents)
    }

    @Test
    fun `setCustomAmount parses to cents`() {
        val model = vm()
        model.setCustomAmount("7.50")
        assertEquals(750, model.uiState.value.amountCents)
        assertEquals("7.50", model.uiState.value.customAmountText)
    }

    @Test
    fun `checkout below minimum sets error and does not call the server`(): TestResult = runTest {
        val model = vm()
        model.setCustomAmount("1") // $1 < $2 min

        model.checkout()

        assertEquals(SupporterViewModel.ERROR_MIN_AMOUNT, model.uiState.value.error)
        io.mockk.coVerify(exactly = 0) { repository.startStripeCheckout(any(), any()) }
    }

    @Test
    fun `checkout success emits the checkout url`(): TestResult = runTest {
        coEvery { repository.startStripeCheckout(500, SupporterInterval.ONCE) } returns "https://checkout.stripe.com/x"
        val model = vm()

        var emitted: String? = null
        // Colectar en el mismo dispatcher Main del viewModelScope para que el emit no se pierda.
        backgroundScope.launch(Dispatchers.Main) { model.checkoutUrlEvent.collect { emitted = it } }

        model.checkout()

        assertEquals("https://checkout.stripe.com/x", emitted)
        assertNull(model.uiState.value.error)
    }

    @Test
    fun `checkout failure sets error`(): TestResult = runTest {
        coEvery { repository.startStripeCheckout(any(), any()) } returns null
        val model = vm()

        model.checkout()

        assertEquals(SupporterViewModel.ERROR_CHECKOUT_FAILED, model.uiState.value.error)
    }

    @Test
    fun `isSupporter reflects the entitlements flow`() {
        val model = vm()
        assertTrue(!model.uiState.value.isSupporter)

        entitlements.value = setOf("supporter")

        assertTrue(model.uiState.value.isSupporter)
    }
}
