package hu.zeletrik.scrumtoolkit.presenter.activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import hu.zeletrik.scrumtoolkit.R

class EstimationCardActivity : BaseActivity() {

    private lateinit var valueText: TextView
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estiamtioncard)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        valueText = findViewById(R.id.estimationCardValue)

        val value = intent.getStringExtra("value")

        when (value) {
            "None" -> valueText.setBackgroundColor(getColor(R.color.risk_none))
            "Low" -> valueText.setBackgroundColor(getColor(R.color.risk_low))
            "Medium" -> valueText.setBackgroundColor(getColor(R.color.risk_medium))
            "High" -> valueText.setBackgroundColor(getColor(R.color.risk_high))
            "Very high" -> valueText.setBackgroundColor(getColor(R.color.risk_veryHigh))
            else -> {
                valueText.text = value
            }
        }


        findViewById<View>(R.id.estimationCardValueHolder).setOnClickListener {
            if (valueText.visibility == View.VISIBLE) finish()
            valueText.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}