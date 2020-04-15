package pl.zdejme.api.converter;

import org.openimaj.image.MBFImage;

public class NegativeConverter implements ImageConverter {
    @Override
    public void processImage(MBFImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.getBand(0).pixels[y][x] = 1 - image.getBand(0).pixels[y][x];
                image.getBand(1).pixels[y][x] = 1 - image.getBand(1).pixels[y][x];
                image.getBand(2).pixels[y][x] = 1 - image.getBand(2).pixels[y][x];
            }
        }
    }
}
