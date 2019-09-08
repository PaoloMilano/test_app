package com.magicbluepenguin.testapplication.ui.main.itemsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.magicbluepenguin.testapp.bindings.BoundPagedRecyclerViewAdapter
import com.magicbluepenguin.testapplication.BR
import com.magicbluepenguin.testapplication.R
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.databinding.ListItemBinding
import com.magicbluepenguin.testapplication.databinding.MainFragmentBinding
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.ItemsViewModel
import com.magicbluepenguin.testapplication.util.GenericNetworkError
import com.magicbluepenguin.testapplication.util.NetworkError
import com.magicbluepenguin.testapplication.util.NetworkUnavailableError

class MainFragment : Fragment() {

    companion object {
        fun newInstance() =
            MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.main_fragment,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(activity as? AppCompatActivity) {
            this?.let {

                val dataBinding = DataBindingUtil.setContentView<MainFragmentBinding>(
                    this,
                    R.layout.main_fragment
                ).apply {
                    lifecycleOwner = viewLifecycleOwner
                    itemsListView.boundAdapter = CustomerRecyclerViewAdapter()
                }

                val viewModel = ItemsViewModel.getInstanceWithCahedRepository(
                    this,
                    "463154134a6642d51c714d685ec0efcb"
                ).apply {
                    onDataStreamReadyListener {
                        dataBinding.items = itemsLiveData
                    }
                    dataBinding.itemsViewModel = this
                }

                viewModel.networkError.observe(viewLifecycleOwner,
                    Observer<NetworkError> { error ->
                        (activity?.findViewById(android.R.id.content) as View).run {
                            val errorStringRes = when (error!!) {
                                is GenericNetworkError -> R.string.generic_network_error
                                is NetworkUnavailableError -> R.string.network_unavailable_error
                            }

                            Snackbar.make(
                                this,
                                errorStringRes,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }

    class CustomerRecyclerViewAdapter() :
        BoundPagedRecyclerViewAdapter<Item, RecyclerView.ViewHolder>() {

        private val VIEW_TYPE_ITEM = 0
        private val VIEW_TYPE_PROGRESS_BAR = 1

        override fun getItemCount(): Int {
            var itemCount = super.getItemCount()
            if (topProgressVisibility) {
                itemCount += 1
            }
            if (bottomProgressVisibility) {
                itemCount += 1
            }
            return itemCount
        }

        override fun getItemViewType(position: Int): Int {
            if (topProgressVisibility && position == 0) {
                return VIEW_TYPE_PROGRESS_BAR
            }
            if (bottomProgressVisibility && position == itemCount - 1) {
                return VIEW_TYPE_PROGRESS_BAR
            }
            return VIEW_TYPE_ITEM
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            if (viewType == VIEW_TYPE_PROGRESS_BAR) {
                return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.refresh_list_item,
                        parent,
                        false
                    )
                ) {}
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
                is ItemViewHolder -> {
                    val adjustedPosition = if (topProgressVisibility) {
                        position - 1
                    } else {
                        position
                    }
                    getItem(adjustedPosition)?.let {
                        holder.bind(it)
                    }
                }
            }
        }
    }

    class ItemViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.item = item
            binding.notifyPropertyChanged(BR._all)
        }
    }
}
