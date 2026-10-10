package xyz.teamgravity.coresdkandroid.coroutine.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class UnconfinedTestDispatcherProvider(
    scheduler: TestCoroutineScheduler
) : DispatcherProvider {
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(scheduler)

    override val main: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}