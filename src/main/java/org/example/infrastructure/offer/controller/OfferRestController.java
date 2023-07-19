package org.example.infrastructure.offer.controller;

import lombok.AllArgsConstructor;
import org.example.domain.offer.OfferFacade;
import org.example.domain.offer.dto.OfferRequestDto;
import org.example.domain.offer.dto.OfferResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/offers")
@AllArgsConstructor
public class OfferRestController {

    private final OfferFacade offerFacade;

    @GetMapping
    public ResponseEntity<List<OfferResponseDto>> getAllOffers() {
        List<OfferResponseDto> allOffers = offerFacade.findAllOffers();
        return ResponseEntity.ok(allOffers);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OfferResponseDto> findOfferById(@PathVariable String id) {
        OfferResponseDto offerById = offerFacade.findOfferById(id);
        return ResponseEntity.ok(offerById);
    }


    @PostMapping
    public ResponseEntity<OfferResponseDto> saveOffer(@RequestBody @Valid OfferRequestDto offerRequestDto) {
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(offerRequestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(offerResponseDto.id())
                .toUri();
        return ResponseEntity.created(uri).body(offerResponseDto);

    }
}
