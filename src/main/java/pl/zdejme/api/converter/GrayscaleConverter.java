package pl.zdejme.api.converter;

import org.openimaj.image.MBFImage;

public class GrayscaleConverter implements ImageConverter {
    @Override
    public void processImage(MBFImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                float intensity = image.getBand(0).pixels[y][x] * 0.3F +
                        image.getBand(1).pixels[y][x] * 0.59F +
                        image.getBand(2).pixels[y][x] * 0.11F;
                image.setPixel(x, y, new Float[] { intensity, intensity, intensity });
            }
        }
    }
}
