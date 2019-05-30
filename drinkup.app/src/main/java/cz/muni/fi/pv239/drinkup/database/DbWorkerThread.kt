package cz.muni.fi.pv239.drinkup.database

import android.os.Handler
import android.os.HandlerThread

// Nepouziva se? Smazat
class DbWorkerThread(threadName: String) : HandlerThread(threadName) {
    private lateinit var workerHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        workerHandler = Handler(looper)
    }

    fun postTask(task: Runnable) {
        workerHandler.post(task)
    }
}