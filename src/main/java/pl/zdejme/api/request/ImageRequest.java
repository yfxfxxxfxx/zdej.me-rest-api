package pl.zdejme.api.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ImageRequest {

    @NotEmpty
    private String filename;

    @NotEmpty
    private String format;

    @NotEmpty
    private String filepath;

}
