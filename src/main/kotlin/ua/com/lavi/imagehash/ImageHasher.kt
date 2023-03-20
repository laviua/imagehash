package ua.com.lavi.imagehash

import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * Represents an image hashing algorithm.
 */
interface ImageHasher {

    /**
     * Hashes the image represented by the given byte array with the specified dimensions.
     *
     * @param bytes the byte array representing the image.
     * @param width the width of the image.
     * @param height the height of the image.
     * @return the hash of the image as a String.
     */
    fun hash(bytes: ByteArray, width: Int, height: Int): String

    /**
     * Hashes the image represented by the given byte array with its original dimensions.
     *
     * @param bytes the byte array representing the image.
     * @return the hash of the image as a String.
     */
    fun hash(bytes: ByteArray): String

    /**
     * Hashes the image from the given InputStream with the specified dimensions.
     *
     * @param inputStream the InputStream containing the image data.
     * @param width the width of the image.
     * @param height the height of the image.
     * @return the hash of the image as a String.
     */
    fun hash(inputStream: InputStream, width: Int, height: Int): String

    /**
     * Hashes the image from the given InputStream with its original dimensions.
     *
     * @param inputStream the InputStream containing the image data.
     * @return the hash of the image as a String.
     */
    fun hash(inputStream: InputStream): String

    /**
     * Hashes the given BufferedImage with the specified dimensions.
     *
     * @param image the BufferedImage representing the image.
     * @param width the width of the image.
     * @param height the height of the image.
     * @return the hash of the image as a String.
     */
    fun hash(image: BufferedImage, width: Int, height: Int): String

    /**
     * Hashes the given BufferedImage with its original dimensions.
     *
     * @param image the BufferedImage representing the image.
     * @return the hash of the image as a String.
     */
    fun hash(image: BufferedImage): String

}