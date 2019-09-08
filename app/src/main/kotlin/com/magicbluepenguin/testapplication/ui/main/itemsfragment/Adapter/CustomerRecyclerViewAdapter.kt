package com.magicbluepenguin.testapplication.ui.main.itemsfragment.Adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.magicbluepenguin.testapp.bindings.BoundPagedRecyclerViewAdapter
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.databinding.ListItemBinding
import kotlinx.android.synthetic.main.list_item.view.imageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CustomerRecyclerViewAdapter() :
    BoundPagedRecyclerViewAdapter<Item, RecyclerView.ViewHolder>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            com.magicbluepenguin.testapplication.R.layout.list_item,
            parent,
            false
        )
    )

    override fun getProgressViewHolder(parent: ViewGroup, viewType: Int) =
        object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                com.magicbluepenguin.testapplication.R.layout.refresh_list_item,
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
    RecyclerView.ViewHolder(binding.root), CoroutineScope, View.OnAttachStateChangeListener {

    val supervisor = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = supervisor + Dispatchers.Main
    var imageJob: Job? = null

    var itemId: String? = null

    init {
        (itemView.context as? AppCompatActivity)?.lifecycle?.run {
            addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    supervisor.cancel()
                }
            })
        }
    }

    fun bind(item: Item) {
        binding.item = item

        if (item._id == itemId) {
            return
        }
        itemView.imageView.setImageResource(
            android.R.drawable.screen_background_dark_transparent
        )

        itemView.removeOnAttachStateChangeListener(this)
        itemView.addOnAttachStateChangeListener(this)

        imageJob = launch {
            val decodedByte = withContext(Dispatchers.Default) {
                val decodedString = Base64.decode(item.img, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            }
            itemView.imageView.setImageBitmap(decodedByte)
            itemId = item._id
        }

        launch {
            imageJob?.join()
        }
    }

    override fun onViewDetachedFromWindow(p0: View?) {
        imageJob?.cancel()
        itemView.removeOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(p0: View?) {
    }
}
