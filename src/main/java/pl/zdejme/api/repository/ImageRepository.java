package pl.zdejme.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zdejme.api.model.Image;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findFirstByOrderByAddedOn();
}
