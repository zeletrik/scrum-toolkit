package hu.zeletrik.dsmtimer

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



class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val simpleAdapter = MemberAdapter((1..5).map { "Attendee: $it" }.toMutableList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val simpleSeekBar = findViewById<SeekBar>(R.id.baseTimeSeekBar)
        val baseTimeText = findViewById<TextView>(R.id.baseTimeValue)
        val numberPicker = findViewById<NumberPicker>(R.id.numberPicker)
        val maxNumberCheckBox = findViewById<CheckBox>(R.id.fixNumAttendee)
        val attendeeListCheckBox = findViewById<CheckBox>(R.id.attendeeList)
        val addAttendeeButton = findViewById<Button>(R.id.addButton)
        val newAttendeeEditText = findViewById<EditText>(R.id.attendeeName)
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = simpleAdapter

        numberPicker.minValue = 2
        numberPicker.maxValue = 25
        numberPicker.setOnValueChangedListener(onNPValueChangeListener)

        maxNumberCheckBox.setOnCheckedChangeListener { _, isChecked ->
            numberPicker.isVisible = isChecked
            if (isChecked) {
                newAttendeeEditText.isVisible = !isChecked
                addAttendeeButton.isVisible = !isChecked
                attendeeListCheckBox.isChecked = !isChecked
                mRecyclerView.isVisible = !isChecked
            }
        }

        attendeeListCheckBox.setOnCheckedChangeListener { _, isChecked ->
            newAttendeeEditText.isVisible = isChecked
            addAttendeeButton.isVisible = isChecked
            mRecyclerView.isVisible = isChecked
            if (isChecked) {
                numberPicker.isVisible = !isChecked
                maxNumberCheckBox.isChecked = !isChecked
            }
        }

        attendeeName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    simpleAdapter.addItem(newAttendeeEditText.text.toString())
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
                Toast.makeText(
                    this@MainActivity, "Seek bar progress is :$progressChangedValue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as MemberAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        addAttendeeButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.addButton -> {
                val newAttendeeName = findViewById<EditText>(R.id.attendeeName)
                simpleAdapter.addItem(newAttendeeName.text.toString())
            }
        }

    }

    private val onNPValueChangeListener = NumberPicker.OnValueChangeListener { numberPicker, i, i1 ->
        Toast.makeText(
            this@MainActivity,
            "selected number " + numberPicker.value, Toast.LENGTH_SHORT
        ).show()
    }
}
