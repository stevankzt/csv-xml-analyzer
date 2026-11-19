package io.github.stevankzt.analyzer.statistics;

import java.util.List;

public record AddressStatistics(
        long totalRecords,
        long uniqueRecords,
        List<DuplicateRecord> duplicates,
        List<CityFloorStatistics> cities) {

    public AddressStatistics {
        duplicates = List.copyOf(duplicates);
        cities = List.copyOf(cities);
    }
}
