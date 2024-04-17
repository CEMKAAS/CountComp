package com.zaroslikov.count2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String, // название
    val count: Int, // Кол-во
    val step: Int,  // Шаг счетчика
    val lastCount: Int, // последний счет
    val time: String // время
)
