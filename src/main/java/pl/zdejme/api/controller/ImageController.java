package pl.zdejme.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.zdejme.api.model.Image;
import pl.zdejme.api.request.ImageRequest;
import pl.zdejme.api.service.ImageService;
import pl.zdejme.api.util.FileUtils;
import pl.zdejme.api.util.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageController {

    public static final Map<String, String> conversionSubdirectories = Map.of(
            "ascii", "ascii",
            "gray", "grayscale",
            "neg", "negative",
            "rgb0", "rgb/blue",
            "rgb1", "rgb/green",
            "rgb2", "rgb/red"
    );
    private final ImageService imageService;
    private final FileUtils fileUtils;
    private final ImageUtils imageUtils;

    public ImageController(
            ImageService imageService,
            FileUtils fileUtils,
            ImageUtils imageUtils
    ) {
        this.imageService = imageService;
        this.fileUtils = fileUtils;
        this.imageUtils = imageUtils;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllImages() {
        List<String> imageLinks = new ArrayList<>();

        for (Image image : imageService.findAll()) {
            //TODO: update once server location is determined
            imageLinks.add("http://localhost/uploads/" + image.getFilename());
        }

        return ResponseEntity.ok(imageLinks);
    }

    @PostMapping
    public ResponseEntity<String> postImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("conversion-type") String conversionType,
            @RequestParam("contrast") float contrast
    ) throws IOException {
        if (imageService.countSavedImages() > 50) {
            imageService.deleteOldestImage();
        }
        imageService.deleteByName(file.getOriginalFilename());

        ImageRequest imageRequest = new ImageRequest();

        imageRequest.setFilename(file.getOriginalFilename());
        imageRequest.setFormat(fileUtils.getFileExtension(file.getOriginalFilename()));
        Image currentImage = imageService.saveImage(imageRequest);

        List<String> targetDirectories = fileUtils.assignDirectory(
                currentImage.getFilename(),
                file,
                conversionType
        );

        // first returned target directory is upload path for this image
        imageRequest.setFilepath(targetDirectories.get(0));

        imageService.updateImageFilepath(currentImage, imageRequest);

        //second target directory is for the image being operated on
        //applies indicated contrast before image manipulation
        String convertedLocation = targetDirectories.get(1);

        imageUtils.applyContrast(convertedLocation, imageRequest.getFormat(), contrast);

        imageUtils.processImage(convertedLocation, conversionType);

        //TODO: update once server location is determined
        return ResponseEntity.ok("http://localhost/" +
                conversionSubdirectories.get(conversionType.toLowerCase()) + "/" +
                currentImage.getFilename());
    }
}
