package io.github.stevankzt.analyzer.service;

import io.github.stevankzt.analyzer.parser.ParseSummary;
import io.github.stevankzt.analyzer.statistics.AddressStatistics;
import java.nio.file.Path;
import java.time.Duration;

public record AnalysisReport(Path file, ParseSummary parseSummary, AddressStatistics statistics, Duration elapsed) {
}
