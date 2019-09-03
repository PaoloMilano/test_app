package com.magicbluepenguin.testapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.magicbluepenguin.testapplication.R
import com.magicbluepenguin.testapplication.ui.main.viewmodel.ItemsViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val itemsViewModel = ItemsViewModel.getInstance(
            activity as AppCompatActivity,
            "463154134a6642d51c714d685ec0efcb"
        )
        itemsViewModel.fetchNextItems()
    }
}
