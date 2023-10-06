package com.fort.testbackgroundlocationservice.local

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Activity.saveImageToInternalStorage(bitmap: Bitmap): Uri? {
    val mImageName = "idCard.jpg"
    val wrapper = ContextWrapper(this)
    var imageFile = wrapper.getDir("Images", Context.MODE_PRIVATE)
    imageFile = File(imageFile, "Pathsala_${mImageName}")
    try {
        var stream: OutputStream? = null
        stream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Uri.parse(imageFile.absolutePath)
}

fun Activity.getRealPathFromURI(contentUri: Uri): String {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = this.managedQuery(contentUri, proj, null, null, null)
    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    return cursor.getString(column_index)
}

 fun convertLongToTimeWithLocale(questionTimestamp: Long): String {
    val dateAsMilliSecond: Long = questionTimestamp
    val date = Date(dateAsMilliSecond)
    val language = "en"
    val formattedDateAsDigitMonth = SimpleDateFormat("dd/MM/yyyy", Locale(language))
    val formattedDateAsShortMonth = SimpleDateFormat("dd MMM yyyy", Locale(language))
    val formattedDateAsLongMonth = SimpleDateFormat("dd MMMM yyyy", Locale(language))
    return formattedDateAsShortMonth.format(date)
}
