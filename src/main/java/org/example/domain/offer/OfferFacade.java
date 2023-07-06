package org.example.domain.offer;

import lombok.AllArgsConstructor;
import org.example.domain.offer.dto.OfferRequestDto;
import org.example.domain.offer.dto.OfferResponseDto;
import org.example.domain.offer.exceptions.OfferNotFoundException;

import java.util.List;


@AllArgsConstructor
public class OfferFacade {

    private final OfferRepository offerRepository;
    private final OfferService offerService;


    public List<OfferResponseDto> findAllOffers() {
        return offerRepository.findAll()
                .stream()
                .map(OfferMapper::mapFromOfferToOfferResponseDto)
                .toList();
    }

    public OfferResponseDto findById(String id) {
        return offerRepository.findById(id)
                .map(OfferMapper::mapFromOfferToOfferResponseDto)
                .orElseThrow(() -> new OfferNotFoundException(id));
    }

    public List<OfferResponseDto> fetchAllOffersAndSaveAllIfNotExists() {
        return offerService.fetchAllOffersAndSaveAllIfNotExists()
                .stream()
                .map(OfferMapper::mapFromOfferToOfferResponseDto)
                .toList();
    }

    public OfferResponseDto save(OfferRequestDto offerRequestDto) {
        Offer offer = OfferMapper.mapFromOfferRequestDtoToOffer(offerRequestDto);
        Offer created = offerRepository.save(offer);
        return OfferMapper.mapFromOfferToOfferResponseDto(created);
    }


}
