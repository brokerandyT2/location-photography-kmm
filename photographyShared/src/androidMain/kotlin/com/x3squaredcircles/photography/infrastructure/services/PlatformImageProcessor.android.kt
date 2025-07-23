// photographyShared/src/androidMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/PlatformImageProcessor.android.kt
package com.x3squaredcircles.photography.infrastructure.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

import com.x3squaredcircles.photography.domain.services.HistogramColor
import com.x3squaredcircles.photography.domain.services.ImageAnalysisData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

actual fun createPlatformImageProcessor(): PlatformImageProcessor {
    return PlatformImageProcessor(null)
}

actual class PlatformImageProcessor(
    private val context: Context?
) {
    actual suspend fun loadImageFromPath(imagePath: String): PlatformImage? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                bitmap?.let { AndroidImage(it) }
            } catch (e: Exception) {
                null
            }
        }
    }

    actual suspend fun extractHistogramData(image: PlatformImage): ImageAnalysisData {
        return withContext(Dispatchers.Default) {
            val androidImage = image as AndroidImage
            val bitmap = androidImage.bitmap

            val width = bitmap.width
            val height = bitmap.height
            val totalPixels = width * height

            val redHistogram = DoubleArray(256) { 0.0 }
            val greenHistogram = DoubleArray(256) { 0.0 }
            val blueHistogram = DoubleArray(256) { 0.0 }
            val luminanceHistogram = DoubleArray(256) { 0.0 }

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = bitmap.getPixel(x, y)

                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue).toInt().coerceIn(0, 255)

                    redHistogram[red] += 1.0
                    greenHistogram[green] += 1.0
                    blueHistogram[blue] += 1.0
                    luminanceHistogram[luminance] += 1.0
                }
            }

            for (i in 0..255) {
                redHistogram[i] = redHistogram[i] / totalPixels
                greenHistogram[i] = greenHistogram[i] / totalPixels
                blueHistogram[i] = blueHistogram[i] / totalPixels
                luminanceHistogram[i] = luminanceHistogram[i] / totalPixels
            }

            ImageAnalysisData(
                redHistogram = redHistogram,
                greenHistogram = greenHistogram,
                blueHistogram = blueHistogram,
                luminanceHistogram = luminanceHistogram,
                totalPixels = totalPixels.toLong()
            )
        }
    }

    actual suspend fun generateHistogramImage(
        histogram: DoubleArray,
        color: HistogramColor,
        fileName: String,
        outputDir: String
    ): String {
        return withContext(Dispatchers.Default) {
            try {
                val width = 512
                val height = 300
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                canvas.drawColor(Color.BLACK)

                val maxValue = histogram.maxOrNull() ?: 0.0
                val barWidth = width.toFloat() / 256

                val paint = Paint().apply {
                    when (color) {
                        HistogramColor.RED -> this.color = Color.RED
                        HistogramColor.GREEN -> this.color = Color.GREEN
                        HistogramColor.BLUE -> this.color = Color.BLUE
                        HistogramColor.LUMINANCE -> this.color = Color.WHITE
                    }
                    strokeWidth = 1f
                }

                for (i in 0..255) {
                    val x = i * barWidth
                    val barHeight = ((histogram[i] / maxValue) * height).toFloat()
                    canvas.drawLine(x, height.toFloat(), x, height - barHeight, paint)
                }

                val outputFile = File(context?.cacheDir ?: File("."), "$outputDir/$fileName.png")
                outputFile.parentFile?.mkdirs()

                FileOutputStream(outputFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                outputFile.absolutePath
            } catch (e: Exception) {
                ""
            }
        }
    }

    actual suspend fun generateStackedHistogramImage(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        fileName: String,
        outputDir: String
    ): String {
        return withContext(Dispatchers.Default) {
            try {
                val width = 512
                val height = 300
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                canvas.drawColor(Color.BLACK)

                val maxValue = max(
                    max(redHistogram.maxOrNull() ?: 0.0, greenHistogram.maxOrNull() ?: 0.0),
                    max(blueHistogram.maxOrNull() ?: 0.0, luminanceHistogram.maxOrNull() ?: 0.0)
                )

                val barWidth = width.toFloat() / 256

                val redPaint = Paint().apply { color = Color.argb(128, 255, 0, 0); strokeWidth = 1f }
                val greenPaint = Paint().apply { color = Color.argb(128, 0, 255, 0); strokeWidth = 1f }
                val bluePaint = Paint().apply { color = Color.argb(128, 0, 0, 255); strokeWidth = 1f }
                val lumPaint = Paint().apply { color = Color.argb(128, 255, 255, 255); strokeWidth = 1f }

                for (i in 0..255) {
                    val x = i * barWidth

                    val redHeight = ((redHistogram[i] / maxValue) * height).toFloat()
                    val greenHeight = ((greenHistogram[i] / maxValue) * height).toFloat()
                    val blueHeight = ((blueHistogram[i] / maxValue) * height).toFloat()
                    val lumHeight = ((luminanceHistogram[i] / maxValue) * height).toFloat()

                    canvas.drawLine(x, height.toFloat(), x, height - redHeight, redPaint)
                    canvas.drawLine(x, height.toFloat(), x, height - greenHeight, greenPaint)
                    canvas.drawLine(x, height.toFloat(), x, height - blueHeight, bluePaint)
                    canvas.drawLine(x, height.toFloat(), x, height - lumHeight, lumPaint)
                }

                val outputFile = File(context?.cacheDir ?: File("."), "$outputDir/$fileName.png")
                outputFile.parentFile?.mkdirs()

                FileOutputStream(outputFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                outputFile.absolutePath
            } catch (e: Exception) {
                ""
            }
        }
    }
}

actual typealias PlatformImage = AndroidImage

class AndroidImage(val bitmap: Bitmap)