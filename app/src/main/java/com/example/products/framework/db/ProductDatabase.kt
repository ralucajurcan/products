package com.example.products.framework.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class],
    version = 2,
    exportSchema = true
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}