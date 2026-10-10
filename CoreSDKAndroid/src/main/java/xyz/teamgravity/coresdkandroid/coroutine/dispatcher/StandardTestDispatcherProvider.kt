package xyz.teamgravity.coresdkandroid.coroutine.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher

class StandardTestDispatcherProvider(
    scheduler: TestCoroutineScheduler
) : DispatcherProvider {
    private val dispatcher: TestDispatcher = StandardTestDispatcher(scheduler)

    override val main: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}