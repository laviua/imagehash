package ua.com.lavi.imagehash.phash

import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import ua.com.lavi.imagehash.HashSearchResult
import ua.com.lavi.imagehash.matcher.HammingImageMatcher
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

internal class PerceptualHasherTest {

    private val imageHasher = PerceptualImageHasher()
    private val imageMatcher = HammingImageMatcher()

    private val imageDataList = listOf(
        PerceptualHasherTest::class.java.getResource("/images/margot-robbie.jpg")!!.readBytes(),
        PerceptualHasherTest::class.java.getResource("/images/sample-photo.jpg")!!.readBytes(),
        PerceptualHasherTest::class.java.getResource("/images/img.jpg")!!.readBytes(),
        PerceptualHasherTest::class.java.getResource("/images/img.png")!!.readBytes(),
        PerceptualHasherTest::class.java.getResource("/images/test1.webp")!!.readBytes(),
        PerceptualHasherTest::class.java.getResource("/images/test2.webp")!!.readBytes(),
        PerceptualHasherTest::class.java.getResource("/images/gray.jpg")!!.readBytes()
    )

    @Test
    fun shouldCalculatePhash() {
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[0]))) shouldBe "cd49ae95814bcd34"
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[1]))) shouldBe "f0e539990bd430f4"
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[2]))) shouldBe "d535fcc8c84c2533"
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[3]))) shouldBe "fb54fcc9e04c81e0"
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[4]))) shouldBe "818c1f73789e912d"
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[5]))) shouldBe "d89b32702d61e566"
        imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[6]))) shouldBe "8f90970cb4f60ba5"
    }

    @Test
    fun shouldShowMaxDistanceBetweenDifferentImages() {

        val hash1 = imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[0])))
        val hash2 = imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[1])))
        val distance = imageMatcher.distance(hash1, hash2)
        distance shouldBeGreaterThanOrEqual 22.0
    }

    @Test
    fun allHashesShouldBeUniq() {
        val allHashes = hashSetOf<String>()
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[0]))))
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[1]))))
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[2]))))
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[3]))))
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[4]))))
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[5]))))
        allHashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(imageDataList[6]))))
        allHashes.size shouldBe 7
    }

    @Test
    fun shouldResizeCorrectlyWithMultipleDimensions() {

        val scales = listOf(0.5, 2.0)

        for (scale in scales.parallelStream()) {
            for ((index, imageData) in imageDataList.withIndex()) {
                // Compute the original hash
                val originalImage = ImageIO.read(ByteArrayInputStream(imageData))
                val originalHash = imageHasher.hash(originalImage)

                // Calculate new dimensions while maintaining aspect ratio
                val newWidth = (originalImage.width * scale).toInt()
                val newHeight = (originalImage.height * scale).toInt()

                val resizedImage = imageHasher.lanczosResize(originalImage, newWidth, newHeight)
                val resizedHash = imageHasher.hash(resizedImage)

                val distance = imageMatcher.distance(originalHash, resizedHash)
                println("Scale: $scale of image: $index. Distance: $distance. Original hash: $originalHash. Got: $resizedHash")
                distance shouldBeLessThan 20.0
            }
        }
    }

    @Test
    fun theBestMatch() {

        val hashes = arrayListOf<String>()
        for (bytes in imageDataList) {
            hashes.add(imageHasher.hash(ImageIO.read(ByteArrayInputStream(bytes))))
        }

        val index = 3
        val originalImage = ImageIO.read(ByteArrayInputStream(imageDataList[index]))
        val newWidth = (originalImage.width * 0.5).toInt()
        val newHeight = (originalImage.height * 0.5).toInt()

        val resizedHash = imageHasher.hash(imageHasher.lanczosResize(originalImage, newWidth, newHeight))

        val result: List<HashSearchResult> = imageMatcher.findTopXMostSimilar(targetHash = resizedHash, hashes = hashes, topX = 5)
        result.first().index shouldBe index

    }
}