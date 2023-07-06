package org.example.domain.offer;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.domain.offer.dto.JobOffersResponse;
import org.example.domain.offer.dto.OfferRequestDto;
import org.example.domain.offer.dto.OfferResponseDto;
import org.example.domain.offer.exceptions.OfferDuplicateException;
import org.example.domain.offer.exceptions.OfferNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


class OfferFacadeTest {

    @Test
    public void should_fetch_from_jobs_from_remote_and_save_all_offers_when_repository_is_empty() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        // when
        List<OfferResponseDto> result = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        // then
        assertThat(result).hasSize(6);
    }

    @Test
    public void should_save_only_2_offers_when_repository_had_4_added_with_offer_urls() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(
                List.of(
                        new JobOffersResponse("id", "id", "asds", "1"),
                        new JobOffersResponse("assd", "id", "asds", "2"),
                        new JobOffersResponse("asddd", "id", "asds", "3"),
                        new JobOffersResponse("asfd", "id", "asds", "4"),
                        new JobOffersResponse("Junior", "Comarch", "1000", "https://someurl.pl/5"),
                        new JobOffersResponse("Mid", "Finanteq", "2000", "https://someother.pl/6")
                )
        ).offerFacadeForTests();
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "1"));
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "2"));
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "3"));
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "4"));
        assertThat(offerFacade.findAllOffers()).hasSize(4);

        // when
        List<OfferResponseDto> response = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        // then
        assertThat(List.of(
                        response.get(0).offerUrl(),
                        response.get(1).offerUrl()
                )
        ).containsExactlyInAnyOrder("https://someurl.pl/5", "https://someother.pl/6");
    }

    @Test
    public void should_save_4_offers_when_there_are_no_offers_in_database() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();

        // when
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "1"));
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "2"));
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "3"));
        offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "4"));

        // then
        assertThat(offerFacade.findAllOffers()).hasSize(4);
    }

    @Test
    public void should_find_offer_by_id_when_offer_was_saved() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "1"));
        // when
        OfferResponseDto offerById = offerFacade.findById(offerResponseDto.id());

        // then
        assertThat(offerById).isEqualTo(OfferResponseDto.builder()
                .id(offerResponseDto.id())
                .companyName("id")
                .position("asds")
                .salary("asdasd")
                .offerUrl("1")
                .build()
        );
    }

    @Test
    public void should_throw_not_found_exception_when_offer_not_found() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        // when
        Throwable thrown = catchThrowable(() -> offerFacade.findById("100"));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(OfferNotFoundException.class)
                .hasMessage("Offer with id 100 not found");
    }

    @Test
    public void should_throw_duplicate_key_exception_when_with_offer_url_exists() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.save(new OfferRequestDto("id", "asds", "asdasd", "hello.pl"));
        String savedId = offerResponseDto.id();
        assertThat(offerFacade.findById(savedId).id()).isEqualTo(savedId);
        // when
        Throwable thrown = catchThrowable(() -> offerFacade.save(
                new OfferRequestDto("cx", "vc", "xcv", "hello.pl")));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(OfferDuplicateException.class)
                .hasMessage("Offer with offerUrl [hello.pl] already exists");
    }
}