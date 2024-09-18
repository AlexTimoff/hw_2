package com.example.secondhomework.presentation

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.example.secondhomework.entity.CountdownTimer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


class TimerViewModel() : ViewModel() {

    var timer: CountdownTimer

    init {
        timer = CountdownTimer()
    }

    fun start(): Observable<Int> = Observable.create<Int> {
        timer.activate = true

        while (timer.activate) {
            decreasingValue()
            it.onNext(timer.value)
            Thread.sleep(1000)
        }
    }.subscribeOn(Schedulers.io())


    private fun decreasingValue() {
        if (timer.maxValue >= 0) {
            timer.value = timer.maxValue--
        } else {
            timer.activate = false
        }
    }

    fun pause(){
        timer.activate = false
    }

    fun stop(): Observable<Int> = Observable.create<Int> {
        timer.activate = false
        timer.value = 0
        it.onNext(timer.value)
    }.subscribeOn(Schedulers.io())


}