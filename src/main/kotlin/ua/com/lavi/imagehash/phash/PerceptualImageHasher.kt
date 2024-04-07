package ua.com.lavi.imagehash.phash

import ua.com.lavi.imagehash.ImageHasher
import ua.com.lavi.imagehash.resizer.SimpleResizer
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sqrt

class PerceptualImageHasher(private val hashSize: Int = 8,
                            private val highfreqFactor: Int = 4,
                            private val resizer: SimpleResizer = SimpleResizer()) : ImageHasher {

    override fun hash(bytes: ByteArray): String {
        return hash(ByteArrayInputStream(bytes))
    }

    override fun hash(inputStream: InputStream): String {
        return hash(ImageIO.read(inputStream))
    }

    override fun hash(image: BufferedImage): String {
        val resizedImage = resizer.resize(image, hashSize * highfreqFactor, hashSize * highfreqFactor)
        val dctMatrix = applyDCT(resizedImage)
        val dctLowFreq = getTopLeftMatrix(dctMatrix, hashSize, hashSize)
        val median = getMedianValue(dctLowFreq)
        val diffBits = buildDiffBits(dctLowFreq, median)

        return ImageHash(diffBits).hexHash()
    }

    override fun hash(uri: URI): String {
        val response: HttpResponse<ByteArray> = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
            .uri(uri)
            .build(), HttpResponse.BodyHandlers.ofByteArray())

        ByteArrayInputStream(response.body()).use { inputStream ->
            val image: BufferedImage = ImageIO.read(inputStream)
            val hash = hash(image)
            return hash
        }
    }

    override fun hash(uri: URI, client: HttpClient): String {
        val response: HttpResponse<ByteArray> = client.send(HttpRequest.newBuilder()
            .uri(uri)
            .build(), HttpResponse.BodyHandlers.ofByteArray())

        ByteArrayInputStream(response.body()).use { inputStream ->
            val image: BufferedImage = ImageIO.read(inputStream)
            val hash = hash(image)
            return hash
        }
    }

    private fun getMedianValue(matrix: Array<DoubleArray>): Double {
        val values = matrix.flatMap { it.toList() }.toMutableList()
        values.sort()
        val mid = values.size / 2
        return values[mid]
    }

    private fun buildDiffBits(matrix: Array<DoubleArray>, median: Double): BooleanArray {
        val diffBits = BooleanArray(matrix.size * matrix[0].size)
        var index = 0
        for (row in matrix) {
            for (value in row) {
                diffBits[index++] = value > median
            }
        }
        return diffBits
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
                        val cosineTerm = cos(((2 * x + 1) * u * Math.PI) / (2 * width)) *
                                cos(((2 * y + 1) * v * Math.PI) / (2 * height))
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
        return if (index == 0) sqrt(1.0 / size) else sqrt(2.0 / size)
    }

    private fun getTopLeftMatrix(matrix: Array<DoubleArray>, width: Int, height: Int): Array<DoubleArray> {
        return Array(width) { x -> DoubleArray(height) { y -> matrix[x][y] } }
    }

}

data class ImageHash(val diffBits: BooleanArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageHash

        if (!diffBits.contentEquals(other.diffBits)) return false

        return true
    }

    override fun hashCode(): Int {
        return diffBits.contentHashCode()
    }

    fun hexHash(): String {
        val sb = StringBuilder()
        var decimal = 0
        for ((index, bit) in this.diffBits.withIndex()) {
            decimal = (decimal shl 1) + if (bit) 1 else 0
            if ((index + 1) % 4 == 0) {
                sb.append(decimal.toString(16))
                decimal = 0
            }
        }
        if (decimal != 0) {
            sb.append(decimal.toString(16))
        }
        return sb.toString()
    }
}