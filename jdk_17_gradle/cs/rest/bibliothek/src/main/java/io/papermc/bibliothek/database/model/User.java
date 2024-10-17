package io.papermc.bibliothek.database.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public record User(

        @Id
        @NotBlank
        @Size(max = 30)
        String username,

        @NotBlank
        @Size(max = 30)
        String password
) {
}
