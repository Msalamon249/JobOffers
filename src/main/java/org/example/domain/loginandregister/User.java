package org.example.domain.loginandregister;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document
public record User(
        @Id
        String id,
        String username,
        String password) {
}
