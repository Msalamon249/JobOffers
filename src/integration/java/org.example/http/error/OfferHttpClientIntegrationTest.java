package org.example.http.error;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.domain.offer.OfferFetchable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.ResponseStatusException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

@WithMockUser
public class OfferHttpClientIntegrationTest {


    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    OfferFetchable offerFetchable = new OfferHttpClientTestConfig().remoteOfferTestClient(wireMockServer.getPort(), 1000, 1000);


    @Test
    public void should_return_500_internal_error_when_fault_connection_reset_by_peer() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));


        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR")
        );
    }


    @Test
    public void should_return_500_internal_error_when_fault_empty_response() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFault(Fault.EMPTY_RESPONSE)));

        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR")
        );
    }

    @Test
    public void should_return_500_internal_error_when_fault_random_data_then_close() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR")
        );
    }

    @Test
    public void should_return_500_internal_error_when_fault_malformed_response_chunk() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR")
        );
    }


    @Test
    public void should_return_401_unauthorized_when_http_returning_status_unauthorized() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeader("Content-Type", "application/json")));


        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("401 UNAUTHORIZED")
        );
    }


    @Test
    public void should_return_400_bad_request_when_http_returning_status_bad_request() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", "application/json")));


        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("400 BAD_REQUEST")
        );
    }


    @Test
    public void should_return_404_not_found_when_http_returning_status_not_found() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")));


        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("404 NOT_FOUND")
        );
    }


    @Test
    public void should_return_500_internal_error_with_delay_5000_and_client_has_1000ms_read_timeout() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                                {
                                    "position": "Software Engineer - Mobile (m/f/d)",
                                    "companyName": "Cybersource",
                                    "salary": "4k - 8k PLN",
                                    "offerUrl": "https://nofluffjobs.com/pl/job/software-engineer-mobile-m-f-d-cybersource-poznan-entavdpn"
                                                }
                                                """.trim())
                        .withFixedDelay(5000)));


        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR")
        );
    }

    @Test
    public void should_return_204_internal_error_when_http_returning_status_no_content() {
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NO_CONTENT.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                                {
                                    "position": "Software Engineer - Mobile (m/f/d)",
                                    "companyName": "Cybersource",
                                    "salary": "4k - 8k PLN",
                                    "offerUrl": "https://nofluffjobs.com/pl/job/software-engineer-mobile-m-f-d-cybersource-poznan-entavdpn"
                                                }
                                                """.trim())));


        Throwable throwable = catchThrowable(() -> offerFetchable.fetchOffers());

        assertAll(
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getMessage()).isEqualTo("204 NO_CONTENT")
        );
    }


}
