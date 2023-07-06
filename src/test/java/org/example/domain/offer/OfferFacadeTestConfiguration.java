package org.example.domain.offer;

import org.example.domain.offer.dto.JobOffersResponse;

import java.util.List;

public class OfferFacadeTestConfiguration {

    private final InMemoryFetcherTestImpl inMemoryFetcherTest;
    private final InMemoryOfferRepository offerRepository;

    OfferFacadeTestConfiguration() {
        this.inMemoryFetcherTest = new InMemoryFetcherTestImpl(
                List.of(
                        new JobOffersResponse("id", "id", "asds", "1"),
                        new JobOffersResponse("assd", "id", "asds", "2"),
                        new JobOffersResponse("asddd", "id", "asds", "3"),
                        new JobOffersResponse("asfd", "id", "asds", "4"),
                        new JobOffersResponse("agsd", "id", "asds", "5"),
                        new JobOffersResponse("adfvsd", "id", "asds", "6")
                )
        );
        this.offerRepository = new InMemoryOfferRepository();
    }

    OfferFacadeTestConfiguration(List<JobOffersResponse> remoteClientOffers) {
        this.inMemoryFetcherTest = new InMemoryFetcherTestImpl(remoteClientOffers);
        this.offerRepository = new InMemoryOfferRepository();
    }

    OfferFacade offerFacadeForTests() {
        return new OfferFacade(offerRepository, new OfferService(inMemoryFetcherTest, offerRepository));
    }
}
