package org.example.domain.offer;

import lombok.Builder;

@Builder
public record Offer(String id,
                    String companyName,
                    String position,
                    String salary,
                    String offerUrl

) {
}
