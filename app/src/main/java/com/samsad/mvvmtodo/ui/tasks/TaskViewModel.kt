package com.samsad.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.samsad.mvvmtodo.data.TaskDao

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

}