package com.zing.zalo.testrxjava;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;


public class RX {
    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor
        Observable.just("long", "longer", "longest")
                .observeOn(Schedulers.newThread())
                .flatMap(v ->
                        performLongOperation(v)
                                .doOnNext(s -> System.out.println("processing item on thread " + Thread.currentThread().getName()))
                                .subscribeOn(Schedulers.newThread()))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Thread.sleep(10000);
    }
    /**
     * Returns length of each param wrapped into an Observable.
     */
    protected static Observable<Integer> performLongOperation(String v) {
        Random random = new Random();
        try {
            Thread.sleep(1000);
            System.out.println(" on thread " + Thread.currentThread().toString());
            return Observable.just(v.length());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}