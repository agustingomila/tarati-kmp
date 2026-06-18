package com.agustin.tarati.features.online.connection

import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.auth.UserInfo
import com.agustin.tarati.network.client.TaratiWebSocketClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests unitarios para ConnectionViewModel
 *
 * Verifica:
 * - Conexión al servidor
 * - Desconexión
 * - Reintento de conexión
 * - Sincronización con WebSocketClient
 * - Manejo de errores
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionViewModelTest {

    private lateinit var mockAuthViewModel: IAuthViewModel
    private lateinit var mockWsClient: TaratiWebSocketClient
    private lateinit var viewModel: ConnectionViewModel
    private val testDispatcher = StandardTestDispatcher()

    /** UserInfo del jugador autenticado en todos los tests. */
    private val testUserInfo = UserInfo(
        userId = "user-123",
        username = "test_player",
        displayName = "Test Player",
        rating = 1500,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockWsClient = mockk(relaxed = true) {
            every { connectionState } returns MutableStateFlow(
                TaratiWebSocketClient.ConnectionState.Disconnected
            )
        }

        // IAuthViewModel mockeado para devolver siempre un usuario autenticado.
        // Esto permite que ConnectionViewModel.resolveUserInfo() obtenga datos reales
        // sin necesitar un ciclo completo de autenticación JWT en el test.
        mockAuthViewModel = mockk(relaxed = true) {
            every { currentUser } returns testUserInfo
        }

        viewModel = ConnectionViewModel(mockWsClient, mockAuthViewModel)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Offline`() {
        assertTrue(viewModel.connectionState.value is ConnectionState.Offline)
    }

    @Test
    fun `connectToServer updates state to Connecting then Online`() = runTest {
        coEvery { mockWsClient.connect(any()) } returns Unit

        val result = viewModel.connectToServer(
            serverUrl = "localhost:8080",
            authToken = "test_token"
        )
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertTrue(viewModel.connectionState.value is ConnectionState.Online)

        val onlineState = viewModel.connectionState.value as ConnectionState.Online
        assertEquals("test_player", onlineState.userInfo.username)
        assertEquals(1500, onlineState.userInfo.rating)

        coVerify { mockWsClient.connect(any()) }
    }

    @Test
    fun `connectToServer with error updates state to Error`() = runTest {
        coEvery { mockWsClient.connect(any()) } throws Exception("Connection failed")

        val result = viewModel.connectToServer(
            serverUrl = "localhost:8080",
            authToken = "test_token"
        )
        advanceUntilIdle()

        assertTrue(result.isFailure)
        assertTrue(viewModel.connectionState.value is ConnectionState.Error)

        val errorState = viewModel.connectionState.value as ConnectionState.Error
        assertEquals("Connection failed", errorState.message)
        assertTrue(errorState.isRecoverable)
    }

    @Test
    fun `disconnect updates state to Offline`() = runTest {
        coEvery { mockWsClient.connect(any()) } returns Unit
        viewModel.connectToServer("localhost:8080", "test_token")
        advanceUntilIdle()

        viewModel.disconnect()
        advanceUntilIdle()

        assertTrue(viewModel.connectionState.value is ConnectionState.Offline)
        coVerify { mockWsClient.disconnect() }
    }

    @Test
    fun `retryConnection uses cached credentials`() = runTest {
        coEvery { mockWsClient.connect(any()) } throws Exception("First attempt failed")
        viewModel.connectToServer("localhost:8080", "test_token")
        advanceUntilIdle()

        coEvery { mockWsClient.connect(any()) } returns Unit

        viewModel.retryConnection()
        advanceUntilIdle()

        assertTrue(viewModel.connectionState.value is ConnectionState.Online)
        coVerify(exactly = 2) { mockWsClient.connect(any()) }
    }

    @Test
    fun `isConnected returns true when Online`() = runTest {
        coEvery { mockWsClient.connect(any()) } returns Unit

        viewModel.connectToServer("localhost:8080", "test_token")
        advanceUntilIdle()

        assertTrue(viewModel.isConnected)
    }

    @Test
    fun `isConnected returns false when Offline`() {
        assertFalse(viewModel.isConnected)
    }
}
