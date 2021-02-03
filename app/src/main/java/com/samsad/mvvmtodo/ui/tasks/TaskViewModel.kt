package com.samsad.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.samsad.mvvmtodo.data.PreferenceManager
import com.samsad.mvvmtodo.data.SortOrder
import com.samsad.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferenceManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")


    val preferencesFlow = preferencesManager.preferencesFlow

    /*val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)*/

    private val taskFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreference ->
        Pair(query, filterPreference)
    }.flatMapLatest { (query, filterPreference) ->
        taskDao.getTasks(query, filterPreference.sortOrder, filterPreference.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    val tasks = taskFlow.asLiveData()

}