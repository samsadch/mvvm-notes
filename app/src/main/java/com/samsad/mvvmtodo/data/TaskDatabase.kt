package com.samsad.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.samsad.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao


    //@Inject create tells
    //Dagger to create an instance of the class
    //also tells dagger to pass the necessory dependencies
    class CallBack @Inject constructor(
        private val taskDatabase: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //Db operations
            val dao = taskDatabase.get().taskDao()
            /*GlobalScope.launch {
                dao.insertTask(Task("First Task"))
            }*/
            applicationScope.launch {
                dao.insertTask(Task("First Task"))
                dao.insertTask(Task("Second Task"))
                dao.insertTask(Task("Third Task", important = true))
                dao.insertTask(Task("Fourth Task", completed = true))
                dao.insertTask(Task("Fifth Task", completed = true))
            }
        }
    }


}