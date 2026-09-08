package it.unipi.LoveMining.model.mongo;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class ReviewDocument {

    @MongoId(FieldType.STRING)
    private String id;

    @Field("target_id")
    private String targetId;
    private Integer rating;
    private String comment;

    @Field("review_date")
    private LocalDateTime date;
}

