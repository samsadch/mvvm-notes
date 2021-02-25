package com.samsad.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.samsad.mvvmtodo.data.PreferenceManager
import com.samsad.mvvmtodo.data.SortOrder
import com.samsad.mvvmtodo.data.Task
import com.samsad.mvvmtodo.data.TaskDao
import com.samsad.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.samsad.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferenceManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    //val searchQuery = MutableStateFlow("")

    val searchQuery = state.getLiveData("SearchQuery", "")

    /*private val taskFlowOld = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }*/

    val preferencesFlow = preferencesManager.preferencesFlow

    private val taskEventChannel = Channel<TasksEvent>()
    val tasksEvent = taskEventChannel.receiveAsFlow()

    private val taskFlow = combine(
        searchQuery.asFlow(),
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

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = checked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        //Channel - we can send data between two coroutines
        taskEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insertTask(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> {
                showTaskConfirmationMessage("Task Added")
            }
            EDIT_TASK_RESULT_OK -> {
                showTaskConfirmationMessage("Task Updated")
            }
        }
    }

    private fun showTaskConfirmationMessage(message: String) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.ShowTaskConfirmationMessage(message))
    }

    val tasks = taskFlow.asLiveData()


    //Enum with closed combination of different values
    sealed class TasksEvent {
        //By creating as object we will create only one instance of this class
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskConfirmationMessage(val message: String) : TasksEvent()
    }
}