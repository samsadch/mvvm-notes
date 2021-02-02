package com.samsad.mvvmtodo.ui.tasks

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samsad.mvvmtodo.data.Task
import com.samsad.mvvmtodo.databinding.ItemTaskBinding

class TasksAdapter : ListAdapter<Task, TaskView> {

    class TasksViewHolder(private val binding:ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(task: Task){
            binding.textViewName = task.name
        }


    }
}