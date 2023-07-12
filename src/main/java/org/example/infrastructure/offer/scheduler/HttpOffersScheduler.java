package org.example.infrastructure.offer.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.domain.offer.OfferFacade;
import org.example.domain.offer.dto.OfferResponseDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class HttpOffersScheduler {

    private final OfferFacade offerFacade;
    private static final String STARTED_OFFERS_FETCHING_MESSAGE = "Started offers fetching{}";
    private static final String STOPPED_OFFERS_FETCHING_MESSAGE = "Stopped offers fetching{}";
    private static final String ADDED_NEW_OFFERS_MESSAGE = "Added new {} offers";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Scheduled(fixedDelayString = "${http.offers.scheduler.request.delay}")
    public List<OfferResponseDto> fetchOffers() {
        log.info(STARTED_OFFERS_FETCHING_MESSAGE, dateFormat.format(new Date()));
        List<OfferResponseDto> offerResponseDtos = offerFacade.fetchAllOffersAndSaveAllIfNotExists();
        log.info(ADDED_NEW_OFFERS_MESSAGE, offerResponseDtos.size());
        log.info(STOPPED_OFFERS_FETCHING_MESSAGE, dateFormat.format(new Date()));
        return offerResponseDtos;
    }
}


