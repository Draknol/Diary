package io.github.draknol.diary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.draknol.diary.DiaryDataBase.Companion.getDataBase

@Suppress("UNCHECKED_CAST")
class DiaryViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(context = context) as T
    }
}

class DiaryViewModel(context: Context): ViewModel() {
    val mDao: DiaryDao
    init {
        val db = getDataBase(context = context)
        mDao = db.DiaryDao()
    }

    fun getAllDesc() = mDao.getAllDesc()
    fun getAllAsc() = mDao.getAllAsc()
    fun insert(entry: Entry) = mDao.insert(entry = entry)
}