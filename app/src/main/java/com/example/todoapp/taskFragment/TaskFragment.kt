package com.example.todoapp.taskFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.*
import com.example.todoapp.HomeFragment.HomeFragment
import com.example.todoapp.database.Task
import java.util.*

const val TASK_DATE_KEY = "taskDate"
const val DATE_FORMAT = "dd/MM/yyyy"


class TaskFragment : Fragment() , DatePickerDialogFragment.DatePickerCallback{
    private lateinit var editTextTitle:EditText
    private lateinit var editTextDescription:EditText
    private lateinit var saveBtn:Button
    private lateinit var updateBtn:Button
    private lateinit var dateBtn:Button
    private lateinit var creationDate:Button
    private lateinit var work:TextView
    private lateinit var personal:TextView
    private lateinit var health:TextView


    private lateinit var task :Task


    var selected_category: Int =0




    private val fragmentViewModel by lazy { ViewModelProvider(this)
        .get(TaskFragmentViewModel::class.java) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_task,container,false)
        editTextTitle = view.findViewById(R.id.editTextTitle_id)
        editTextDescription = view.findViewById(R.id.editTextDescription_id)
        saveBtn = view.findViewById(R.id.addTaskBtn_id)
        updateBtn = view.findViewById(R.id.updateBtn_id)
        dateBtn = view.findViewById(R.id.date_img)
        creationDate = view.findViewById(R.id.creation_date_id)
        work = view.findViewById(R.id.work_id)
        personal = view.findViewById(R.id.personal_id)
        health = view.findViewById(R.id.health_id)
        creationDate.text = android.text.format.DateFormat.format(DATE_FORMAT,task.creation_date)


        return view
    }



    override fun onStart() {
        super.onStart()

        editTextTitle.setOnClickListener { task.title }
        editTextDescription.setOnClickListener { task.Description }
        saveBtn.setOnClickListener { saveTask() }
        updateBtn.setOnClickListener { updateTask() }
        work.setOnClickListener { selected_category = 0; work.setBackgroundResource(R.color.teal_200) }
        personal.setOnClickListener { selected_category = 1 ; personal.setBackgroundResource(R.color.teal_200) }
        health.setOnClickListener { selected_category = 2 ; health.setBackgroundResource(R.color.teal_200) }

        dateBtn.setOnClickListener {
            val args = Bundle()
            args.putSerializable(TASK_DATE_KEY,task.due_date)

            val datePicker = DatePickerDialogFragment()

            datePicker.arguments = args
            datePicker.setTargetFragment(this,0)
            datePicker.show(this.parentFragmentManager,"date picker")
        }

    }




    private fun saveTask() {
        val title = editTextTitle?.text.toString().trim { it <= ' ' }
        val description = editTextDescription?.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this.activity, "Please insert a title", Toast.LENGTH_LONG).show()
            return
        }else
        {
           // val task = Task(title,description, selected_category)
            task.title = title
            task.Description = description
            task.category = selected_category
            fragmentViewModel.addTask(task)

            val fragment = HomeFragment()
            activity?.let {
                    it.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,fragment)
                        .commit()
                }
        }

    }

    private fun updateTask() {
        val title = editTextTitle?.text.toString().trim { it <= ' ' }
        val description = editTextDescription?.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this.activity, "Please insert a title", Toast.LENGTH_LONG).show()
            return
        }else
        {
            // val task = Task(title,description, selected_category)
            task.title = title
            task.Description = description
            task.category = selected_category
            fragmentViewModel.saveUpdate(task)

            val fragment = HomeFragment()
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.taskLiveData.observe(
            viewLifecycleOwner, androidx.lifecycle.Observer {
                it?.let {
                    task  = it
                    editTextTitle.setText(it.title)
                    editTextDescription.setText(it.Description)
                    dateBtn?.text = it.due_date.toString()
                    //dateBtn?.text = android.text.format.DateFormat.format(DATE_FORMAT,it.due_date).toString()

                    val buttonVisibility = arguments?.getInt(KEY_ID_UPDATE)
                    buttonVisibility?.let {
                        saveBtn.visibility=View.GONE
                        updateBtn.visibility = View.VISIBLE

                    }

                }

            }
        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = Task()

        val taskId = arguments?.getInt(KEY_ID)
        taskId?.let { fragmentViewModel.loadTask(it) }

    }

    override fun onDateSelected(date: Date) {
        task.due_date = date
        dateBtn.text = android.text.format.DateFormat.format(DATE_FORMAT,task.due_date)

    }


    override fun onStop() {
        super.onStop()
        Log.d("eman","eman")
//        val title = editTextTitle?.text.toString().trim { it <= ' ' }
//        val description = editTextDescription?.text.toString()
//        task.title = title
//        task.Description = description
//
       // fragmentViewModel.saveUpdate(task)


    }

}