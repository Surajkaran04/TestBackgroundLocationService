package com.fort.testbackgroundlocationservice.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

class Converters {

    val gson = Gson()

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @TypeConverter
    fun from(list: List<String>?): String? {
        val string = if (list == null) null else gson.toJson(list)
        return string
    }

    @TypeConverter
    fun to(jsonData: String?): List<String>? {
        val gson = Gson()
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(jsonData, listType)
    }
}