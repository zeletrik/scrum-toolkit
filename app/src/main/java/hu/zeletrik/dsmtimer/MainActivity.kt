package hu.zeletrik.dsmtimer

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.EditText
import android.widget.TextView
import android.content.SharedPreferences
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Intent
import hu.zeletrik.dsmtimer.Constants.Companion.PREFERENCE_KEY
import hu.zeletrik.dsmtimer.Constants.Companion.PREF_ATTENDEE_LIST_KEY
import hu.zeletrik.dsmtimer.Constants.Companion.PREF_NUM_OF_ATTENDEES_KEY
import hu.zeletrik.dsmtimer.Constants.Companion.PREF_TIME_KEY
import hu.zeletrik.dsmtimer.Constants.Companion.PREF_USE_FIX_NUM_KEY
import hu.zeletrik.dsmtimer.Constants.Companion.PREF_USE_LIST_KEY


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val members = ArrayList<String>()
    private val memberAdapter = MemberAdapter(members)
    private lateinit var sharedPreferences: SharedPreferences
    private var useList: Boolean = false
    private var useFixNumber: Boolean = false
    private var time: Int = 60
    private var numberOfAttendees: Int = 100
    private lateinit var numberPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker = findViewById(R.id.numberPicker)
        val simpleSeekBar = findViewById<SeekBar>(R.id.baseTimeSeekBar)
        val baseTimeText = findViewById<TextView>(R.id.baseTimeValue)
        val numberPicker = findViewById<NumberPicker>(R.id.numberPicker)
        val maxNumberCheckBox = findViewById<CheckBox>(R.id.fixNumAttendee)
        val attendeeListCheckBox = findViewById<CheckBox>(R.id.attendeeList)
        val addAttendeeButton = findViewById<Button>(R.id.addButton)
        val newAttendeeEditText = findViewById<EditText>(R.id.attendeeName)
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val mFab = findViewById<FloatingActionButton>(R.id.fab)

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = memberAdapter

        numberPicker.minValue = 2
        numberPicker.maxValue = 25
        numberPicker.setOnValueChangedListener(onNPValueChangeListener)

        maxNumberCheckBox.setOnCheckedChangeListener { _, isChecked ->
            numberPicker.isVisible = isChecked
            useFixNumber = isChecked
            if (isChecked) {
                newAttendeeEditText.isVisible = !isChecked
                addAttendeeButton.isVisible = !isChecked
                attendeeListCheckBox.isChecked = !isChecked
                mRecyclerView.isVisible = !isChecked
            }

            saveValues()
        }

        attendeeListCheckBox.setOnCheckedChangeListener { _, isChecked ->
            useList = isChecked
            newAttendeeEditText.isVisible = isChecked
            addAttendeeButton.isVisible = isChecked
            mRecyclerView.isVisible = isChecked
            if (isChecked) {
                numberPicker.isVisible = !isChecked
                maxNumberCheckBox.isChecked = !isChecked
            }

            saveValues()
        }

        attendeeName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    members.add(newAttendeeEditText.text.toString())
                    memberAdapter.notifyDataSetChanged()
                    saveValues()
                    return true
                }
                return false
            }
        })

        simpleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                baseTimeText.text = String.format(getString(R.string.base_time_value), progressChangedValue)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                time = progressChangedValue
                saveValues()
            }
        })

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as MemberAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                saveValues()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        addAttendeeButton.setOnClickListener(this)

        //region SharedPrefs
        sharedPreferences = getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE)
        getValues()
        attendeeListCheckBox.isChecked = useList
        maxNumberCheckBox.isChecked = useFixNumber
        simpleSeekBar.progress = time
        //endregion

        mFab.setOnClickListener {
            saveValues()
            if (useList) {
                 val i = Intent(this, OptionsActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this, TimerActivity::class.java)
                i.putExtra("numberOfAttendees", numberOfAttendees)
                startActivity(i)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addButton -> {
                val newAttendeeName = findViewById<EditText>(R.id.attendeeName)
                members.add(newAttendeeName.text.toString())
                memberAdapter.notifyDataSetChanged()
                saveValues()
            }
        }

    }

    private val onNPValueChangeListener = NumberPicker.OnValueChangeListener { numberPicker, _, _ ->
        numberOfAttendees = numberPicker.value
    }

    fun saveValues() {
        val json = Gson().toJson(members)

        val editor = sharedPreferences!!.edit()
        editor.putBoolean(PREF_USE_LIST_KEY, useList)
        editor.putBoolean(PREF_USE_FIX_NUM_KEY, useFixNumber)
        editor.putInt(PREF_NUM_OF_ATTENDEES_KEY, numberOfAttendees)
        editor.putInt(PREF_TIME_KEY, time)
        editor.putString(PREF_ATTENDEE_LIST_KEY, json)
        editor.apply()
    }

    private fun getValues() {
        if (sharedPreferences.contains(PREF_USE_LIST_KEY)) {
            useList = sharedPreferences.getBoolean(PREF_USE_LIST_KEY, false)
        }
        if (sharedPreferences.contains(PREF_USE_FIX_NUM_KEY)) {
            useFixNumber = sharedPreferences.getBoolean(PREF_USE_FIX_NUM_KEY, false)
        }
        if (sharedPreferences.contains(PREF_TIME_KEY)) {
            time = sharedPreferences.getInt(PREF_TIME_KEY, 60)
        }
        if (sharedPreferences.contains(PREF_NUM_OF_ATTENDEES_KEY)) {
            numberOfAttendees = sharedPreferences.getInt(PREF_NUM_OF_ATTENDEES_KEY, 100)
            if (numberOfAttendees <= numberPicker.maxValue) {
                numberPicker.value = numberOfAttendees
            }
        }
        if (sharedPreferences.contains(PREF_ATTENDEE_LIST_KEY)) {
            val json = sharedPreferences.getString(PREF_ATTENDEE_LIST_KEY, "")
            val saved = getList(json!!)

            saved.forEach { member ->  members.add(member) }

            memberAdapter.notifyDataSetChanged()
        }
    }

    private fun getList(jsonArray: String): ArrayList<String> {
        val typeOfT = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        return Gson().fromJson(jsonArray, typeOfT)
    }
}
