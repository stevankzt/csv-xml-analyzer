package io.github.stevankzt.analyzer.statistics;

import java.util.Arrays;

public final class CityFloorStatistics {

    public static final int MIN_FLOOR = 1;
    public static final int MAX_FLOOR = 5;

    private final String city;
    private final int[] buildingsByFloor;

    CityFloorStatistics(String city, int[] buildingsByFloor) {
        this.city = city;
        this.buildingsByFloor = Arrays.copyOf(buildingsByFloor, buildingsByFloor.length);
    }

    public String city() {
        return city;
    }

    public int buildings(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException("Этажность должна быть от " + MIN_FLOOR + " до " + MAX_FLOOR);
        }
        return buildingsByFloor[floor - MIN_FLOOR];
    }
}
