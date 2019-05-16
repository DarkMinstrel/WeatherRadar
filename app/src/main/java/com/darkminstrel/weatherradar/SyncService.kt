package com.darkminstrel.weatherradar

import android.app.job.JobParameters
import android.app.job.JobService
import com.darkminstrel.weatherradar.rx.sync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import android.app.job.JobInfo
import android.content.ComponentName
import android.app.job.JobScheduler
import android.content.Context
import android.os.Build
import java.util.concurrent.TimeUnit

class SyncService : JobService() {

    companion object {
        private const val JOB_ID = 1
        fun schedule(context: Context){
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if(jobScheduler.allPendingJobs.isNotEmpty()) {
                DBG("Job is already scheduled")
                return
            }
            val builder = JobInfo.Builder(JOB_ID, ComponentName(context, SyncService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(TimeUnit.SECONDS.toMillis(10))
                .setPersisted(true)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                builder.setEstimatedNetworkBytes(115000, 1000)
            }
            val result = jobScheduler.schedule(builder.build())
            when(result){
                JobScheduler.RESULT_FAILURE -> DBG("Job was NOT scheduled")
                JobScheduler.RESULT_SUCCESS -> DBG("Job was successfully scheduled")
            }
        }
    }

    private var disposable:Disposable? = null

    override fun onStartJob(params: JobParameters): Boolean {
        DBG("Job started")
        disposable?.dispose()
        disposable = sync(applicationContext)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack -> onJobFinished(params, null)},
                {error -> onJobFinished(params, error)})
        return true //job hasn't finished yet
    }

    private fun onJobFinished(params: JobParameters, error:Throwable?){
        if(error==null) DBG("Job finished")
        else DBG("Job failed with $error")
        jobFinished(params, false)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        disposable?.dispose()
        return false //don't reschedule
    }

}