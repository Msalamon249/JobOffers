package org.example.controller.error;

import org.example.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
public class DuplicateUsernameIntegrationTest extends BaseIntegrationTest {


    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    public void should_throw_duplicateUsernameException() throws Exception {
        ResultActions perform = mockMvc.perform(post("/register")
                .content("""
                        {
                        "username" : "userTest",
                        "password" : "passwordTest"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        perform.andExpect(status().isCreated());



        ResultActions perform2 = mockMvc.perform(post("/register")
                .content("""
                        {
                        "username" : "userTest",
                        "password" : "passwordTest"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        perform2.andExpect(status().isConflict());
    }
}
