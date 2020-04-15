package pl.zdejme.api.converter.isolator;

import org.openimaj.image.MBFImage;

public abstract class BandIsolator {

    void isolateBand(MBFImage image, int[] bandsToEliminate) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.getBand(bandsToEliminate[0]).pixels[y][x] = 0;
                image.getBand(bandsToEliminate[1]).pixels[y][x] = 0;
            }
        }
    }

}
