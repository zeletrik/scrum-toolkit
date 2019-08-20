package hu.zeletrik.scrumtoolkit.presenter.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.zeletrik.scrumtoolkit.adapter.MemberAdapter
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.util.SwipeToDeleteCallback
import hu.zeletrik.scrumtoolkit.domain.ThemeMode
import hu.zeletrik.scrumtoolkit.util.Constants
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_CURRENT_THEME_KEY
import org.apache.commons.lang3.StringUtils
import java.util.*

class OptionsFragment : Fragment() {

    private lateinit var rootView: View

    private val members = ArrayList<String>()
    private val memberAdapter = MemberAdapter(members)
    private lateinit var sharedPreferences: SharedPreferences
    private var useList: Boolean = false
    private var useFixNumber: Boolean = false
    private var time: Int = 60
    private var numberOfAttendees: Int = 100
    private lateinit var numberPicker: NumberPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.options_fragment, container, false)
        numberPicker = rootView.findViewById(R.id.numberPicker)
        val simpleSeekBar = rootView.findViewById<SeekBar>(R.id.baseTimeSeekBar)
        val baseTimeText = rootView.findViewById<TextView>(R.id.baseTimeValue)
        val numberPicker = rootView.findViewById<NumberPicker>(R.id.numberPicker)
        val maxNumberCheckBox = rootView.findViewById<CheckBox>(R.id.fixNumAttendee)
        val attendeeListCheckBox = rootView.findViewById<CheckBox>(R.id.attendeeList)
        val addAttendeeButton = rootView.findViewById<Button>(R.id.addButton)
        val newAttendeeEditText = rootView.findViewById<EditText>(R.id.attendeeName)
        val mRecyclerView = rootView.findViewById<RecyclerView>(R.id.membersRecyclerView)

        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = memberAdapter

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

        newAttendeeEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    members.add(newAttendeeEditText.text.toString())
                    memberAdapter.notifyDataSetChanged()
                    saveValues()
                    return true
                }
                return false
            }
        })

        addAttendeeButton.setOnClickListener {
            members.add(newAttendeeEditText.text.toString())
            memberAdapter.notifyDataSetChanged()
            saveValues()
        }

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

        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = mRecyclerView.adapter as MemberAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                saveValues()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)


        //region SharedPrefs
        sharedPreferences = activity!!.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        getValues()
        attendeeListCheckBox.isChecked = useList
        maxNumberCheckBox.isChecked = useFixNumber
        simpleSeekBar.progress = time
        //endregion

        val radioGroup = rootView.findViewById<RadioGroup>(R.id.themeRadio)
        radioGroup.clearCheck()
        radioGroup
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioLightTheme -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
                        if (current != ThemeMode.LIGHT.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_THEME_KEY, ThemeMode.LIGHT.name).apply()
                            activity!!.recreate()
                        }
                    }
                    R.id.radioDarkTheme -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
                        if (current != ThemeMode.DARK.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_THEME_KEY, ThemeMode.DARK.name).apply()
                            activity!!.recreate()
                        }
                    }
                    R.id.radioOledTheme -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
                        if (current != ThemeMode.OLED.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_THEME_KEY, ThemeMode.OLED.name).apply()
                            activity!!.recreate()
                        }
                    }
                }
            }

        when (sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)) {
            ThemeMode.DARK.name -> rootView.findViewById<RadioButton>(R.id.radioDarkTheme).isChecked = true
            ThemeMode.OLED.name -> rootView.findViewById<RadioButton>(R.id.radioOledTheme).isChecked = true
            else -> rootView.findViewById<RadioButton>(R.id.radioLightTheme).isChecked = true
        }

        return rootView
    }

    private val onNPValueChangeListener = NumberPicker.OnValueChangeListener { numberPicker, _, _ ->
        numberOfAttendees = numberPicker.value
           sharedPreferences.edit()
               .putInt(Constants.PREF_NUM_OF_ATTENDEES_KEY, numberOfAttendees)
               .apply()

    }

    fun saveValues() {
        val json = Gson().toJson(members)

        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.PREF_USE_LIST_KEY, useList)
        editor.putBoolean(Constants.PREF_USE_FIX_NUM_KEY, useFixNumber)
        editor.putInt(Constants.PREF_NUM_OF_ATTENDEES_KEY, numberPicker.value)
        editor.putInt(Constants.PREF_TIME_KEY, time)
        editor.putString(Constants.PREF_ATTENDEE_LIST_KEY, json)
        editor.apply()
    }

    private fun getValues() {
        if (sharedPreferences.contains(Constants.PREF_USE_LIST_KEY)) {
            useList = sharedPreferences.getBoolean(Constants.PREF_USE_LIST_KEY, false)
        }
        if (sharedPreferences.contains(Constants.PREF_USE_FIX_NUM_KEY)) {
            useFixNumber = sharedPreferences.getBoolean(Constants.PREF_USE_FIX_NUM_KEY, false)
        }
        if (sharedPreferences.contains(Constants.PREF_TIME_KEY)) {
            time = sharedPreferences.getInt(Constants.PREF_TIME_KEY, 60)
        }
        if (sharedPreferences.contains(Constants.PREF_NUM_OF_ATTENDEES_KEY)) {
            numberOfAttendees = sharedPreferences.getInt(Constants.PREF_NUM_OF_ATTENDEES_KEY, 100)
            if (numberOfAttendees <= numberPicker.maxValue) {
                numberPicker.value = numberOfAttendees
            }
        }
        if (sharedPreferences.contains(Constants.PREF_ATTENDEE_LIST_KEY)) {
            val json = sharedPreferences.getString(Constants.PREF_ATTENDEE_LIST_KEY, "")
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