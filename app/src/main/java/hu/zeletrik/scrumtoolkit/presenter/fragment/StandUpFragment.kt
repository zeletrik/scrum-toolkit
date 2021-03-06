package hu.zeletrik.scrumtoolkit.presenter.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.domain.MeasureType
import hu.zeletrik.scrumtoolkit.presenter.activity.TimerActivity
import hu.zeletrik.scrumtoolkit.util.Constants
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_NUM_OF_ATTENDEES_KEY
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_USE_FIX_NUM_KEY
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_USE_LIST_KEY
import org.apache.commons.lang3.StringUtils

class StandUpFragment : Fragment() {

    private lateinit var chipGroup: ChipGroup
    private lateinit var startTimerFab: FloatingActionButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rootView: View
    private lateinit var title: TextView
    private lateinit var hint: TextView
    private lateinit var attendeeNumber: TextView
    private var firstMember: String = StringUtils.EMPTY
    private lateinit var refreshButton: ImageView
    private val members = ArrayList<String>()
    var isUsersRefreshed = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.timer_fragment, container, false)

        chipGroup = rootView.findViewById(R.id.chipGroup)
        startTimerFab = rootView.findViewById(R.id.startTimerFab)
        title = rootView.findViewById(R.id.optionTitle)
        hint = rootView.findViewById(R.id.hint)
        attendeeNumber = rootView.findViewById(R.id.attendeeNumber)
        refreshButton = rootView.findViewById(R.id.refresh)

        sharedPreferences = activity!!.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)

        init()
        return rootView
    }

    private fun init() {
        attendeeNumber.visibility = View.INVISIBLE
        var measureType: MeasureType = MeasureType.FREE_FORM

        if (sharedPreferences.contains(PREF_USE_LIST_KEY)) {
            if (sharedPreferences.getBoolean(PREF_USE_LIST_KEY, false)) {
                measureType = MeasureType.FIXED_LIST
            }
        }
        if (sharedPreferences.contains(PREF_USE_FIX_NUM_KEY)) {
            if (sharedPreferences.getBoolean(PREF_USE_FIX_NUM_KEY, false)) {
                measureType = MeasureType.FIXED_NUMBER
            }
        }

        when (measureType) {
            MeasureType.FREE_FORM -> freeForm()
            MeasureType.FIXED_LIST -> fixedList()
            MeasureType.FIXED_NUMBER -> fixedNumber()

        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun fixedNumber() {
        refreshButton.visibility = View.INVISIBLE
        title.text = getString(R.string.no_of_members)
        hint.text = getString(R.string.hint_specify_members)
        var numberOfAttendees = 100
        if (sharedPreferences.contains(PREF_NUM_OF_ATTENDEES_KEY)) {
            numberOfAttendees = sharedPreferences.getInt(PREF_NUM_OF_ATTENDEES_KEY, 100)
            attendeeNumber.visibility = View.VISIBLE
            attendeeNumber.text = numberOfAttendees.toString()
        }

        startTimerFab.setOnClickListener {
            val timerIntent = Intent(activity, TimerActivity::class.java)
            timerIntent.putExtra("numberOfAttendees", numberOfAttendees)
            startActivity(timerIntent)
        }
    }

    private fun fixedList() {
        refreshButton.visibility = View.VISIBLE
        title.text = getString(R.string.vac_today)
        hint.text = getString(R.string.hint_fix_who_starts)
        if (!isUsersRefreshed) {
            isUsersRefreshed = true
            members.clear()
            if (sharedPreferences.contains(Constants.PREF_ATTENDEE_LIST_KEY)) {
                val json = sharedPreferences.getString(Constants.PREF_ATTENDEE_LIST_KEY, StringUtils.EMPTY)
                val saved = getList(json!!)
                saved.forEach { member -> members.add(member) }
            }
        }

        chipGroup.removeAllViews()
        members.forEach { member -> addChipToGroup(member) }


        chipGroup.isSingleSelection = true
        chipGroup.setOnCheckedChangeListener { _, i ->
            firstMember = getSelectedText(chipGroup, i)
        }

        startTimerFab.setOnClickListener {
            if (members.isEmpty()) {
                Toast.makeText(context, "No user present", Toast.LENGTH_SHORT).show()

            } else {
                val timerIntent = Intent(activity, TimerActivity::class.java)
                timerIntent.putExtra("members", members)
                timerIntent.putExtra("firstMember", firstMember)
                startActivity(timerIntent)
            }

        }

        refreshButton.setOnClickListener {
            isUsersRefreshed = false
            fixedList()
        }
    }

    private fun freeForm() {
        title.text = getString(R.string.no_constraint)
        hint.text = getString(R.string.hint_specify_number_or_members)
        startTimerFab.setOnClickListener {
            val timerIntent = Intent(activity, TimerActivity::class.java)
            startActivity(timerIntent)
        }
    }

    private fun addChipToGroup(person: String) {
        val chip = Chip(context)
        chip.text = person
        chip.isCloseIconVisible = true
        chip.setChipIconTintResource(R.color.colorPrimaryDark)
        chip.isClickable = true
        chip.isCheckable = true
        chip.height = 12
        chip.textSize = 24F
        chipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip as View)
            members.remove(chip.text.toString())

            if (firstMember == chip.text.toString()) {
                firstMember = StringUtils.EMPTY
            }
        }
    }

    private fun getSelectedText(chipGroup: ChipGroup, id: Int): String {
        val mySelection = chipGroup.findViewById<Chip>(id)
        return mySelection?.text?.toString() ?: StringUtils.EMPTY
    }

    private fun getList(jsonArray: String): ArrayList<String> {
        val typeOfT = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        return Gson().fromJson(jsonArray, typeOfT)
    }
}
