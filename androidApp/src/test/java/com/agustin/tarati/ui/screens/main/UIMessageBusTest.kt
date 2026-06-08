package com.agustin.tarati.ui.screens.main

import com.agustin.tarati.services.notifications.UIMessage
import com.agustin.tarati.services.notifications.UIMessageBus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class UIMessageBusTest {

    private lateinit var bus: UIMessageBus

    @Before
    fun setUp() {
        bus = UIMessageBus()
    }

    // ── Toast ─────────────────────────────────────────────────────────────────

    @Test
    fun `toast sends message to channel`() = runTest {
        val toast = UIMessage.Toast("Hello")

        bus.toast(toast)

        assertEquals(toast, bus.toasts.receive())
    }

    @Test
    fun `multiple toasts are buffered in order`() = runTest {
        val t1 = UIMessage.Toast("first")
        val t2 = UIMessage.Toast("second")
        val t3 = UIMessage.Toast("third")

        bus.toast(t1)
        bus.toast(t2)
        bus.toast(t3)

        assertEquals(t1, bus.toasts.receive())
        assertEquals(t2, bus.toasts.receive())
        assertEquals(t3, bus.toasts.receive())
    }

    @Test
    fun `toast preserves message and duration`() = runTest {
        val toast = UIMessage.Toast(message = "rating update", duration = 6.seconds)

        bus.toast(toast)
        val received = bus.toasts.receive()

        assertEquals("rating update", received.message)
        assertEquals(6.seconds, received.duration)
    }

    @Test
    fun `toast preserves actions`() = runTest {
        var clicked = false
        val toast = UIMessage.Toast(
            message = "Rematch?",
            actions = listOf(
                com.agustin.tarati.services.notifications.MessageAction(
                    label = "Accept",
                    onClick = { clicked = true },
                )
            ),
        )

        bus.toast(toast)
        val received = bus.toasts.receive()

        assertEquals(1, received.actions.size)
        received.actions[0].onClick()
        assertTrue(clicked)
    }

    // ── Alert ─────────────────────────────────────────────────────────────────

    @Test
    fun `alert content is null initially`() {
        assertNull(bus.alertContent.value)
    }

    @Test
    fun `alert sets content`() {
        bus.alert { }

        assertNotNull(bus.alertContent.value)
    }

    @Test
    fun `clearAlert removes content`() {
        bus.alert { }
        assertNotNull(bus.alertContent.value)

        bus.clearAlert()

        assertNull(bus.alertContent.value)
    }

    @Test
    fun `second alert overwrites first`() {
        bus.alert { /* first */ }
        val firstContent = bus.alertContent.value

        bus.alert { /* second */ }
        val secondContent = bus.alertContent.value

        // Both non-null, but they should be different lambda instances
        assertNotNull(firstContent)
        assertNotNull(secondContent)
        assertTrue("Second call must replace first content", firstContent !== secondContent)
    }
}
