package com.magicbluepenguin.testapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.magicbluepenguin.testapp.bindings.BindableViewHolder
import com.magicbluepenguin.testapp.bindings.BoundPagedRecyclerViewAdapter
import com.magicbluepenguin.testapplication.R
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.databinding.ListItemBinding
import com.magicbluepenguin.testapplication.databinding.MainFragmentBinding
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

        with(activity as? AppCompatActivity) {
            this?.let {
                val itemsViewModel = ItemsViewModel.getInstance(
                    activity as AppCompatActivity,
                    "463154134a6642d51c714d685ec0efcb"
                )

                val dataBinding = DataBindingUtil.setContentView<MainFragmentBinding>(
                    this,
                    R.layout.main_fragment
                )
                dataBinding.lifecycleOwner = viewLifecycleOwner
                dataBinding.itemsListView.boundAdapter = CustomerRecyclerViewAdapter()
                dataBinding.itemsViewModel = itemsViewModel
            }
        }
    }

    class CustomerRecyclerViewAdapter :
        BoundPagedRecyclerViewAdapter<Item, ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return ItemViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item,
                    parent,
                    false
                )
            )
        }
    }

    class ItemViewHolder(binder: ListItemBinding) :
        BindableViewHolder<Item, ListItemBinding>(binder) {

        override fun bind(viewBinding: ListItemBinding, item: Item) {
            viewBinding.item = item
        }
    }
}
