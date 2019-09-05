package com.magicbluepenguin.testapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.magicbluepenguin.testapp.bindings.BoundPagedRecyclerViewAdapter
import com.magicbluepenguin.testapplication.R
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.databinding.ListItemBinding
import com.magicbluepenguin.testapplication.databinding.MainFragmentBinding
import com.magicbluepenguin.testapplication.databinding.RefreshListItemBinding
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
                dataBinding.itemsListView.boundAdapter =
                    CustomerRecyclerViewAdapter()
                dataBinding.itemsViewModel = itemsViewModel
            }
        }
    }

    class CustomerRecyclerViewAdapter() :
        BoundPagedRecyclerViewAdapter<Item, RecyclerView.ViewHolder>() {

        private val VIEW_TYPE_ITEM = 0
        private val VIEW_TYPE_PROGRESS_BAR = 1

        override fun getItemCount(): Int {
            return super.getItemCount() + 1
        }

        override fun getItemViewType(position: Int): Int {
            if (position == itemCount - 1) {
                return VIEW_TYPE_PROGRESS_BAR
            }
            return VIEW_TYPE_ITEM
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            if (viewType == VIEW_TYPE_PROGRESS_BAR) {
                return ProgressBarItem(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.refresh_list_item,
                        parent,
                        false
                    )
                )
            }
            return ItemViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is ItemViewHolder -> getItem(position)?.let {
                    holder.bind(it)
                }
                is ProgressBarItem -> holder.bind(isFetchingMore)
            }
        }
    }

    open class ItemViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.item = item
        }
    }

    class ProgressBarItem(val binding: RefreshListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(isFetchingMore: Boolean) {
            binding.isFetchingMoreItems = isFetchingMore
        }
    }
}
