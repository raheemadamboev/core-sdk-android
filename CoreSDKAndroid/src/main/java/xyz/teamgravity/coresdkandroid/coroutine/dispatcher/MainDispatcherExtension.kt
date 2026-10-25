package xyz.teamgravity.coresdkandroid.coroutine.dispatcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension that replaces Dispatchers.Main with [dispatcher] before each test and restores it after.
 *
 * Test dispatcher providers built from the scheduler of [dispatcher] share one virtual clock with Dispatchers.Main and
 * runTest.
 *
 * @property dispatcher
 * Dispatcher that stands in for Dispatchers.Main. UnconfinedTestDispatcher by default.
 */
class MainDispatcherExtension(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext) {
        Dispatchers.resetMain()
    }
}
