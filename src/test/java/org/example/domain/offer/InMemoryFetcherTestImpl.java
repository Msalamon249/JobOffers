package org.example.domain.offer;

import java.util.List;
import org.example.domain.offer.dto.JobOffersResponse;

public class InMemoryFetcherTestImpl  implements OfferFetchable{
    List<JobOffersResponse> listOfOffers;

    InMemoryFetcherTestImpl(List<JobOffersResponse> listOfOffers) {
        this.listOfOffers = listOfOffers;
    }

    @Override
    public List<JobOffersResponse> fetchOffers() {
        return listOfOffers;
    }
}
