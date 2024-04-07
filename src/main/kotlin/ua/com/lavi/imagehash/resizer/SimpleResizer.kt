package ua.com.lavi.imagehash.resizer

import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * Provides an implementation of the {@link ImageResizer} interface for resizing images.
 * This class utilizes high-quality rendering hints to ensure the resized image maintains
 * a high level of quality. Specifically, it employs the Bicubic interpolation method
 * along with other quality-enhancing rendering hints.
 */
class SimpleResizer : ImageResizer {

    /**
     * Resizes an image to the specified width and height while attempting to preserve the
     * original image's quality. This method applies several rendering hints to use the
     * Bicubic interpolation and other quality-enhancing settings.
     *
     * @param bufferedImage The source {@link BufferedImage} to be resized.
     * @param width The desired width of the resized image.
     * @param height The desired height of the resized image.
     * @return A new {@link BufferedImage} instance containing the resized image.
     */
    override fun resize(bufferedImage: BufferedImage, width: Int, height: Int): BufferedImage {
        val resizedImage = BufferedImage(width, height, bufferedImage.type)
        val g2d = resizedImage.createGraphics()

        // Set the rendering hints to use the Lanczos algorithm and adjust the quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)

        // Adjust the quality based on the provided parameter
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
        g2d.dispose()

        return resizedImage
    }
}