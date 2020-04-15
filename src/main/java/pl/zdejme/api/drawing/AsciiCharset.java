package pl.zdejme.api.drawing;

import lombok.Getter;
import org.openimaj.image.MBFImage;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AsciiCharset {

    private static final String CHARACTER_BANK =
            "`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";
    private final StringMetrics stringMetrics;
    private final Map<Character, BufferedImage> charImages;

    public AsciiCharset() {
        this.stringMetrics = new StringMetrics(
                new JLabel().getFont(),
                new FontRenderContext(
                        new AffineTransform(), true, true
                ));
        this.charImages = createCharacterImages();
    }

    public Dimension getBaselineDimensions() {
        int baselineWidth = 0;
        int baselineHeight = 0;

        int tempWidth;
        int tempHeight;

        for (char c : CHARACTER_BANK.toCharArray()) {
            tempWidth = (int) (this.stringMetrics.getWidth(String.valueOf(c)));
            tempHeight = (int) (this.stringMetrics.getHeight(String.valueOf(c)));

            if (tempWidth > baselineWidth) {
                baselineWidth = tempWidth;
            }

            if (tempHeight > baselineHeight) {
                baselineHeight = tempHeight;
            }
        }

        if (baselineHeight > baselineWidth) {
            baselineWidth += baselineHeight - baselineWidth;
        } else if (baselineWidth > baselineHeight) {
            baselineHeight += baselineWidth - baselineHeight;
        }

        return new Dimension(baselineWidth, baselineHeight);
    }

    public Map<Character, BufferedImage> createCharacterImages() {
        Dimension baselineDimensions = getBaselineDimensions();
        BufferedImage charImg = new BufferedImage((int) baselineDimensions.getWidth(),
                (int) baselineDimensions.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics cg = charImg.getGraphics();
        Graphics2D charGraphics = (Graphics2D) cg;
        charGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        charGraphics.setFont(this.stringMetrics.font);
        FontMetrics fm = charGraphics.getFontMetrics();

        Map<Character, BufferedImage> charSet = new HashMap<>();

        for (char c : CHARACTER_BANK.toCharArray()) {
            String character = Character.toString(c);

            cg.setColor(Color.WHITE);
            cg.fillRect(0, 0, (int) baselineDimensions.getWidth(), (int) baselineDimensions.getHeight());
            cg.setColor(Color.BLACK);

            Rectangle rect = new TextLayout(character, fm.getFont(), fm.getFontRenderContext())
                    .getOutline(null).getBounds();

            cg.drawString(character, 0,
                    (int) (rect.getHeight() - rect.getMaxY()));


            charSet.put(c, generateBufferedImageValue(charImg));
        }
        return charSet;
    }

    public BufferedImage generateBufferedImageValue(BufferedImage bImage) {
        BufferedImage generatedBImage =
                new BufferedImage(bImage.getWidth(null), bImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = generatedBImage.createGraphics();
        bGr.drawImage(bImage, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return generatedBImage;
    }

    public BufferedImage convertImage(MBFImage image) {
        int outputImageWidth = (image.getWidth() / getBaselineDimensions().width)
                * getBaselineDimensions().width;
        int outputImageHeight = (image.getHeight() / getBaselineDimensions().height)
                * getBaselineDimensions().height;

        BufferedImage resultingImage = new BufferedImage(outputImageWidth, outputImageHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resultingImage.createGraphics();

        for (int y = 0; y < image.getHeight() ; y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if(x % getBaselineDimensions().width == 0 &&
                        y % getBaselineDimensions().height == 0) {
                    float intensity = image.getBand(0).pixels[y][x] * 0.3F +
                            image.getBand(1).pixels[y][x] * 0.59F +
                            image.getBand(2).pixels[y][x] * 0.11F;
                    g2d.drawImage(getCharImages().get(CHARACTER_BANK.charAt((int) (255 * intensity) / 4)), null, x, y);
                }
            }
        }

        return resultingImage;
    }
}
