package com.example.streaming.base

import android.annotation.TargetApi
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.io.IOException
import java.io.InputStream


object RealPart {
    private const val TAG = "IntentBitmapFetch"
    private const val COLON_SEPARATOR = ":"
    private const val IMAGE = "image"

    @Nullable
    fun getBitmap(context:Context,@NonNull bitmapUri: Uri): Bitmap? {
        val `is`: InputStream? = context.getContentResolver().openInputStream(bitmapUri)
        var bitmap = BitmapFactory.decodeStream(`is`, null, getBitmapOptions())
        val imgRotation:Float = getImageRotationDegrees(context,bitmapUri).toFloat()
        var endRotation:Float = if (imgRotation < 0) -imgRotation else imgRotation
        endRotation %= 360
        endRotation = 90 * (endRotation / 90)
        if (endRotation > 0 && bitmap != null) {
            val m = Matrix()
            m.setRotate(endRotation)
            val tmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
            if (tmp != null) {
                bitmap.recycle()
                bitmap = tmp
            }
        }
        return bitmap
    }

    private fun getBitmapOptions(): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        return options
    }

    private fun calculateScaleFactor(
        @NonNull bitmapOptionsMeasureOnly: BitmapFactory.Options,
        imageMaxDimen: Int
    ): Int {
        var inSampleSize = 1
        if (bitmapOptionsMeasureOnly.outHeight > imageMaxDimen || bitmapOptionsMeasureOnly.outWidth > imageMaxDimen) {
            val halfHeight = bitmapOptionsMeasureOnly.outHeight / 2
            val halfWidth = bitmapOptionsMeasureOnly.outWidth / 2
            while (halfHeight / inSampleSize > imageMaxDimen && halfWidth / inSampleSize > imageMaxDimen) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun getImageRotationDegrees(context:Context,@NonNull imgUri: Uri): Int {
        var photoRotation: Int = ExifInterface.ORIENTATION_UNDEFINED
        try {
            var hasRotation = false
            //If image comes from the gallery and is not in the folder DCIM (Scheme: content://)
            val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
            val cursor: Cursor? =
                context.getContentResolver().query(imgUri, projection, null, null, null)
            if (cursor != null) {
                if (cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
                    photoRotation = cursor.getInt(cursor.getColumnIndex(projection[0]))
                    hasRotation = photoRotation != 0

                }
                cursor.close()
            }

            //If image comes from the camera (Scheme: file://) or is from the folder DCIM (Scheme: content://)
            if (!hasRotation) {
                val exif = getAbsolutePath(context,imgUri)?.let { ExifInterface(it) }
                val exifRotation: Int? = exif?.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (exifRotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        photoRotation = 90
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        photoRotation = 180
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        photoRotation = 270
                    }
                }

            }
        } catch (e: IOException) {

        }
        return photoRotation
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getAbsolutePath(context: Context,uri: Uri): String? {
        //Code snippet edited from: http://stackoverflow.com/a/20559418/2235133
        var filePath: String? = uri.getPath()
        // Will return "image:x*"
        val wholeID = TextUtils.split(DocumentsContract.getDocumentId(uri), COLON_SEPARATOR)
        // Split at colon, use second item in the array
        val type = wholeID[0]
        if (IMAGE.equals(
                type,
                ignoreCase = true
            )
        ) { //If it not type image, it means it comes from a remote location, like Google Photos
            val id = wholeID[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)
            // where id is equal to
            val sel = MediaStore.Images.Media._ID + "=?"
            val cursor: Cursor? = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )
            if (cursor != null) {
                val columnIndex: Int = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
            }

        }
        return filePath
    }

}