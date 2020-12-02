package com.zing.zalo.zalorx

import com.zing.zalo.zalorx.schedulers.NewThreadWorker
import java.util.concurrent.TimeUnit

abstract class Scheduler {
    /**
     * Schedules the given task on this Scheduler without any time delay.
     *
     * @param run the task to execute
     *
     * @return the Disposable instance that let's one cancel this particular task.
     * @since 2.0
     */
    open fun schedule(run: Runnable): Disposable {
        return schedule(run, 0L, TimeUnit.NANOSECONDS)
    }

    open fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val w: Worker = createWorker()
        val task = DisposeTask(run, w)
        w.schedule(task, delay, unit)
        return task
    }

    abstract fun createWorker(): Worker

    abstract class Worker : Disposable {
        /**
         * Schedules a Runnable for execution without any time delay.
         *
         *
         * The default implementation delegates to [.schedule].
         *
         * @param run
         * Runnable to schedule
         * @return a Disposable to be able to unsubscribe the action (cancel it if not executed)
         */
        open fun schedule(run: Runnable): Disposable {
            return schedule(run, 0L, TimeUnit.NANOSECONDS)
        }

        /**
         * Schedules an Runnable for execution at some point in the future specified by a time delay
         * relative to the current time.
         *
         *
         * Note to implementors: non-positive `delayTime` should be regarded as non-delayed schedule, i.e.,
         * as if the [.schedule] was called.
         *
         * @param run
         * the Runnable to schedule
         * @param delay
         * time to "wait" before executing the action; non-positive values indicate an non-delayed
         * schedule
         * @param unit
         * the time unit of `delayTime`
         * @return a Disposable to be able to unsubscribe the action (cancel it if not executed)
         */
        abstract fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable
    }

    internal class DisposeTask(private val runnable: Runnable, private val w: Worker) :
        Disposable, Runnable {

        var runner: Thread? = null

        override fun run() {
            runner = Thread.currentThread()
            try {
                runnable.run()
            } finally {
                dispose()
                runner = null
            }
        }

        override fun dispose() {
            if (runner === Thread.currentThread() && w is NewThreadWorker) {
                w.shutdown()
            } else {
                w.dispose()
            }
        }

        override fun isDisposed() = w.isDisposed()
    }

}