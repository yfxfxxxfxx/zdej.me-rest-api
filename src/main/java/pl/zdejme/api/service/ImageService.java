package pl.zdejme.api.service;

import org.springframework.stereotype.Service;
import pl.zdejme.api.exception.ImageNotPresentException;
import pl.zdejme.api.model.Image;
import pl.zdejme.api.repository.ImageRepository;
import pl.zdejme.api.request.ImageRequest;
import pl.zdejme.api.util.FileUtils;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final FileUtils fileUtils;

    public ImageService(ImageRepository imageRepository, FileUtils fileUtils) {
        this.imageRepository = imageRepository;
        this.fileUtils = fileUtils;
    }

    public Long countSavedImages() {
        return imageRepository.count();
    }

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public Image findById(Long id) {
        Optional<Image> imageToLocate = imageRepository.findById(id);

        if (imageToLocate.isEmpty()) {
            throw new ImageNotPresentException("Unable to locate image with ID provided.");
        }

        return imageToLocate.get();
    }

    public Image saveImage(ImageRequest imageRequest) {
        Image image = new Image();

        image.setPrefix(UUID.randomUUID());
        image.setFilename(image.getPrefix() + "_" + imageRequest.getFilename());
        image.setFormat(imageRequest.getFormat());
        image.setFilepath(imageRequest.getFilepath() + "\\" + image.getFilename());

        image.setAddedOn(OffsetDateTime.now());
        image = imageRepository.save(image);

        return image;
    }

    @Transactional
    public void updateImageFilepath(Image image, ImageRequest imageModificationRequest) {
        image.setFilepath(imageModificationRequest.getFilepath());
    }

    public void deleteByName(String filename) {
        String[] filenameElements;

        for (Image image : findAll()) {
            filenameElements = image.getFilename().split("_");
            if(filenameElements[1].toLowerCase().equals(filename.toLowerCase())) {
                deleteById(image.getId());
            }
        }
    }

    public void deleteById(Long id) {
        Image toDelete = findById(id);
        fileUtils.deleteFromDirectory(toDelete.getFilepath());
        imageRepository.delete(toDelete);
    }

    public void deleteOldestImage() {
        Optional<Image> oldestImage = imageRepository.findFirstByOrderByAddedOn();

        oldestImage.ifPresent(image -> fileUtils.deleteFromDirectory(image.getFilepath()));
        oldestImage.ifPresent(imageRepository::delete);
    }
}
