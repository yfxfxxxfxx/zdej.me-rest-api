package pl.zdejme.api.converter.isolator;

import org.openimaj.image.MBFImage;
import pl.zdejme.api.converter.ImageConverter;

public class GreenIsolator extends BandIsolator implements ImageConverter {
    @Override
    public void processImage(MBFImage image) {
        isolateBand(image, new int[] { 0, 2 });
    }
}
