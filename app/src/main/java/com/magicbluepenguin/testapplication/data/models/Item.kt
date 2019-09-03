package com.magicbluepenguin.testapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(@PrimaryKey val img: String, val text: String, val confidence: Float)