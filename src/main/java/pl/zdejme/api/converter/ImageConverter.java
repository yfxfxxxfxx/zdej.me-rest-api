package pl.zdejme.api.converter;

import org.openimaj.image.MBFImage;

import java.io.IOException;

public interface ImageConverter {
    void processImage(MBFImage image) throws IOException;
}
