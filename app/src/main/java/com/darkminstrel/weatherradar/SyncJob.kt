package com.darkminstrel.weatherradar

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.darkminstrel.weatherradar.data.UpdatePeriod
import com.darkminstrel.weatherradar.usecases.UsecaseSync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

class SyncJob : JobService() {

    companion object {

        private const val JOB_ID = 1

        fun schedule(context: Context, period:UpdatePeriod, wifiOnly:Boolean, forceReschedule:Boolean){
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if(!forceReschedule && jobScheduler.allPendingJobs.isNotEmpty()) {
                DBG("Job is already scheduled")
                return
            }

            if(period==UpdatePeriod.NONE) {
                jobScheduler.cancel(JOB_ID)
                DBG("Job was canceled")
            }else{
                val networkType = if(wifiOnly) JobInfo.NETWORK_TYPE_UNMETERED else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        JobInfo.NETWORK_TYPE_NOT_ROAMING
                    } else {
                        JobInfo.NETWORK_TYPE_ANY
                    }
                }
                val builder = JobInfo.Builder(JOB_ID, ComponentName(context, SyncJob::class.java))
                    .setRequiredNetworkType(networkType)
                    .setPeriodic(period.millis)
                    .setPersisted(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    builder.setEstimatedNetworkBytes(115000, 1000)
                }
                val result = jobScheduler.schedule(builder.build())
                when (result) {
                    JobScheduler.RESULT_FAILURE -> DBG("Job was NOT scheduled")
                    JobScheduler.RESULT_SUCCESS -> DBG("Job was successfully scheduled to '${period.getString(context)}'"+(if(wifiOnly) " (wifi only)" else ""))
                }
            }
        }
    }

    private var disposable:Disposable? = null
    private val usecaseSync:UsecaseSync by inject()

    override fun onStartJob(params: JobParameters): Boolean {
        DBG("Job started")
        disposable?.dispose()
        disposable = usecaseSync.getSyncSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {pack ->
                    onJobFinished(params, null)
                },
                {error -> onJobFinished(params, error)})
        return true //job hasn't finished yet
    }

    private fun onJobFinished(params: JobParameters, error:Throwable?){
        DBG(if(error==null) "Job finished" else "Job failed with $error")
        disposable = null
        jobFinished(params, false)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        disposable?.dispose()
        disposable = null
        return false //don't reschedule
    }
}