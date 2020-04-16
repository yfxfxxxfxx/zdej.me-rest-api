package pl.zdejme.api.converter;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import pl.zdejme.api.drawing.AsciiCharset;

public class AsciiConverter implements ImageConverter {

    private final AsciiCharset asciiCharset;

    public AsciiConverter() {
        this.asciiCharset = new AsciiCharset();
    }

    @Override
    public void processImage(MBFImage image) {
        ImageUtilities.assignBufferedImage(this.asciiCharset.convertImage(image), image);
    }
}
