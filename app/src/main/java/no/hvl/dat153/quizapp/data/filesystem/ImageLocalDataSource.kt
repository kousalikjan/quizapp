package no.hvl.dat153.quizapp.data.filesystem

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File
import java.io.InputStream
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ImageLocalDataSource {

    @OptIn(ExperimentalUuidApi::class)
    fun saveImage(inputStream: InputStream, storageDir: File): Uri {
        storageDir.mkdir()
        val outputFile = File.createTempFile("image_${Uuid.random()}", ".jpg", storageDir)
        val outputStream = outputFile.outputStream()

        try {
            inputStream.copyTo(outputStream)
            return outputFile.toUri()
        } catch (t: Throwable) {
            Log.w("ImageLocalDataSource", "Failed to save image", t)
            throw t
        } finally {
            inputStream.close()
            outputStream.close()
        }
    }

    fun deleteImage(uri: Uri) {
        try {
            if (uri.scheme != "file") return

            val file = uri.toFile()
            file.delete()
        } catch (t: Throwable) {
            Log.w("ImageLocalDataSource", "Failed to delete image", t)
        }
    }
}
