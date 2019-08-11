package hu.zeletrik.dsmtimer.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.zeletrik.dsmtimer.R

class RetroFragment : Fragment() {

    private lateinit var rootView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.retro_fragment, container, false)

        return rootView
    }
}