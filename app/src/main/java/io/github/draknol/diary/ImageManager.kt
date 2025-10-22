package io.github.draknol.diary

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.flow.first
import java.io.File

class ImageManager(private val context: Context, private val viewModel: DiaryViewModel) {
    // Thumbnail saving and loading
    var imageUpdated by mutableStateOf(value = false)


    /**
     * Caches the thumbnail of the selected image.
     * @param uri The URI of the selected image.
     */
    suspend fun cacheThumbnail(uri: Uri) {
        val imageLoader = ImageLoader(context = context)
        val request = ImageRequest.Builder(context = context)
            .data(data = uri)
            .size(size = 1080)
            .build()
        val result = imageLoader.execute(request)
        val bitmap = (result as SuccessResult).drawable.toBitmap()

        val file = File(context.cacheDir, "tmp_thumb_${viewModel.selectedEntry.value.id}.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it) }

        viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(
            imagePath = file.absolutePath
        )

        imageUpdated = !imageUpdated
    }


    /**
     * Saves the cached thumbnail to the app's files directory.
     */
    fun saveCachedThumbnail() {
        val tempPath = viewModel.selectedEntry.value.imagePath
        if (tempPath != null && tempPath.contains(other = "tmp_thumb")) {
            val tempFile = File(tempPath)
            val file = File(context.filesDir, "thumb_${viewModel.selectedEntry.value.id}.jpg")
            tempFile.copyTo(target = file, overwrite = true)
            tempFile.delete()

            viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(
                imagePath = file.absolutePath
            )
        }
    }


    /**
     * Deletes the cached thumbnail from the app's files directory.
     */
    suspend fun deleteCachedThumbnail() {
        val path = viewModel.selectedEntry.value.imagePath
        if (path != null) {
            File(path).delete()
            viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(
                imagePath = null
            )
            val entry = viewModel.getEntry(viewModel.selectedEntry.value.id).first()
            if (entry != null) {
                val updatedEntry = entry.copy(imagePath = null)
                viewModel.update(updatedEntry)
            }
        }
    }
}
