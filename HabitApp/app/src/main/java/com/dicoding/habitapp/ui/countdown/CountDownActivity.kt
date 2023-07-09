package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class CountDownActivity : AppCompatActivity() {
    private lateinit var workManager: WorkManager
    private lateinit var oneTimeWorkRequest: OneTimeWorkRequest
    private var timerIsStoppedByClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(habit.minutesFocus)
        viewModel.currentTimeString.observe(this) { time ->
            findViewById<TextView>(R.id.tv_count_down).text = time
        }

        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
        workManager = WorkManager.getInstance(this)
        val data = Data.Builder().apply {
            putInt(HABIT_ID, habit.id)
            putString(HABIT_TITLE, habit.title)
        }.build()
        oneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java).setInputData(data).build()

        viewModel.eventCountDownFinish.observe(this) { isFinished ->
            if (isFinished) {
                if (!timerIsStoppedByClick) workManager.enqueue(oneTimeWorkRequest)
                updateButtonState(false)
            }
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            timerIsStoppedByClick = false
            viewModel.startTimer()
            updateButtonState(true)
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            timerIsStoppedByClick = true
            viewModel.resetTimer()
            updateButtonState(false)
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}