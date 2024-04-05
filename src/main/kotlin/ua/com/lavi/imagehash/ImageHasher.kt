package ua.com.lavi.imagehash

import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient

/**
 * Represents an image hashing algorithm.
 */
interface ImageHasher {

    /**
     * Hashes the image represented by the given byte array with its original dimensions.
     *
     * @param bytes the byte array representing the image.
     * @return the hash of the image as a String.
     */
    fun hash(bytes: ByteArray): String

    /**
     * Hashes the image from the given InputStream with its original dimensions.
     *
     * @param inputStream the InputStream containing the image data.
     * @return the hash of the image as a String.
     */
    fun hash(inputStream: InputStream): String

    /**
     * Hashes the given BufferedImage with its original dimensions.
     *
     * @param image the BufferedImage representing the image.
     * @return the hash of the image as a String.
     */
    fun hash(image: BufferedImage): String

    /**
     * Hashes the image from the given URI with its original dimensions.
     */
    fun hash(uri: URI): String

    /**
     * Hashes the image from the given URI with its original dimensions.
     */
    fun hash(uri: URI, client: HttpClient): String

}