package org.example.domain.offer;

import org.example.domain.offer.dto.JobOffersResponse;
import org.example.domain.offer.dto.OfferRequestDto;
import org.example.domain.offer.dto.OfferResponseDto;

public class OfferMapper {

    public static OfferResponseDto mapFromOfferToOfferResponseDto(Offer offer) {
        return OfferResponseDto.builder()
                .id(offer.id())
                .position(offer.position())
                .companyName(offer.companyName())
                .salary(offer.salary())
                .offerUrl(offer.offerUrl())
                .build();
    }


    public static Offer mapFromOfferRequestDtoToOffer(OfferRequestDto offerRequestDto) {
        return Offer.builder()
                .companyName(offerRequestDto.companyName())
                .position(offerRequestDto.position())
                .salary(offerRequestDto.salary())
                .offerUrl(offerRequestDto.offerUrl())
                .build();
    }

    public static Offer mapFromJobOffersResponseToOffer(JobOffersResponse jobOffersResponse) {
        return Offer.builder()
                .companyName(jobOffersResponse.company())
                .position(jobOffersResponse.title())
                .offerUrl(jobOffersResponse.offerUrl())
                .salary(jobOffersResponse.salary())
                .build();
    }
}
