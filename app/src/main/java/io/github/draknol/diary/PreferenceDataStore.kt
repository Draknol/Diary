package io.github.draknol.diary

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Stores an hour and minute time
data class Time(val hour: Int, val minute: Int)

class PreferenceDataStore(context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val dataStore = context.dataStore

    // Keys for the time
    companion object {
        val reminderHour = intPreferencesKey(name = "reminder_hour")
        val reminderMinute = intPreferencesKey(name = "reminder_minute")
    }


    /**
     * Sets the time for a reminder.
     * @param time The time to set.
     */
    fun setDetails(time: Time) {
        CoroutineScope(context = Dispatchers.IO).launch {
            dataStore.edit { settings ->
                settings[reminderHour] = time.hour
                settings[reminderMinute] = time.minute
            }
        }
    }


    /**
     * Gets the time for a reminder.
     * @return The time for a reminder.
     */
    fun getDetails() = dataStore.data.map {
        Time(
            hour = it[reminderHour] ?: 0,
            minute = it[reminderMinute] ?: 0
        )
    }
}
