package hu.zeletrik.scrumtoolkit.presenter.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.adapter.MemberAdapter
import hu.zeletrik.scrumtoolkit.domain.MeasureType
import hu.zeletrik.scrumtoolkit.util.Constants
import hu.zeletrik.scrumtoolkit.util.SwipeToDeleteCallback
import org.apache.commons.lang3.StringUtils
import java.util.*

class AttendeeSettingsActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences


    private val members = ArrayList<String>()
    private val memberAdapter = MemberAdapter(members)
    private var numberOfAttendees: Int = 100
    private lateinit var numberPicker: NumberPicker
    private lateinit var addAttendeeButton: Button
    private lateinit var newAttendeeEditText: EditText
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var listNote: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendee_settings)

        numberPicker = findViewById(R.id.numberPicker)
        addAttendeeButton = findViewById(R.id.addButton)
        newAttendeeEditText = findViewById(R.id.attendeeName)
        mRecyclerView = findViewById(R.id.membersRecyclerView)
        listNote = findViewById(R.id.listNote)


        when (MeasureType.valueOf(intent.getStringExtra("mode")!!)) {
            MeasureType.FIXED_LIST -> initListView()
            MeasureType.FIXED_NUMBER -> initNumberView()
            MeasureType.FREE_FORM -> errorView()
        }


        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = memberAdapter

        numberPicker.minValue = 2
        numberPicker.maxValue = 25
        numberPicker.setOnValueChangedListener(onNPValueChangeListener)


        newAttendeeEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    addMember()
                    return true
                }
                return false
            }
        })

        addAttendeeButton.setOnClickListener {
            addMember()
        }

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = mRecyclerView.adapter as MemberAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                saveValues()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        getValues()
    }

    private fun addMember() {
        val name = newAttendeeEditText.text.toString()
        if (StringUtils.isNotEmpty(name)) {
            members.add(name)
            memberAdapter.notifyDataSetChanged()
            saveValues()
            newAttendeeEditText.clearFocus()
            newAttendeeEditText.text.clear()
            newAttendeeEditText.hideKeyboard()
            mRecyclerView.scrollToPosition(members.size - 1)
        } else {
            Toast.makeText(applicationContext, "Name can't be blank", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initListView() {
        numberPicker.visibility = View.INVISIBLE
        addAttendeeButton.visibility = View.VISIBLE
        newAttendeeEditText.visibility = View.VISIBLE
        mRecyclerView.visibility = View.VISIBLE
        listNote.text = "Note: To delete member, swipe left the entry"
    }

    private fun initNumberView() {
        numberPicker.visibility = View.VISIBLE
        addAttendeeButton.visibility = View.INVISIBLE
        newAttendeeEditText.visibility = View.INVISIBLE
        mRecyclerView.visibility = View.INVISIBLE
    }

    private fun errorView() {
        numberPicker.visibility = View.INVISIBLE
        addAttendeeButton.visibility = View.INVISIBLE
        newAttendeeEditText.visibility = View.INVISIBLE
        mRecyclerView.visibility = View.INVISIBLE
    }

    private val onNPValueChangeListener = NumberPicker.OnValueChangeListener { numberPicker, _, _ ->
        numberOfAttendees = numberPicker.value
        sharedPreferences.edit()
            .putInt(Constants.PREF_NUM_OF_ATTENDEES_KEY, numberOfAttendees)
            .apply()
    }

   private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun saveValues() {
        val json = Gson().toJson(members)
        val editor = sharedPreferences.edit()
        editor.putInt(Constants.PREF_NUM_OF_ATTENDEES_KEY, numberPicker.value)
        editor.putString(Constants.PREF_ATTENDEE_LIST_KEY, json)
        editor.apply()
    }

    private fun getValues() {
        if (sharedPreferences.contains(Constants.PREF_NUM_OF_ATTENDEES_KEY)) {
            numberOfAttendees = sharedPreferences.getInt(Constants.PREF_NUM_OF_ATTENDEES_KEY, 100)
            if (numberOfAttendees <= numberPicker.maxValue) {
                numberPicker.value = numberOfAttendees
            }
        }
        if (sharedPreferences.contains(Constants.PREF_ATTENDEE_LIST_KEY)) {
            val json = sharedPreferences.getString(Constants.PREF_ATTENDEE_LIST_KEY, StringUtils.EMPTY)
            val saved = getList(json!!)

            members.clear()
            saved.forEach { member -> members.add(member) }

            memberAdapter.notifyDataSetChanged()
        }
    }

    private fun getList(jsonArray: String): ArrayList<String> {
        val typeOfT = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        return Gson().fromJson(jsonArray, typeOfT)
    }

}