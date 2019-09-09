package com.magicbluepenguin.testapplication.ui.main.itemsfragment.Adapter

import android.graphics.Bitmap
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
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.databinding.ImageListItemBinding
import com.magicbluepenguin.testapplication.util.toBitMap
import kotlinx.android.synthetic.main.image_list_item.view.imageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ImageItemsRecyclerViewAdapter() :
    BoundPagedRecyclerViewAdapter<ImageItem, RecyclerView.ViewHolder>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int) = ImageItemViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            com.magicbluepenguin.testapplication.R.layout.image_list_item,
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
            is ImageItemViewHolder -> {
                getItem(position)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    class ImageItemViewHolder(val binding: ImageListItemBinding) :
        RecyclerView.ViewHolder(binding.root), CoroutineScope, View.OnAttachStateChangeListener {

        private val supervisor = SupervisorJob()
        override val coroutineContext: CoroutineContext
            get() = supervisor + Dispatchers.Main
        private var imageJob: Job? = null

        private var itemId: String? = null

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

        fun bind(item: ImageItem) = launch {
            binding.item = item

            if (item._id != itemId) {

                itemView.imageView.setImageResource(
                    android.R.drawable.screen_background_dark_transparent
                )

                itemView.removeOnAttachStateChangeListener(this@ImageItemViewHolder)
                itemView.addOnAttachStateChangeListener(this@ImageItemViewHolder)

                imageJob = async(Dispatchers.Default) { item.img.toBitMap() }
                ((imageJob as? Deferred<*>)?.await() as? Bitmap).run {
                    itemView.imageView.setImageBitmap(this)
                }
                itemId = item._id
            }
        }

        override fun onViewDetachedFromWindow(p0: View?) {
            imageJob?.cancel()
            itemView.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(p0: View?) {
        }
    }
}