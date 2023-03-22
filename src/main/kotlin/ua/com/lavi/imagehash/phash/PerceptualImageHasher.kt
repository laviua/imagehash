package ua.com.lavi.imagehash.phash

import ua.com.lavi.imagehash.ImageHasher
import java.awt.Color
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * PerceptualHasher takes an input image, resizes it, converts it to a different color space, applies DCT, and generates a perceptual hash based on the top-left 8x8 matrix of DCT coefficients.
 * The resulting hash is returned as a hexadecimal string.
 */
class PerceptualImageHasher: ImageHasher {

    // requires for resize
    private val RESIZE_WIDTH = 32
    private val RESIZE_HEIGHT = 32

    override fun hash(bytes: ByteArray, width: Int, height: Int): String {
        return hash(ByteArrayInputStream(bytes), width, height)
    }

    override fun hash(bytes: ByteArray): String {
        return hash(bytes, RESIZE_WIDTH, RESIZE_HEIGHT)
    }

    override fun hash(inputStream: InputStream, width: Int, height: Int): String {
        return hash(ImageIO.read(inputStream), width, height)
    }

    override fun hash(inputStream: InputStream): String {
        return hash(inputStream, RESIZE_WIDTH, RESIZE_HEIGHT)
    }

    override fun hash(image: BufferedImage): String {
        return hash(image, RESIZE_WIDTH, RESIZE_HEIGHT)
    }

    override fun hash(image: BufferedImage, width: Int, height: Int): String {

        val resizedImage = lanczosResize(image, width, height)
        val ycbcrImage = convertToYCbCr(resizedImage)
        val dctImage = applyDCT(ycbcrImage)
        val dctTopLeft = getTopLeftMatrix(dctImage, 8, 8)
        val averageValue = getAverageValue(dctTopLeft)
        val binaryString = buildBinaryString(dctTopLeft, averageValue)
        return binaryStringToHexString(binaryString)
    }

    private fun convertToYCbCr(image: BufferedImage): BufferedImage {
        val op = ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_PYCC), null)
        return op.filter(image, null)
    }

    private fun applyDCT(image: BufferedImage): Array<DoubleArray> {
        val width = image.width
        val height = image.height
        val dctMatrix = Array(width) { DoubleArray(height) }

        for (u in 0 until width) {
            for (v in 0 until height) {
                var sum = 0.0
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val pixelValue = Color(image.getRGB(x, y)).red
                        val cosineTerm = Math.cos(((2 * x + 1) * u * Math.PI) / (2 * width)) *
                                Math.cos(((2 * y + 1) * v * Math.PI) / (2 * height))
                        sum += pixelValue * cosineTerm
                    }
                }
                val scaleFactor = getScaleFactor(u, width) * getScaleFactor(v, height)
                dctMatrix[u][v] = scaleFactor * sum
            }
        }

        return dctMatrix
    }

    private fun getScaleFactor(index: Int, size: Int): Double {
        return if (index == 0) Math.sqrt(1.0 / size) else Math.sqrt(2.0 / size)
    }

    private fun getTopLeftMatrix(matrix: Array<DoubleArray>, width: Int, height: Int): Array<DoubleArray> {
        return Array(width) { x -> DoubleArray(height) { y -> matrix[x][y] } }
    }

    private fun getAverageValue(matrix: Array<DoubleArray>): Double {
        var sum = 0.0
        for (row in matrix) {
            for (value in row) {
                sum += value
            }
        }
        return sum / (matrix.size * matrix[0].size)
    }

    private fun buildBinaryString(matrix: Array<DoubleArray>, averageValue: Double): String {
        return matrix.joinToString("") { row ->
            row.joinToString("") { value ->
                if (value >= averageValue) "1" else "0"
            }
        }
    }

    fun lanczosResize(image: BufferedImage, width: Int, height: Int, a: Int = 3): BufferedImage {
        val resizedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        val scaleX = image.width.toDouble() / width
        val scaleY = image.height.toDouble() / height

        val translateX = 0.5 * scaleX - 0.5
        val translateY = 0.5 * scaleY - 0.5

        for (x in 0 until width) {
            for (y in 0 until height) {
                val srcX = (x * scaleX + translateX).coerceIn(0.0, image.width - 1.0)
                val srcY = (y * scaleY + translateY).coerceIn(0.0, image.height - 1.0)

                var red = 0.0
                var green = 0.0
                var blue = 0.0
                var weightSum = 0.0

                for (j in (-a + 1)..a) {
                    for (i in (-a + 1)..a) {
                        val lanczosVal = lanczos(i.toDouble(), a.toDouble()) * lanczos(j.toDouble(), a.toDouble())
                        val sampleX = (Math.floor(srcX) + i).toInt().coerceIn(0, image.width - 1)
                        val sampleY = (Math.floor(srcY) + j).toInt().coerceIn(0, image.height - 1)
                        val pixel = Color(image.getRGB(sampleX, sampleY))

                        red += lanczosVal * pixel.red
                        green += lanczosVal * pixel.green
                        blue += lanczosVal * pixel.blue
                        weightSum += lanczosVal
                    }
                }

                val newRed = (red / weightSum).coerceIn(0.0, 255.0).toInt()
                val newGreen = (green / weightSum).coerceIn(0.0, 255.0).toInt()
                val newBlue = (blue / weightSum).coerceIn(0.0, 255.0).toInt()

                val newColor = Color(newRed, newGreen, newBlue).rgb
                resizedImage.setRGB(x, y, newColor)
            }
        }
        return resizedImage
    }

    private fun lanczos(x: Double, a: Double): Double {
        if (x == 0.0) return 1.0
        if (kotlin.math.abs(x) >= a) return 0.0

        val pix = Math.PI * x
        return kotlin.math.sin(pix) * kotlin.math.sin(pix / a) / (pix * pix)
    }

    private fun binaryStringToHexString(binaryString: String): String {
        val hexString = StringBuilder()
        var index = 0
        while (index < binaryString.length) {
            val currentChunk = binaryString.substring(index, index + 4)
            val decimalValue = Integer.parseInt(currentChunk, 2)
            hexString.append(Integer.toHexString(decimalValue))
            index += 4
        }
        return hexString.toString()
    }
}