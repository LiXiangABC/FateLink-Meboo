package com.crush.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService

@SuppressLint("SpecifyJobSchedulerIdRange")
class YourJobService(private val onStartJob:()->Unit, private val onStopJob:()->Unit): JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        onStartJob.invoke()
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        onStopJob.invoke()
        return false
    }
}