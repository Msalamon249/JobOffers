package org.example.infrastructure.offer.controller;

import lombok.AllArgsConstructor;
import org.example.domain.offer.OfferFacade;
import org.example.domain.offer.dto.OfferResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/offers")
@AllArgsConstructor
public class OfferRestController {

    private final OfferFacade offerFacade;

    @GetMapping
    public List<OfferResponseDto> getAllOffers() {
        return offerFacade.findAllOffers();
    }


    @GetMapping("/{id}")
    public OfferResponseDto findOfferById(@PathVariable String id) {
        return offerFacade.findOfferById(id);
    }


}
