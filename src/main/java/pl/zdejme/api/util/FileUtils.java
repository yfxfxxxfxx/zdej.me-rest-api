package pl.zdejme.api.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileUtils {

    public void deleteFromDirectory(String path) {
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
