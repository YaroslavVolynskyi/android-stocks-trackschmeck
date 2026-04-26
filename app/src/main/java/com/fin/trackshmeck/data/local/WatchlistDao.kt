package com.fin.trackshmeck.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlist_items ORDER BY id ASC")
    fun observeAll(): Flow<List<WatchlistItemEntity>>

    @Query("SELECT * FROM watchlist_items WHERE id = :id")
    suspend fun getById(id: Long): WatchlistItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistItemEntity): Long

    @Update
    suspend fun update(item: WatchlistItemEntity)

    @Query("DELETE FROM watchlist_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM watchlist_items ORDER BY id ASC")
    suspend fun getAll(): List<WatchlistItemEntity>

    @Query("SELECT COUNT(*) FROM watchlist_items")
    suspend fun count(): Int
}
