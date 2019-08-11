package hu.zeletrik.dsmtimer.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.zeletrik.dsmtimer.EstimateAdapter
import hu.zeletrik.dsmtimer.R
import android.widget.RadioGroup



class EstimateFragment : Fragment() {

    private val normalPoints: MutableList<String> =
        arrayListOf("0", "1.5", "1", "2", "3", "5", "8", "13", "20", "40", "100")

    private val tshirtPoints:MutableList<String> =
        arrayListOf("XS", "S", "M", "L", "XL", "XXL")

    private val fibonacciPoints:MutableList<String> =
        arrayListOf("0", "1", "2", "3", "5", "8", "13", "21", "34", "55", "89", "144")

    private val estimatePoints:MutableList<String> = ArrayList()

    private val adapter = EstimateAdapter(estimatePoints)

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.estimation_fragment, container, false)
        val mRecyclerView = rootView.findViewById<RecyclerView>(R.id.estimationList)
        mRecyclerView.layoutManager = GridLayoutManager(context, 3)
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

                    }
                }
            }

        rootView.findViewById<RadioButton>(R.id.radioNormal).isChecked = true


        return rootView
    }
}