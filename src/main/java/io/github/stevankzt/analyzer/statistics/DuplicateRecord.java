package io.github.stevankzt.analyzer.statistics;

import io.github.stevankzt.analyzer.model.Address;

public record DuplicateRecord(Address address, int occurrences) {
}
