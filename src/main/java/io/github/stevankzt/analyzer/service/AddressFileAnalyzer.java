package io.github.stevankzt.analyzer.service;

import io.github.stevankzt.analyzer.parser.AddressParseException;
import io.github.stevankzt.analyzer.parser.AddressParser;
import io.github.stevankzt.analyzer.parser.ParseSummary;
import io.github.stevankzt.analyzer.parser.ParserRegistry;
import io.github.stevankzt.analyzer.statistics.AddressStatistics;
import io.github.stevankzt.analyzer.statistics.StatisticsCollector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public final class AddressFileAnalyzer {

    private final ParserRegistry parsers;

    public AddressFileAnalyzer(ParserRegistry parsers) {
        this.parsers = parsers;
    }

    public AnalysisReport analyze(Path file) throws AddressParseException {
        if (!Files.isRegularFile(file)) {
            throw new AddressParseException("Файл не найден: " + file);
        }
        AddressParser parser = parsers.find(file)
                .orElseThrow(() -> new AddressParseException(
                        "Неподдерживаемый формат файла. Допустимые расширения: "
                                + String.join(", ", parsers.supportedExtensions())));
        long start = System.nanoTime();
        StatisticsCollector collector = new StatisticsCollector();
        ParseSummary summary = parser.parse(file, collector);
        AddressStatistics statistics = collector.result();
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
        return new AnalysisReport(file, summary, statistics, elapsed);
    }
}
