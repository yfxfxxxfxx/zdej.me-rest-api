package pl.zdejme.api.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "image")
@Data
@EqualsAndHashCode(callSuper = true)
public class Image extends BaseEntity {
    private UUID prefix;
    private String filename;
    private String format;
    private String filepath;

    @Column(name = "added_on", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime addedOn;
}
