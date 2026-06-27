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
 * Tests de [SupporterViewModel] — flujo de pago Supporter vía Polar (proveedor activo Web/Desktop).
 *
 * El monto lo cobra Polar en su página, así que el ViewModel solo maneja intervalo + checkout.
 * `stripeAvailable` se inyecta en true para testear el checkout aunque el actual de Android sea false.
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
    fun `selectInterval updates state`() {
        val model = vm()
        model.selectInterval(SupporterInterval.MONTHLY)
        assertEquals(SupporterInterval.MONTHLY, model.uiState.value.interval)
    }

    @Test
    fun `checkout success emits the checkout url`(): TestResult = runTest {
        coEvery { repository.startPolarCheckout(SupporterInterval.ONCE) } returns "https://polar.sh/checkout/x"
        val model = vm()

        var emitted: String? = null
        backgroundScope.launch(Dispatchers.Main) { model.checkoutUrlEvent.collect { emitted = it } }

        model.checkout()

        assertEquals("https://polar.sh/checkout/x", emitted)
        assertNull(model.uiState.value.error)
    }

    @Test
    fun `checkout uses the selected interval`(): TestResult = runTest {
        coEvery { repository.startPolarCheckout(SupporterInterval.MONTHLY) } returns "https://polar.sh/checkout/m"
        val model = vm()
        model.selectInterval(SupporterInterval.MONTHLY)

        var emitted: String? = null
        backgroundScope.launch(Dispatchers.Main) { model.checkoutUrlEvent.collect { emitted = it } }

        model.checkout()

        assertEquals("https://polar.sh/checkout/m", emitted)
    }

    @Test
    fun `checkout failure sets error`(): TestResult = runTest {
        coEvery { repository.startPolarCheckout(any()) } returns null
        val model = vm()

        model.checkout()

        assertEquals(SupporterViewModel.ERROR_CHECKOUT_FAILED, model.uiState.value.error)
    }

    @Test
    fun `checkout is a no-op when web checkout is unavailable`(): TestResult = runTest {
        val model = vm(available = false)

        model.checkout()

        io.mockk.coVerify(exactly = 0) { repository.startPolarCheckout(any()) }
    }

    @Test
    fun `isSupporter reflects the entitlements flow`() {
        val model = vm()
        assertTrue(!model.uiState.value.isSupporter)

        entitlements.value = setOf("supporter")

        assertTrue(model.uiState.value.isSupporter)
    }
}
