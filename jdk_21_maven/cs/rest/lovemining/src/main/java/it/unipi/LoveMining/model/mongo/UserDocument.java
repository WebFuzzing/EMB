package it.unipi.LoveMining.model.mongo;

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
@Document(collection = "users")
public class UserDocument {

    @MongoId(FieldType.STRING)
    private String id;

    @Field("Email")
    private String email;

    @Field("Password")
    private String password;

    @Field("is_admin")
    private Boolean isAdmin;

    private Integer age;
    private String status;
    private String sex;
    private String orientation;

    @Field("body_type")
    private String bodyType;

    private String diet;
    private String drinks;
    private String education;
    private String ethnicity;
    private Integer height;
    private Integer income;
    private String job;
    private String offspring;
    private String pets;
    private String religion;
    private String smokes;
    private String speaks;
    private String city;
    private String state;

    private String essay0;

    private java.util.List<String> interests;

    @Field("reviews_made")
    private java.util.List<ReviewSummary> reviewsMade;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummary {
        @Field("review_id")
        private String reviewId;

        @Field("target_id")
        private String targetId;

        private Integer rating;
    }
}
