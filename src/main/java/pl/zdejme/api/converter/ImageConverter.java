package pl.zdejme.api.converter;

import org.openimaj.image.MBFImage;


public interface ImageConverter {
    void processImage(MBFImage image);
}
