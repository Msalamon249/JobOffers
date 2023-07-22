package org.example.apivalidation;

import org.assertj.core.api.Assertions;
import org.example.BaseIntegrationTest;
import org.example.infrastructure.apivalidation.dto.ApiValidationErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WithMockUser
public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    @Test

    public void should_return_400_bad_request_and_validation_message_when_empty_and_null_in_offer_save_request() throws Exception {
        ResultActions performPostOffer = mockMvc.perform(post("/offers")
                .content(
                        """
                                {
                                "companyName" : "",
                                "position" : "",
                                "salary" : ""
                                }
                                """.trim()
                ).contentType(MediaType.APPLICATION_JSON_VALUE));

        String contentAsString = performPostOffer.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        ApiValidationErrorResponseDto apiValidationErrorResponse = objectMapper.readValue(contentAsString, ApiValidationErrorResponseDto.class);


        Assertions.assertThat(apiValidationErrorResponse.messages()).containsExactlyInAnyOrder(
                "companyName must not be empty",
                "position must not be empty",
                "salary must not be empty",
                "offerUrl must not be null",
                "offerUrl must not be empty"
        );
    }


    @Test
    public void should_return_400_bad_request_and_validation_message_when_password_is_blank() throws Exception {
        ResultActions performRegister = mockMvc.perform(post("/register").content(
                        """
                                {
                                "username" : "testUser",
                                "password" : ""
                                }
                                """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        MvcResult mvcResult = performRegister.andExpect(status().isBadRequest()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ApiValidationErrorResponseDto apiValidationErrorResponseDto = objectMapper.readValue(contentAsString, ApiValidationErrorResponseDto.class);

        Assertions.assertThat(apiValidationErrorResponseDto.messages()).containsExactlyInAnyOrder(
                "password must not be blank",
                "size must be between 2 and 20"
        );
    }
}
