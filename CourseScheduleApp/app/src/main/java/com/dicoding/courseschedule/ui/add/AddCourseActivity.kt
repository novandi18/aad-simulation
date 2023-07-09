package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var viewModel: AddCourseViewModel

    private lateinit var edCourseName: TextInputEditText
    private lateinit var spDay: Spinner
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var edLecturer: TextInputEditText
    private lateinit var edNote: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = AddCourseViewModelFactory.createFactory(this)
        initializeUI()
        viewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]

        viewModel.saved.observe(this) {
            it.getContentIfNotHandled()?.let { isSucceed ->
                if (isSucceed) {
                    finish()
                } else {
                    Toast.makeText(this, resources.getString(R.string.input_empty_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                viewModel.insertCourse(
                    courseName = edCourseName.text.toString(),
                    day = spDay.selectedItemPosition,
                    startTime = tvStartTime.text.toString(),
                    endTime = tvEndTime.text.toString(),
                    lecturer = edLecturer.text.toString(),
                    note = edNote.text.toString()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val time = String.format("%02d:%02d", hour, minute)
        if (tag == "ib_start_time") tvStartTime.text = time else tvEndTime.text = time
    }

    private fun showTimePicker(tag: String) {
        val dialogFragment = TimePickerFragment()
        dialogFragment.show(supportFragmentManager, tag)
    }

    private fun initializeUI() {
        edCourseName = findViewById(R.id.ed_course_name)
        spDay = findViewById(R.id.spinner_day)
        tvStartTime = findViewById(R.id.tv_start_time)
        tvEndTime = findViewById(R.id.tv_end_time)
        edLecturer = findViewById(R.id.ed_lecturer)
        edNote = findViewById(R.id.ed_note)

        findViewById<ImageButton>(R.id.ib_start_time).setOnClickListener { showTimePicker("ib_start_time") }
        findViewById<ImageButton>(R.id.ib_end_time).setOnClickListener { showTimePicker("ib_end_time") }
    }
}