package com.zing.zalo.zalorx.schedulers

import com.zing.zalo.zalorx.Disposable
import com.zing.zalo.zalorx.Scheduler
import java.util.concurrent.*

open class NewThreadWorker(private val factory: ThreadFactory): Scheduler.Worker(), Disposable {

    private var executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1, factory)

    @Volatile
    private var disposed = false


    override fun schedule(run: Runnable, delayTime: Long, unit: TimeUnit): Disposable {
        return if (disposed) {
            EmptyDisposable
        } else {
            return try {
                val f: Future<*> = if (delayTime <= 0L) {
                    executor.submit(run)
                } else {
                    executor.schedule(run, delayTime, unit)
                }
                run.setFuture(f)
                task
            } catch (ex: RejectedExecutionException) {
                RxJavaPlugins.onError(ex)
                io.reactivex.internal.disposables.EmptyDisposable.INSTANCE
            }
        }
    }

    override fun dispose() {
        if (!disposed) {
            disposed = true
            executor.shutdownNow()
        }
    }

    override fun isDisposed() = disposed

    fun shutdown() {
        if (!disposed) {
            disposed = true
            executor.shutdown()
        }
    }
}