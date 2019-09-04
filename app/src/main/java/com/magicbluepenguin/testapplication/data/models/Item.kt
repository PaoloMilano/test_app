package com.magicbluepenguin.testapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.magicbluepenguin.testapp.bindings.DifferentiableObject

@Entity
data class Item(@PrimaryKey val img: String, val text: String, val confidence: Float) : DifferentiableObject {
    override fun hasSameContents(other: DifferentiableObject): Boolean {
        return if (other is Item) {
            other.img == img
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