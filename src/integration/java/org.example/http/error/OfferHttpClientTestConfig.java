package org.example.http.error;

import org.example.domain.offer.OfferFetchable;
import org.example.infrastructure.offer.http.OfferHttpClientConfig;

import org.springframework.web.client.RestTemplate;

import static org.example.BaseIntegrationTest.WIRE_MOCK_HOST;


public class OfferHttpClientTestConfig extends OfferHttpClientConfig {


    public OfferFetchable remoteOfferTestClient(int port, int connectionTimeout, int readTimeout) {
        RestTemplate restTemplate = restTemplate(connectionTimeout, readTimeout, restTemplateResponseErrorHandler());
        return remoteOfferClient(restTemplate,WIRE_MOCK_HOST,port);

    }
}
