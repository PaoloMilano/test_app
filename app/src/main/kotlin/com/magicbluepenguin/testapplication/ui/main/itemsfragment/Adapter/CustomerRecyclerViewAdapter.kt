package com.magicbluepenguin.testapplication.ui.main.itemsfragment.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.magicbluepenguin.testapp.bindings.BoundPagedRecyclerViewAdapter
import com.magicbluepenguin.testapplication.BR
import com.magicbluepenguin.testapplication.R
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.databinding.ListItemBinding

class CustomerRecyclerViewAdapter() :
    BoundPagedRecyclerViewAdapter<Item, RecyclerView.ViewHolder>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item,
            parent,
            false
        )
    )

    override fun getProgressViewHolder(parent: ViewGroup, viewType: Int) =
        object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.refresh_list_item,
                parent,
                false
            )
        ) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                getItem(position)?.let {
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
