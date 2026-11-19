package io.github.stevankzt.analyzer.statistics;

import io.github.stevankzt.analyzer.model.Address;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public final class StatisticsCollector implements Consumer<Address> {

    private static final int INITIAL_CAPACITY = 1 << 16;
    private static final int FLOOR_RANGE = CityFloorStatistics.MAX_FLOOR - CityFloorStatistics.MIN_FLOOR + 1;

    private final Map<Address, Integer> occurrences = new HashMap<>(INITIAL_CAPACITY);
    private final Map<String, int[]> floorsByCity = new HashMap<>();
    private long totalRecords;

    @Override
    public void accept(Address address) {
        totalRecords++;
        int count = occurrences.merge(address, 1, Integer::sum);
        if (count == 1) {
            registerBuilding(address);
        }
    }

    public AddressStatistics result() {
        return new AddressStatistics(totalRecords, occurrences.size(), collectDuplicates(), collectCities());
    }

    private void registerBuilding(Address address) {
        int[] buildings = floorsByCity.computeIfAbsent(address.city(), city -> new int[FLOOR_RANGE]);
        int floor = address.floor();
        if (floor >= CityFloorStatistics.MIN_FLOOR && floor <= CityFloorStatistics.MAX_FLOOR) {
            buildings[floor - CityFloorStatistics.MIN_FLOOR]++;
        }
    }

    private List<DuplicateRecord> collectDuplicates() {
        return occurrences.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> new DuplicateRecord(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(DuplicateRecord::occurrences).reversed()
                        .thenComparing(duplicate -> duplicate.address().city())
                        .thenComparing(duplicate -> duplicate.address().street())
                        .thenComparing(duplicate -> duplicate.address().house()))
                .toList();
    }

    private List<CityFloorStatistics> collectCities() {
        return floorsByCity.entrySet().stream()
                .map(entry -> new CityFloorStatistics(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CityFloorStatistics::city, russianCollator()))
                .toList();
    }

    private static Comparator<String> russianCollator() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("ru"));
        return collator::compare;
    }
}
