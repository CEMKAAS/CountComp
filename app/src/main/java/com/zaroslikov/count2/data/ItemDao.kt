package com.zaroslikov.count2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ItemDao {

    @Query("SELECT * from items")
    fun getItem() : Flow<List<Item>>

    @Query("SELECT * from items WHERE id = :id")
    fun getItemS(id: Int): Flow<Item>

    @Query("SELECT * from items WHERE lastCount = 1")
    fun getlastReadProject() : Flow<Item>

    @Query("UPDATE items SET lastCount = 0")
    suspend fun updateToCount()

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
