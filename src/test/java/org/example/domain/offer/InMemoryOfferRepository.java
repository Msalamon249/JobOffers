package org.example.domain.offer;

import org.example.domain.offer.exceptions.OfferDuplicateException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryOfferRepository implements OfferRepository {

    Map<String, Offer> map = new ConcurrentHashMap<>();

    @Override
    public List<Offer> findAll() {
        return map.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Offer> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Offer save(Offer offer) {
        if (map.values().stream().anyMatch(offer1 -> offer1.offerUrl().equals(offer.offerUrl()))) {
            throw new OfferDuplicateException(offer.offerUrl());
        }
        String id = UUID.randomUUID().toString();
        Offer offer1 = new Offer(id, offer.companyName(), offer.position(), offer.salary(), offer.offerUrl());
        map.put(id, offer1);
        return offer1;
    }

    @Override
    public boolean existsByOfferUrl(String offerUrl) {
        long count = map.values()
                .stream()
                .filter(offer -> offer.offerUrl().equals(offerUrl))
                .count();
        return count == 1;
    }

    @Override
    public Optional<Offer> findByOfferUrl(String offerUrl) {
        return map.values().stream()
                .findFirst();
    }

    @Override
    public List<Offer> saveAll(List<Offer> offers) {
        return offers.stream()
                .map(this::save)
                .toList();
    }
}
