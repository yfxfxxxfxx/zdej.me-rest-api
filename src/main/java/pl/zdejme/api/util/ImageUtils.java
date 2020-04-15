package pl.zdejme.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.zdejme.api.exception.UnsupportedConversionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class ImageUtils {

    public void applyContrast(String path, String format, float contrast) throws IOException {

        BufferedImage bi = ImageIO.read(new File(path));

        // brightness factor of 1f to preserve original brightness, contrast factor provided by end user
        RescaleOp rescaleOp = new RescaleOp(1f, contrast, null);
        rescaleOp.filter(bi, bi);

        try (OutputStream out = Files.newOutputStream(Path.of(path), StandardOpenOption.WRITE)) {
            ImageIO.write(bi, format, out);
            log.info("Contrast applied successfully, " + path);
        } catch (IOException ex) {
            log.info(ex.getMessage());
        }
    }
}
