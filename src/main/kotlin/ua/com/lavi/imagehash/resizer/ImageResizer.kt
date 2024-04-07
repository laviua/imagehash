package ua.com.lavi.imagehash.resizer

import java.awt.image.BufferedImage

interface ImageResizer {

    /**
     * Resizes an image to the specified width and height
     */
    fun resize(bufferedImage: BufferedImage, width: Int, height: Int): BufferedImage
}