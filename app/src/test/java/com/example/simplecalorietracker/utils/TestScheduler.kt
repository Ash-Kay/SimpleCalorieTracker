package com.example.simplecalorietracker.utils

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class TestSchedulerImpl : IScheduler {
    override val io: Scheduler = Schedulers.trampoline()
    override val mainThread: Scheduler = Schedulers.trampoline()
}