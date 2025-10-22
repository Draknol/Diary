package io.github.draknol.diary

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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

    fun getEntry(id: Long) = mDao.getEntry(id = id)

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

    fun delete(entry: Entry) = viewModelScope.launch {
        withContext(context = Dispatchers.IO) {
            mDao.delete(entry = entry)
        }
    }

    val reminderTime = preferenceDataStore.getDetails()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Time(hour = 0, minute = 0)
        )

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
            .addTag("reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName = "reminder",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
            request = dailyReminderRequest
        )
    }
}