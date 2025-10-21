package io.github.draknol.diary

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.draknol.diary.DiaryDataBase.Companion.getDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
class DiaryViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(context = context) as T
    }
}
class DiaryViewModel(context: Context): ViewModel() {
    val mDao: DiaryDao
    private val preferenceDataStore = PreferenceDataStore(context)

    init {
        val db = getDataBase(context = context)
        mDao = db.DiaryDao()
    }

    val selectedEntry = mutableStateOf(value = Entry(id = -1, title = "", content = "", date = ""))

    fun getAllDesc() = mDao.getAllDesc()

    fun insert(entry: Entry) = viewModelScope.launch {
        withContext(context = Dispatchers.IO) {
            mDao.insert(entry = entry)
        }
    }

    fun update(entry: Entry) = viewModelScope.launch {
        withContext(context = Dispatchers.IO) {
            mDao.update(entry = entry)
        }
    }

    val reminderTime = preferenceDataStore.getDetails()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Time(hour = 0, minute = 0)
        )

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun setReminderTime(context: Context, time: Time) {
        preferenceDataStore.setDetails(time)

        // Get time for reminder
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
        calendar.set(Calendar.MINUTE, time.minute)
        calendar.set(Calendar.SECOND, 0)

        // If the time is in the past, set it for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Work out how long until reminder should be sent
        val delay = calendar.timeInMillis - System.currentTimeMillis()

        // Repeat every 24h
        val dailyReminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("reminder_${time.hour}_${time.minute}")
            .build()
        WorkManager.getInstance(context).enqueue(dailyReminderRequest)
    }
}