package io.github.draknol.diary

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.draknol.diary.DiaryDataBase.Companion.getDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun setReminderTime(time: Time) {
        preferenceDataStore.setDetails(time)
    }
}