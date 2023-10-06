package com.fort.testbackgroundlocationservice.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDAO {
    @Query("SELECT * FROM location_data")
    fun getlocation_dataList(): List<EntityLocation>

    @Query("SELECT * FROM location_data")
    fun getlocation_dataLiveList(): LiveData<List<EntityLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location_data: EntityLocation)


    @Query("DELETE FROM location_data")
    fun clearLocationList()

    @Query("SELECT * FROM location_data ORDER BY id DESC LIMIT 1")
    fun getFirstReversedLocationData(): LiveData<EntityLocation?>

//
//    @Query("SELECT * FROM location_data WHERE videoId = :bookId")
//    fun bookExists(bookId:Int): Boolean
//
//    @Query("DELETE FROM location_data WHERE videoId = :bookId")
//    fun deleteByBookId(bookId: Int)
}