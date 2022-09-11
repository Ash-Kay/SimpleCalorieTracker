package com.example.simplecalorietracker.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

interface IScheduler {
    val io: Scheduler
    val mainThread: Scheduler
}

class SchedulerImpl : IScheduler {
    override val io: Scheduler = Schedulers.io()
    override val mainThread: Scheduler = AndroidSchedulers.mainThread()
}