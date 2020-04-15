package pl.zdejme.api.util;

import lombok.extern.slf4j.Slf4j;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.springframework.stereotype.Component;
import pl.zdejme.api.converter.AsciiConverter;
import pl.zdejme.api.converter.GrayscaleConverter;
import pl.zdejme.api.converter.ImageConverter;
import pl.zdejme.api.converter.NegativeConverter;
import pl.zdejme.api.converter.isolator.BlueIsolator;
import pl.zdejme.api.converter.isolator.GreenIsolator;
import pl.zdejme.api.converter.isolator.RedIsolator;
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
import java.util.Map;

@Slf4j
@Component
public class ImageUtils {

    private static final Map<String, ImageConverter> imageProcessors = Map.of(
            "ascii", new AsciiConverter(),
            "gray", new GrayscaleConverter(),
            "neg", new NegativeConverter(),
            "rgb0", new BlueIsolator(),
            "rgb1", new GreenIsolator(),
            "rgb2", new RedIsolator()
    );
    private final FileUtils fileUtils;

    public ImageUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

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

    public void processImage(String path, String conversionType) throws IOException {
        String formattedConversionType = conversionType.toLowerCase();

        if (!imageProcessors.containsKey(formattedConversionType)) {
            throw new UnsupportedConversionException("Unsupported conversion type: " + conversionType);
        }

        File target = new File(path);
        MBFImage clone = ImageUtilities.readMBF(target);

        imageProcessors.get(formattedConversionType).processImage(clone);
        ImageUtilities.write(clone, fileUtils.getFileExtension(path), target);
    }
}
