package com.magicbluepenguin.testapp.bindings

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * View bindings for RecyclerView
 */
@BindingAdapter("items")
fun <T : DifferentiableObject> bindList(view: BoundRecyclerView<T>, list: List<T>?) {
    list?.let {
        view.boundAdapter?.setItems(it)
    }
}

@BindingAdapter("isFetchingMore")
fun onFetching(view: BoundRecyclerView<*>, isFetchingMore: Boolean) {
    view.boundAdapter?.isFetchingMore = isFetchingMore
}

@BindingAdapter("onLastItemShown")
fun onLastItemShown(view: BoundRecyclerView<*>, fetchNextFunction: () -> Unit) {
    (view.layoutManager as? LinearLayoutManager)?.let {
        view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (recyclerView.layoutManager as? LinearLayoutManager)?.let {
                    fetchNextFunction.invoke()
                }
            }
        })
    }
}

/**
 * Generic RecyclerView that uses generics to enable reuse with data bindings
 */
class BoundRecyclerView<T : DifferentiableObject>(context: Context, attrs: AttributeSet) :
    RecyclerView(context, attrs) {

    var boundAdapter: BoundPagedRecyclerViewAdapter<T, *>?
        set(value) {
            value?.let {
                adapter = it
            }
        }
        @Suppress("UNCHECKED_CAST")
        get() {
            if (adapter is BoundPagedRecyclerViewAdapter<*, *>) {
                return adapter as? BoundPagedRecyclerViewAdapter<T, *>
            }
            return null
        }
}

/**
 * Custom Adapter that uses generics to enable reuse with data bindings. This adapter is low on functionality, limiting
 * itself to providing fields for data to be updated while letting subclasses decide how to answer update events
 */
abstract class BoundPagedRecyclerViewAdapter<T : DifferentiableObject, I : RecyclerView.ViewHolder> :
    PagedListAdapter<T, I> {

    val itemList = ArrayList<T>()
    var isFetchingMore = false
        set(value) {
            field = value
            notifyItemChanged(itemCount)
        }

    constructor() : super(object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.hasSameId(newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.hasSameContents(newItem)
        }
    })

    override fun getItem(position: Int): T? {
        return if (position < itemList.size) {
            itemList.get(position)
        } else {
            null
        }
    }

    fun setItems(items: List<T>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount() = itemList.size
}

interface DifferentiableObject {
    fun hasSameId(other: DifferentiableObject): Boolean
    fun hasSameContents(other: DifferentiableObject): Boolean
}
