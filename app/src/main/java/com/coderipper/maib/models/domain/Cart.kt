package com.coderipper.maib.models.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart",
)
data class Cart(
    @PrimaryKey val id: Long = System.nanoTime(),
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
)
