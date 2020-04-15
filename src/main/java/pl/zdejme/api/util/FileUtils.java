package pl.zdejme.api.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.zdejme.api.exception.FileExtensionNotSupportedException;
import pl.zdejme.api.exception.UnsupportedConversionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FileUtils {

    public final Map<String, Path> downloadDirectories = Map.of(
            "upload", Path.of("src/main/resources/static/uploads"),
            "ascii", Path.of("src/main/resources/static/converted/ascii"),
            "gray", Path.of("src/main/resources/static/converted/grayscale"),
            "neg", Path.of("src/main/resources/static/converted/negative"),
            "rgb0", Path.of("src/main/resources/static/converted/rgb/blue"),
            "rgb1", Path.of("src/main/resources/static/converted/rgb/green"),
            "rgb2", Path.of("src/main/resources/static/converted/rgb/red")
    );

    public List<String> assignDirectory(String filename, MultipartFile file, String conversionType) throws IOException {
        String formattedConversionType = conversionType.toLowerCase();

        if (!downloadDirectories.containsKey(formattedConversionType) || formattedConversionType.equals("upload")) {
            throw new UnsupportedConversionException("Unsupported conversion type: " + conversionType);
        }

        deleteMatchingFiles(downloadDirectories.get("upload"), filename.split("_")[1]);
        Path uploadPath = downloadDirectories.get("upload")
                .resolve(
                        Objects.requireNonNull(
                                filename
                        )
                );
        Files.write(uploadPath, file.getBytes());

        deleteMatchingFiles(downloadDirectories.get(formattedConversionType), filename.split("_")[1]);
        Path convertedPath = downloadDirectories.get(formattedConversionType)
                .resolve(
                        Objects.requireNonNull(
                                filename
                        )
                );
        Files.write(convertedPath, file.getBytes());

        //converting from path to string in order to set filepath on object, and
        //write to converted path when applying contrast
        return List.of(String.valueOf(uploadPath), String.valueOf(convertedPath));
    }

    public String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(fn -> fn.contains("."))
                .map(fn -> fn.substring(filename.lastIndexOf(".") + 1))
                .orElseThrow(() -> new FileExtensionNotSupportedException(
                        "Invalid file format: " + filename
                ));
    }

    public void deleteMatchingFiles(Path path, String filename) throws IOException {
        List<Path> matchingFiles = Files.list(path)
                .filter(file -> String.valueOf(file).contains(filename))
                .collect(Collectors.toList());

        for (Path matchingFile : matchingFiles) {
            Files.delete(matchingFile);
        }
    }

    public void deleteFromDirectory(String path) {
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
