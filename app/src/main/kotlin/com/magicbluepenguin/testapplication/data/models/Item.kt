package com.magicbluepenguin.testapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.magicbluepenguin.testapp.bindings.BoundPagedRecyclerViewAdapter

@Entity
data class Item(
    @PrimaryKey val _id: String,
    val img: String,
    val text: String,
    val confidence: Float
) :
    BoundPagedRecyclerViewAdapter.DifferentiableObject {
    override fun hasSameContents(other: BoundPagedRecyclerViewAdapter.DifferentiableObject): Boolean {
        return if (other is Item) {
            other.toString() == toString()
        } else {
            false
        }
    }

    override fun hasSameId(other: BoundPagedRecyclerViewAdapter.DifferentiableObject): Boolean {
        return if (other is Item) {
            other._id == _id
        } else {
            false
        }
    }
}