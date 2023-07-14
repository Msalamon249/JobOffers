package org.example.domain.offer;

import lombok.AllArgsConstructor;
import org.example.domain.offer.exceptions.OfferDuplicateException;
import org.example.domain.offer.exceptions.OfferSavingException;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OfferService {

    private final OfferFetchable offerFetchable;
    private final OfferRepository offerRepository;

    List<Offer> fetchAllOffersAndSaveAllIfNotExists() {
        List<Offer> jobOffers = fetchOffers();
        final List<Offer> offers = filterNotExistingOffers(jobOffers);
        try {
            return offerRepository.saveAll(offers);
        } catch (OfferDuplicateException duplicateKeyException) {
            throw new OfferSavingException(duplicateKeyException.getMessage(), jobOffers);
        }
    }

    private List<Offer> filterNotExistingOffers(List<Offer> jobOffers) {
        return jobOffers
                .stream()
                .filter(offer -> !offer.offerUrl().isEmpty())
                .filter(offer -> !offerRepository.existsByOfferUrl(offer.offerUrl()))
                .collect(Collectors.toList());
    }

    private List<Offer> fetchOffers() {
        return offerFetchable.fetchOffers()
                .stream()
                .map(OfferMapper::mapFromJobOffersResponseToOffer)
                .collect(Collectors.toList());
    }

}
