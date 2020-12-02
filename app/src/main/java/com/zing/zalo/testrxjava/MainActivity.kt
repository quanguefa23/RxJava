package com.zing.zalo.testrxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//
//        Observable.just("long", "longer", "longest")
//                .doOnNext { c: String? -> Log.d(
//                    "QUANG",
//                    "processing item on thread " + Thread.currentThread().name
//                ) }
//                .observeOn(AndroidSchedulers.mainThread())
//                .map { obj: String -> obj.length }
//                .subscribe { length: Int -> Log.d("QUANG", "item length $length") }

//        Observable
//                .just(1, 2, 3, 4)
//                .subscribeOn(io())
//                .filter {
//                    Log.i("QUANG onFilter", it.toString() + " " + Thread.currentThread().toString())
//                    it > 1
//               .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(object : Observer<Int> {
////                    override fun onSubscribe(d: Disposable?) {
////                    }
////
////                    override fun onNext(t: Int) {
////                        Log.i("QUANG onNext", t.toString() + " " + Thread.currentThread().toString())
////                        try {
////                            Thread.sleep(2000)
////                        } catch (e: Exception) {
////                            e.printStackTrace()
////                        }
////                    }
////
////                    override fun onError(e: Throwable?) {
////                    }
////
////                    override fun onComplete() {
////                    }
////                })        }
//
    }
}

//fun main() {
//
//    Observable.just("long", "longer", "longest")
//            .doOnNext { c: String? -> println("processing item on thread " + Thread.currentThread().name) }
//            .subscribeOn(io())
//            .map { obj: String -> obj.length }
//            .subscribe { length: Int -> println("item length $length") }
//}



fun maini() {
    val a = Observable.just(1, 2, 3)
        .subscribeOn(Schedulers.io())
        .doAfterNext {
            println("emit " + it + " on thread " + Thread.currentThread().name)
        }
        .observeOn(Schedulers.computation())
        .flatMap {
           // Thread.sleep(it * 500.toLong())
            val num = ((10-it) * 200).toLong()
            Thread.sleep(num)
            println("$it    " + num + " on thread " + Thread.currentThread().name)
            Observable.just(it)
           // println("mapping $it" + " on thread " + Thread.currentThread().name)
        }
        .observeOn(Schedulers.io())
        .subscribe {
                    length ->
                //Thread.sleep(1000)
                 println("received item length " + length + " on thread " + Thread.currentThread().name)
        }


    Thread.sleep(10000)
}

/**
 * Returns length of each param wrapped into an Observable.
 */
fun performLongOperation(v: Int): Observable<Int>? {
    //val random = Random()
    try {
        val num = ((10-v) * 200).toLong()
        Thread.sleep(num)
        println("$v    " + num + " on thread " + Thread.currentThread().name)
        return Observable.just(v)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return null
}

fun main() {
    val items = arrayListOf("a", "b", "c", "d", "e", "f")


    val a= Observable.fromIterable(items)
        .concatMap { s ->
            val delay = java.util.Random().nextInt(10)
            Observable.just(s.toString() + "x" + delay)
                .delay(delay.toLong(), TimeUnit.SECONDS)
        }
        .subscribe {
            println(it)
        }

    //scheduler.advanceTimeBy(1, TimeUnit.MINUTES)
    //test()
    Thread.sleep(10000)
}

fun test() {
    val a = Observable.just(1, 2, 3, 4)
        .doAfterNext {
            println("do after next $it in ${Thread.currentThread().name}")
        }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())
        .map {
            println("map $it in ${Thread.currentThread().name}")
            Thread.sleep( 1000)
            it * 10
        }
        .buffer(2000, TimeUnit.MILLISECONDS)
        .subscribe {
            println(it)
        }
}

