package io.github.stevankzt.analyzer.parser;

import io.github.stevankzt.analyzer.model.Address;
import java.util.Optional;

final class AddressMapper {

    private AddressMapper() {
    }

    static Optional<Address> map(String city, String street, String house, String floor) {
        if (isBlank(city) || street == null || isBlank(house) || isBlank(floor)) {
            return Optional.empty();
        }
        try {
            int floorNumber = Integer.parseInt(floor.strip());
            return Optional.of(new Address(city.strip(), street.strip(), house.strip(), floorNumber));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
