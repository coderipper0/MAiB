package com.coderipper.maib.models.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


data class Size(
    val id: String = UUID.randomUUID().toString(),
    val size: Int = -1,
    //@ColumnInfo(name = "id_product") val id_product: Long
)
