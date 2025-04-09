package com.example.products.framework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    var title: String,
    var description: String,

    @ColumnInfo(name = "creation_date")
    var creationTime: Long,

    @ColumnInfo(name = "update_date")
    var updateTime: Long,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
)
