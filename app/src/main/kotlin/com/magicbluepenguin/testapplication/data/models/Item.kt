package com.magicbluepenguin.testapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.magicbluepenguin.testapp.bindings.DifferentiableObject

@Entity
data class Item(@PrimaryKey val _id: String, val img: String, val text: String, val confidence: Float) : DifferentiableObject {
    override fun hasSameContents(other: DifferentiableObject): Boolean {
        return if (other is Item) {
            other._id == _id
        } else {
            false
        }
    }

    override fun hasSameId(other: DifferentiableObject): Boolean {
        return if (other is Item) {
            other.toString() == toString()
        } else {
            false
        }
    }
}