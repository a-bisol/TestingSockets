package com.example.testingsockets

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.testingsockets.data.CroppedResult
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

object ObjectDetect {
    val TAG: String = "ObjectDetect"
    private lateinit var detector: ObjectDetector
    val target = "person"

    fun initDetector(context: Context) {
        val options =
            ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(5).setScoreThreshold(0.4f)
                .build()
        detector = ObjectDetector.createFromFileAndOptions(context, "model.tflite", options)
        Log.d(TAG, "Detector initialized")
    }

    fun runObjectDetection(bitmap: Bitmap): List<CroppedResult>? {
        val image = TensorImage.fromBitmap(bitmap)
        val results = detector.detect(image)
        val finalResults = mutableListOf<CroppedResult>()

        for (result in results) {
            for (category in result.categories) {
                if (category.label.lowercase() == target) {
                    val label = category.label
                    val score = category.score.times(100).toInt()
                    Log.d("Detected", "$label $score")

                    val tempDims = mutableMapOf<String, Int>()
                    val bBox = result.boundingBox
                    val pad = 0.35
                    //If boundingBox+pad extends past bitmap, set to edge to 1, otherwise give padding
                    val leftMargin = (bBox.left-(bBox.width()*pad)).toInt()
                    if (leftMargin < 0) {
                        tempDims["Left"] = 1
                    } else {
                        tempDims["Left"] = (((bBox.left) - (bBox.width()*pad)).toInt())
                    }
                    val topMargin = (bBox.top-(bBox.height()*pad)).toInt()
                    if (topMargin < 0) {
                        tempDims["Top"] = 1
                    } else {
                        tempDims["Top"] = ((bBox.top) - (bBox.height()*pad)).toInt()
                    }

                    //If boundingBox+pad extends past bitmap, set to edge to bitmap constraint, otherwise give padding
                    val rightMargin = ((1+(2*pad))*bBox.width()).toInt() + tempDims["Left"]!!
                    if (rightMargin > bitmap.width) {
                        tempDims["Width"] = ((bitmap.width - tempDims["Left"]!!) - 1)
                    } else {
                        tempDims["Width"] = (((1+(2*pad))*bBox.width()).toInt())
                    }
                    val bottomMargin = ((1+(2*pad))*bBox.height()).toInt() + tempDims["Top"]!!
                    if (bottomMargin > bitmap.height) {
                        tempDims["Height"] = ((bitmap.height - tempDims["Top"]!!) - 1)
                    } else {
                        tempDims["Height"] = (((1+(2*pad))*bBox.height()).toInt())
                    }

                    val tempBitmap = Bitmap.createBitmap(
                        bitmap,
                        tempDims["Left"]!!,
                        tempDims["Top"]!!,
                        tempDims["Width"]!! - 1,
                        tempDims["Height"]!! - 1
                    )
                    finalResults.add(CroppedResult(tempBitmap, label, score))
                }
                Log.d("DETECTED",category.label+" "+category.score)
            }
        }
        if (finalResults.isNotEmpty()) {
            for (result in finalResults) {
                Log.d("ResultOut",result.label+" "+result.score)
            }
            return finalResults
        }
        return null
    }
}