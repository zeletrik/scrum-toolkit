package hu.zeletrik.scrumtoolkit.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.zeletrik.scrumtoolkit.adapter.EstimateAdapter
import hu.zeletrik.scrumtoolkit.R
import android.widget.RadioGroup



class EstimateFragment : Fragment() {

    private val coffee = String(Character.toChars(0x2615))
    private val infinity = String(Character.toChars(0x267e))
    private val normalPoints: MutableList<String> =
        arrayListOf("0", "1/2", "1", "2", "3", "5", "8", "13", "20", "40", "100", infinity, "?", coffee)

    private val tshirtPoints:MutableList<String> =
        arrayListOf("XS", "S", "M", "L", "XL", "XXL", infinity, "?", coffee)

    private val fibonacciPoints:MutableList<String> =
        arrayListOf("0", "1", "2", "3", "5", "8", "13", "21", "34", "55", "89", "144", infinity, "?", coffee)

    private val riskPoints:MutableList<String> =
        arrayListOf("None", "Low", "Medium", "High", "Very high", infinity, "?", coffee)

    private val estimatePoints:MutableList<String> = ArrayList()

    private val adapter = EstimateAdapter(estimatePoints)

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.estimation_fragment, container, false)
        val mRecyclerView = rootView.findViewById<RecyclerView>(R.id.estimationList)
        mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        mRecyclerView.adapter = adapter

        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radioEstimate)

        radioGroup.clearCheck()
        radioGroup
            .setOnCheckedChangeListener { _, checkedId ->
                estimatePoints.clear()
                when(checkedId) {
                    R.id.radioNormal -> {
                        normalPoints.forEach {
                            p: String -> estimatePoints.add(p)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    R.id.radioTShirt -> {
                        tshirtPoints.forEach {
                                p: String -> estimatePoints.add(p)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    R.id.radioFibonacci -> {
                        fibonacciPoints.forEach {
                                p: String -> estimatePoints.add(p)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    R.id.radioRisk -> {
                        riskPoints.forEach {
                                p: String -> estimatePoints.add(p)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        rootView.findViewById<RadioButton>(R.id.radioNormal).isChecked = true

        return rootView
    }
}