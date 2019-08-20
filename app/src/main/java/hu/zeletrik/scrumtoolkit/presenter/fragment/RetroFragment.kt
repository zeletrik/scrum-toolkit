package hu.zeletrik.scrumtoolkit.presenter.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.util.SwipeToDeleteCallback
import hu.zeletrik.scrumtoolkit.adapter.RetroAdapter
import hu.zeletrik.scrumtoolkit.domain.RetroItem
import hu.zeletrik.scrumtoolkit.domain.RetroItemType
import hu.zeletrik.scrumtoolkit.util.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RetroFragment : Fragment() {

    private lateinit var rootView: View

    private val wentWellList: MutableList<RetroItem> = ArrayList()
    private val cbiList: MutableList<RetroItem> = ArrayList()
    private val timeLineList: MutableList<RetroItem> = ArrayList()
    private val wwAdapter = RetroAdapter(wentWellList)
    private val cbiAdapter = RetroAdapter(cbiList)
    private val timelineAdapter = RetroAdapter(timeLineList)

    private lateinit var sharedPreferences: SharedPreferences

    private var type = RetroItemType.WENT_WELL

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.retro_fragment, container, false)
        sharedPreferences = activity!!.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)

        val wwRecyclerView = rootView.findViewById<RecyclerView>(R.id.retroWWItemsRecyclerView)
        wwRecyclerView.layoutManager = LinearLayoutManager(context)
        wwRecyclerView.adapter = wwAdapter

        val cbiRecyclerView = rootView.findViewById<RecyclerView>(R.id.retroCBIItemsRecyclerView)
        cbiRecyclerView.layoutManager = LinearLayoutManager(context)
        cbiRecyclerView.adapter = cbiAdapter


        val timiLineRecyclerView = rootView.findViewById<RecyclerView>(R.id.retroTILItemsRecyclerView)
        timiLineRecyclerView.layoutManager = LinearLayoutManager(context)
        timiLineRecyclerView.adapter = timelineAdapter


        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radioRetro)
        radioGroup.clearCheck()
        radioGroup
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioWentWell -> type = RetroItemType.WENT_WELL
                    R.id.radioCBI -> type = RetroItemType.COULD_BE_IMPROVE
                    R.id.radioTimeline -> type = RetroItemType.TIMELINE
                }
            }
        rootView.findViewById<RadioButton>(R.id.radioWentWell).isChecked = true

        val wwSwipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                wwAdapter.removeAt(viewHolder.adapterPosition)
                saveList(RetroItemType.WENT_WELL)
            }
        }

        val wwItemTouchHelper = ItemTouchHelper(wwSwipeHandler)
        wwItemTouchHelper.attachToRecyclerView(wwRecyclerView)

        val cbiSwipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                cbiAdapter.removeAt(viewHolder.adapterPosition)
                saveList(RetroItemType.COULD_BE_IMPROVE)
            }
        }

        val cbiItemTouchHelper = ItemTouchHelper(cbiSwipeHandler)
        cbiItemTouchHelper.attachToRecyclerView(cbiRecyclerView)

        val timeLineSwipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                timelineAdapter.removeAt(viewHolder.adapterPosition)
                saveList(RetroItemType.TIMELINE)
            }
        }

        val timeLineItemTouchHelper = ItemTouchHelper(timeLineSwipeHandler)
        timeLineItemTouchHelper.attachToRecyclerView(timiLineRecyclerView)

        rootView.findViewById<Button>(R.id.addRetroItem).setOnClickListener {
            val retroText = rootView.findViewById<EditText>(R.id.addRetroText).text.toString()
            val date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val time = formatter.format(date)
            when (type) {
                RetroItemType.WENT_WELL -> {
                    wentWellList.add(RetroItem(time, retroText))
                    wwAdapter.notifyDataSetChanged()
                }
                RetroItemType.COULD_BE_IMPROVE -> {
                    cbiList.add(RetroItem(time, retroText))
                    cbiAdapter.notifyDataSetChanged()
                }
                RetroItemType.TIMELINE -> {
                    timeLineList.add(RetroItem(time, retroText))
                    timelineAdapter.notifyDataSetChanged()
                }
            }
            saveList(type)
        }

        rootView.findViewById<FloatingActionButton>(R.id.shareRetroItems).setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, createMessageToShare())
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
        }

        loadLists()
        return rootView
    }

    private fun createMessageToShare(): String {
        return "Went Well:\n" +
                "${wentWellList.joinToString(",\n")}\n" +
                "Could be improved:\n" +
                "${cbiList.joinToString(",\n")}\n" +
                "Timeline:\n" +
                timeLineList.joinToString(",\n")
    }

    private fun saveList(type: RetroItemType) {
        val editor = sharedPreferences.edit()
        when (type) {
            RetroItemType.WENT_WELL -> {
                val json = Gson().toJson(wentWellList)
                editor.putString(Constants.PREF_RETRO_WW_KEY, json)
            }
            RetroItemType.COULD_BE_IMPROVE -> {
                val json = Gson().toJson(cbiList)
                editor.putString(Constants.PREF_RETRO_CBI_KEY, json)
            }
            RetroItemType.TIMELINE -> {
                val json = Gson().toJson(timeLineList)
                editor.putString(Constants.PREF_RETRO_TIMELINE_KEY, json)
            }
        }
        editor.apply()
    }

    private fun loadLists() {
        if (sharedPreferences.contains(Constants.PREF_RETRO_WW_KEY)) {
            val json = sharedPreferences.getString(Constants.PREF_RETRO_WW_KEY, "")
            val saved = getList(json!!)
            wentWellList.clear()
            saved.forEach { item -> wentWellList.add(item) }

            wwAdapter.notifyDataSetChanged()
        }

        if (sharedPreferences.contains(Constants.PREF_RETRO_CBI_KEY)) {
            val json = sharedPreferences.getString(Constants.PREF_RETRO_CBI_KEY, "")
            val saved = getList(json!!)
            cbiList.clear()
            saved.forEach { item -> cbiList.add(item) }

            cbiAdapter.notifyDataSetChanged()
        }

        if (sharedPreferences.contains(Constants.PREF_RETRO_TIMELINE_KEY)) {
            val json = sharedPreferences.getString(Constants.PREF_RETRO_TIMELINE_KEY, "")
            val saved = getList(json!!)
            timeLineList.clear()
            saved.forEach { item -> timeLineList.add(item) }

            timelineAdapter.notifyDataSetChanged()
        }

    }

    private fun getList(jsonArray: String): ArrayList<RetroItem> {
        val typeOfT = TypeToken.getParameterized(ArrayList::class.java, RetroItem::class.java).type
        return Gson().fromJson(jsonArray, typeOfT)
    }
}