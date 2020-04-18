package pl.zdejme.api.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.zdejme.api.util.FileUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
public class ImageInitializer implements CommandLineRunner {

    private final FileUtils fileUtils;

    public ImageInitializer(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    @Override
    public void run(String... args) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.put("files", List.of(
                ImageInitializer.class.getResourceAsStream("/init/GettyImages-142116239_medium.jpg")
//                new ClassPathResource("init\\GettyImages-142116239_medium.jpg", this.getClass().getClassLoader())
//                new ClassPathResource("src\\main\\resources\\init\\orca.jpg"),
//                new ClassPathResource("src\\main\\resources\\init\\pacnw.jpg"),
//                new ClassPathResource("src\\main\\resources\\init\\platonov.png"),
//                new ClassPathResource("src\\main\\resources\\init\\svaneti-mountains.jpg")
                )
        );

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String serverUrl = "https://zdej-me.herokuapp.com/image/multiple-upload-init";
//        String serverUrl = "http://localhost/image/multiple-upload-init";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        log.info(Objects.requireNonNull(response).getBody());
    }

    @PreDestroy
    public void clearDirectories() {
        for (String dir : fileUtils.downloadDirectories.keySet()) {
            Stream.of(Objects.requireNonNull(
                    new File(String.valueOf(fileUtils.downloadDirectories.get(dir))).listFiles()))
                    .filter(file -> !file.isDirectory())
                    .map(File::getPath)
                    .forEach(fileUtils::deleteFromDirectory);
        }
    }
}
