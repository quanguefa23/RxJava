package com.zing.zalo.zalorx.schedulers

import com.zing.zalo.zalorx.Disposable
import com.zing.zalo.zalorx.Scheduler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class ComputationScheduler: Scheduler() {

    companion object {
        /** Manages a fixed number of workers.  */
        private val THREAD_NAME_PREFIX = "RxComputationThreadPool"
        var THREAD_FACTORY: RxThreadFactory

        /** The maximum number of computation scheduler threads.  */
        var MAX_THREADS = 0

        var SHUTDOWN_WORKER: PoolWorker

        init {
            MAX_THREADS = Runtime.getRuntime().availableProcessors()

            SHUTDOWN_WORKER = PoolWorker(RxThreadFactory("RxComputationShutdown"))
            SHUTDOWN_WORKER.dispose()

            val priority = Thread.NORM_PRIORITY

            THREAD_FACTORY = RxThreadFactory(THREAD_NAME_PREFIX, priority, true)
        }
    }

    val pool: AtomicReference<FixedSchedulerPool> = AtomicReference<FixedSchedulerPool>(
        FixedSchedulerPool(MAX_THREADS, THREAD_FACTORY)
    )

    class FixedSchedulerPool(private val cores: Int, threadFactory: ThreadFactory) {

        private val eventLoops: Array<PoolWorker>
        private var n: Int = 0

        init {
            // initialize event loops
            val emptyValue = PoolWorker(threadFactory)
            eventLoops = Array(cores) { emptyValue }
            for (i in 0 until cores) {
                eventLoops[i] = PoolWorker(threadFactory)
            }
        }

        fun getEventLoop(): PoolWorker {
            return if (cores == 0) {
                SHUTDOWN_WORKER
            }
            else {
                n = (n + 1) % cores
                eventLoops[n]
            } // simple round robin, improvements to come
        }

        fun shutdown() {
            for (w in eventLoops) {
                w.dispose()
            }
        }
    }

    class PoolWorker(threadFactory: ThreadFactory) :
        NewThreadWorker(threadFactory)

    override fun createWorker() = pool.get().getEventLoop()

    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val w: PoolWorker = pool.get().getEventLoop()
        return w.schedule(run, delay, unit)
    }
}