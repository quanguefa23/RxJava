package com.zing.zalo.zalorx.schedulers

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong


/**
 * A ThreadFactory that counts how many threads have been created and given a prefix,
 * sets the created Thread's name to `prefix-count`.
 */
class RxThreadFactory
@JvmOverloads constructor(
    val prefix: String,
    val priority: Int = Thread.NORM_PRIORITY,
    val nonBlocking: Boolean = false
) :
    AtomicLong(), ThreadFactory {

    override fun newThread(r: Runnable): Thread {
        val nameBuilder = StringBuilder(prefix).append('-').append(incrementAndGet())

        val name = nameBuilder.toString()
        val t = if (nonBlocking) RxCustomThread(r, name) else Thread(r, name)
        t.priority = priority
        t.isDaemon = true
        return t
    }

    override fun toByte() = Byte.MAX_VALUE

    override fun toChar() = Char.MAX_VALUE

    override fun toShort() = Short.MAX_VALUE

    override fun toString(): String {
        return "RxThreadFactory[$prefix]"
    }

    internal class RxCustomThread(run: Runnable?, name: String?) :
        Thread(run, name), NonBlockingThread

    interface NonBlockingThread
}
