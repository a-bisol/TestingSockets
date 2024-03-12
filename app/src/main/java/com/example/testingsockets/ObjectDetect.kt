package com.example.testingsockets

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.testingsockets.data.DetectionResult
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

object ObjectDetect {
    val TAG: String = "ObjectDetect"
    private lateinit var detector: ObjectDetector

    fun initDetector(context: Context) {
        val options =
            ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(5).setScoreThreshold(0.5f)
                .build()
        detector = ObjectDetector.createFromFileAndOptions(context, "model.tflite", options)
        Log.d(TAG, "Detector initialized")
    }

    fun runObjectDetection(bitmap: Bitmap): String {
        val image = TensorImage.fromBitmap(bitmap)
        val results = detector.detect(image)

        val resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val label = "${category.label}, ${category.score.times(100).toInt()}%"
            val score = "${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, label, score)
        }
        for (result in resultToDisplay.withIndex()) {
            val tempDims = mutableMapOf<String, Int>()
            val bBox = result.value.boundingBox
            if (bBox.left.toInt() < 0) {
                tempDims["Left"] = 1
            } else {
                tempDims["Left"] = (bBox.left.toInt())
            }
            if (bBox.top.toInt() < 0) {
                tempDims["Top"] = 1
            } else {
                tempDims["Top"] = (bBox.top.toInt())
            }
            if (bBox.width() + tempDims["Left"]!! > bitmap.width) {
                tempDims["Width"] = ((bitmap.width - tempDims["Left"]!!) - 1)
            } else {
                tempDims["Width"] = (bBox.width().toInt())
            }
            if (bBox.height() + tempDims["Top"]!! > bitmap.height) {
                tempDims["Height"] = ((bitmap.height - tempDims["Top"]!!) - 1)
            } else {
                tempDims["Height"] = (bBox.height().toInt())
            }

            val tempBitmap = Bitmap.createBitmap(
                bitmap,
                tempDims["Left"]!!,
                tempDims["Top"]!!,
                tempDims["Width"]!! - 1,
                tempDims["Height"]!! - 1
            )
            val b64 = Base64Util.bitmapToBase64(tempBitmap)
            return b64
        }
        return "Null"
    }

}