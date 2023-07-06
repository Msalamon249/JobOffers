package org.example.domain.offer;

import org.example.domain.offer.dto.JobOffersResponse;

import java.util.List;

public interface OfferFetchable {
    List<JobOffersResponse> fetchOffers();
}
