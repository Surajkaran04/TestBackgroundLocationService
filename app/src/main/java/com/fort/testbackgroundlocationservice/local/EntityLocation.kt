package com.fort.testbackgroundlocationservice.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "location_data")
data class EntityLocation(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "latitude") var latitude: String,
    @ColumnInfo(name = "longitude") val longitude: String,
    @ColumnInfo(name = "time") var time: String,
): Serializable