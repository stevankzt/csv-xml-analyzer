package io.github.stevankzt.analyzer.model;

import java.util.Objects;

public record Address(String city, String street, String house, int floor) {

    public Address {
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(street, "street");
        Objects.requireNonNull(house, "house");
    }
}
