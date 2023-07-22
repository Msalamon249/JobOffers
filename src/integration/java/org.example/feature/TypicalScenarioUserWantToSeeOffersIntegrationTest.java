package org.example.feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.example.BaseIntegrationTest;
import org.example.SampleJobOfferResponse;
import org.example.domain.loginandregister.dto.RegistrationResultDto;
import org.example.domain.offer.dto.OfferResponseDto;
import org.example.infrastructure.loginandregister.controller.dto.JwtResponseDto;
import org.example.infrastructure.offer.scheduler.HttpOffersScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TypicalScenarioUserWantToSeeOffersIntegrationTest extends BaseIntegrationTest implements SampleJobOfferResponse {

    @Autowired
    HttpOffersScheduler httpOffersScheduler;

    @Test
    public void user_want_to_see_offers_but_have_to_be_logged_in_and_external_server_should_have_some_offers() throws Exception {
        // step 1: there are no offers in external HTTP server
        // given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithZeroOffersJson())));


        // step 2: scheduler ran 1st time and made GET to external server and system added 0 offers to database
        // given && when
        List<OfferResponseDto> newOffers = httpOffersScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(newOffers).isEmpty();


        //step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        ResultActions perform3 = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username" : "someUser",
                        "password" : "password"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        perform3.andExpect(status().isUnauthorized())
                .andExpect(content().json("""
                        {
                        "message" : "Bad Credentials",
                        "status" : "UNAUTHORIZED"
                        }
                        """.trim()));
        //step 4: user made GET /offers with no jwt token and system returned UNAUTHORIZED(401)
        ResultActions perform4 = mockMvc.perform(get("/offers")
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        perform4.andExpect(status().isForbidden());
        //step 5: user made POST /register with username=someUser, password=somePassword and system registered user with created(201)

        ResultActions perform5 = mockMvc.perform(post("/register")
                .content("""
                        {
                        "username" : "someUser",
                        "password" : "password"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        MvcResult result = perform5.andExpect(status().isCreated()).andReturn();

        String contentAsString5 = result.getResponse().getContentAsString();
        RegistrationResultDto registrationResultDto = objectMapper.readValue(contentAsString5, RegistrationResultDto.class);

        assertAll(
                () -> assertThat(registrationResultDto.username()).isEqualTo("someUser"),
                () -> assertTrue(registrationResultDto.created())
        );


        //step 6: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC

        ResultActions perfom6 = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username" : "someUser",
                        "password" : "password"
                        }
                        """.trim()
                )
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        MvcResult mvcResult6 = perfom6.andExpect(status().isOk()).andReturn();
        String contentAsString6 = mvcResult6.getResponse().getContentAsString();
        JwtResponseDto jwtResponseDto = objectMapper.readValue(contentAsString6, JwtResponseDto.class);
        String token = jwtResponseDto.token();


        //step 7: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 0 offers
        // given

        // when
        ResultActions perform7 = mockMvc.perform(get("/offers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        MvcResult mvcResult2 = perform7.andExpect(status().isOk()).andReturn();
        String jsonWithOffers = mvcResult2.getResponse().getContentAsString();
        List<OfferResponseDto> offers = objectMapper.readValue(jsonWithOffers, new TypeReference<>() {
        });
        assertThat(offers).isEmpty();


        //step 8: there are 2 new offers in external HTTP server

        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithTwoOffersJson())));

        //step 9: scheduler ran 2nd time and made GET to external server and system added 2 new offers with ids: 1000 and 2000 to database

        // given && when
        List<OfferResponseDto> twoNewOffers = httpOffersScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(twoNewOffers).hasSize(2);
        //step 10: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 2 offers with ids: 1000 and 2000

        MvcResult mvcResult10 = mockMvc.perform(get("/offers")
                        .header("Authorization", "Baerer " + token))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString10 = mvcResult10.getResponse().getContentAsString();
        List<OfferResponseDto> listWithTwoOffers = objectMapper.readValue(contentAsString10, new TypeReference<>() {
        });

        assertThat(listWithTwoOffers).hasSize(2);
        OfferResponseDto expectedFirstOffer = listWithTwoOffers.get(0);
        OfferResponseDto expectedSecondOffer = listWithTwoOffers.get(1);


        assertThat(listWithTwoOffers).containsExactlyInAnyOrder(
                new OfferResponseDto(expectedFirstOffer.id(), expectedFirstOffer.companyName(), expectedFirstOffer.position(), expectedFirstOffer.salary(), expectedFirstOffer.offerUrl()),
                new OfferResponseDto(expectedSecondOffer.id(), expectedSecondOffer.companyName(), expectedSecondOffer.position(), expectedSecondOffer.salary(), expectedSecondOffer.offerUrl())
        );

        //step 11: user made GET /offers/9999 and system returned NOT_FOUND(404) with message “Offer with id 9999 not found”


        ResultActions perform1 = mockMvc.perform(get("/offers/9999")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        perform1.andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                        "message": "Offer with id 9999 not found",
                        "status": "NOT_FOUND"
                        }
                        """.trim()));


        //step 12: user made GET /offers/1000 and system returned OK(200) with offer
        String idFromExpected = expectedFirstOffer.id();
        String contentAsString2 = mockMvc.perform(get("/offers/" + idFromExpected)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OfferResponseDto offerResponseDto1 = objectMapper.readValue(contentAsString2, OfferResponseDto.class);

        assertThat(offerResponseDto1).isEqualTo(expectedFirstOffer);


        //step 13: there are 2 new offers in external HTTP server

        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithFourOffersJson())));
        //step 14: scheduler ran 3rd time and made GET to external server and system added 2 new offers with ids: 3000 and 4000 to database

        // given && when
        List<OfferResponseDto> twoNewOffers2 = httpOffersScheduler.fetchAllOffersAndSaveAllIfNotExists();
        // then
        assertThat(twoNewOffers2).hasSize(2);

        //step 15: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 4 offers with ids: 1000,2000, 3000 and 4000

        String contentAsString4 = mockMvc.perform(get("/offers")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> listOfFourOffers = objectMapper.readValue(contentAsString4, new TypeReference<>() {
        });
        assertThat(listOfFourOffers).hasSize(4);
        OfferResponseDto expectedFirstOfferFromFour = listOfFourOffers.get(0);
        OfferResponseDto expectedSecondOfferFromFour = listOfFourOffers.get(1);
        OfferResponseDto expectedThirdOfferFromFour = listOfFourOffers.get(2);
        OfferResponseDto expectedFourOfferFromFour = listOfFourOffers.get(3);

        assertThat(listOfFourOffers).containsExactlyInAnyOrder(
                new OfferResponseDto(expectedFirstOfferFromFour.id(), expectedFirstOfferFromFour.companyName(), expectedFirstOfferFromFour.position(), expectedFirstOfferFromFour.salary(), expectedFirstOfferFromFour.offerUrl()),
                new OfferResponseDto(expectedSecondOfferFromFour.id(), expectedSecondOfferFromFour.companyName(), expectedSecondOfferFromFour.position(), expectedSecondOfferFromFour.salary(), expectedSecondOfferFromFour.offerUrl()),
                new OfferResponseDto(expectedThirdOfferFromFour.id(), expectedThirdOfferFromFour.companyName(), expectedThirdOfferFromFour.position(), expectedThirdOfferFromFour.salary(), expectedThirdOfferFromFour.offerUrl()),
                new OfferResponseDto(expectedFourOfferFromFour.id(), expectedFourOfferFromFour.companyName(), expectedFourOfferFromFour.position(), expectedFourOfferFromFour.salary(), expectedFourOfferFromFour.offerUrl())
        );


        //step 16: user made POST /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and offer as body and system returned CREATED(201) with saved offer

        ResultActions perform2 = mockMvc.perform(post("/offers")
                .header("Authorization", "Bearer " + token)
                .content(
                """
                        {    
                        "companyName": "test",
                        "position": "test",
                        "salary": "test",
                        "offerUrl": "test"
                        }
                        """.trim()
        ).contentType(MediaType.APPLICATION_JSON_VALUE));


        MvcResult mvcResult = perform2.andExpect(status().isCreated()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        OfferResponseDto parsedCreatedOfferJson = objectMapper.readValue(contentAsString, OfferResponseDto.class);
        String id = parsedCreatedOfferJson.id();


        assertAll(
                () -> assertThat(id).isNotNull(),
                () -> assertThat(parsedCreatedOfferJson.companyName()).isEqualTo("test"),
                () -> assertThat(parsedCreatedOfferJson.position()).isEqualTo("test"),
                () -> assertThat(parsedCreatedOfferJson.salary()).isEqualTo("test"),
                () -> assertThat(parsedCreatedOfferJson.offerUrl()).isEqualTo("test")

        );


        //step 17: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 5 offers

        ResultActions getAllFiveOffers = mockMvc.perform(get("/offers")
                .header("Authorization", "Bearer " + token));
        String contentAsString1 = getAllFiveOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> offerResponseDtoList = objectMapper.readValue(contentAsString1, new TypeReference<>() {
        });


        assertAll(
                () -> assertThat(offerResponseDtoList.size() == 5),
                () -> assertThat(offerResponseDtoList.stream().map(OfferResponseDto::id)).contains(id)
        );


    }
}

