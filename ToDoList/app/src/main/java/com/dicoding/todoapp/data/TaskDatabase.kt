package com.dicoding.todoapp.data

import android.content.Context
import androidx.core.content.edit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.todoapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

//TODO 3 : Define room database class and prepopulate database using JSON
@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = true
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        private const val IS_FIRST_RUN = "is_first_run"
        private const val APP_PREF = "app_pref"

        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task.db"
                ).build()
                INSTANCE = instance

                val sharedPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
                val isFirstRun = sharedPreferences.getBoolean(IS_FIRST_RUN, true)

                if (isFirstRun) {
                    runBlocking {
                        fillWithStartingData(context, instance.taskDao())
                    }

                    sharedPreferences.edit {
                        putBoolean(IS_FIRST_RUN, false)
                    }
                }
                instance
            }
        }

        private suspend fun fillWithStartingData(context: Context, dao: TaskDao) = withContext(Dispatchers.IO) {
            val task = loadJsonArray(context)
            try {
                if (task != null) {
                    for (i in 0 until task.length()) {
                        val item = task.getJSONObject(i)
                        dao.insertAll(
                            Task(
                                item.getInt("id"),
                                item.getString("title"),
                                item.getString("description"),
                                item.getLong("dueDate"),
                                item.getBoolean("completed")
                            )
                        )
                    }
                }
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }

        private fun loadJsonArray(context: Context): JSONArray? {
            val builder = StringBuilder()
            val `in` = context.resources.openRawResource(R.raw.task)
            val reader = BufferedReader(InputStreamReader(`in`))
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                val json = JSONObject(builder.toString())
                return json.getJSONArray("tasks")
            } catch (exception: IOException) {
                exception.printStackTrace()
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
            return null
        }

    }
}
