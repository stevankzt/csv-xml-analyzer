package io.github.stevankzt.analyzer.console;

import io.github.stevankzt.analyzer.model.Address;
import io.github.stevankzt.analyzer.service.AnalysisReport;
import io.github.stevankzt.analyzer.statistics.AddressStatistics;
import io.github.stevankzt.analyzer.statistics.CityFloorStatistics;
import io.github.stevankzt.analyzer.statistics.DuplicateRecord;
import java.io.PrintStream;
import java.util.List;

public final class ReportPrinter {

    private static final String CITY_HEADER = "Город";
    private static final int FLOOR_COLUMN_WIDTH = 8;
    private static final double NANOS_IN_MILLI = 1_000_000.0;

    private final PrintStream out;

    public ReportPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(AnalysisReport report) {
        StringBuilder text = new StringBuilder();
        AddressStatistics statistics = report.statistics();
        appendSummary(text, report, statistics);
        appendDuplicates(text, statistics.duplicates());
        appendFloors(text, statistics.cities());
        text.append(String.format("%nВремя обработки файла: %.2f мс%n",
                report.elapsed().toNanos() / NANOS_IN_MILLI));
        out.print(text);
        out.flush();
    }

    private static void appendSummary(StringBuilder text, AnalysisReport report, AddressStatistics statistics) {
        text.append(String.format("%nФайл: %s%n", report.file().toAbsolutePath()));
        text.append(String.format("Всего записей: %d, уникальных: %d%n",
                statistics.totalRecords(), statistics.uniqueRecords()));
        long rejected = report.parseSummary().rejectedRecords();
        if (rejected > 0) {
            text.append(String.format("Пропущено некорректных записей: %d%n", rejected));
        }
    }

    private static void appendDuplicates(StringBuilder text, List<DuplicateRecord> duplicates) {
        text.append(String.format("%nДублирующиеся записи (%d):%n", duplicates.size()));
        if (duplicates.isEmpty()) {
            text.append(String.format("  Дубликатов нет%n"));
            return;
        }
        for (DuplicateRecord duplicate : duplicates) {
            Address address = duplicate.address();
            text.append(String.format("  %s, %s, д. %s, этажей: %d — повторений: %d%n",
                    address.city(), address.street(), address.house(), address.floor(),
                    duplicate.occurrences()));
        }
    }

    private static void appendFloors(StringBuilder text, List<CityFloorStatistics> cities) {
        text.append(String.format("%nКоличество зданий по этажности:%n"));
        if (cities.isEmpty()) {
            text.append(String.format("  Нет данных%n"));
            return;
        }
        int cityWidth = cityColumnWidth(cities);
        text.append(String.format("  %-" + cityWidth + "s", CITY_HEADER));
        for (int floor = CityFloorStatistics.MIN_FLOOR; floor <= CityFloorStatistics.MAX_FLOOR; floor++) {
            text.append(String.format("%" + FLOOR_COLUMN_WIDTH + "s", floor + " эт."));
        }
        text.append(System.lineSeparator());
        for (CityFloorStatistics city : cities) {
            text.append(String.format("  %-" + cityWidth + "s", city.city()));
            for (int floor = CityFloorStatistics.MIN_FLOOR; floor <= CityFloorStatistics.MAX_FLOOR; floor++) {
                text.append(String.format("%" + FLOOR_COLUMN_WIDTH + "d", city.buildings(floor)));
            }
            text.append(System.lineSeparator());
        }
    }

    private static int cityColumnWidth(List<CityFloorStatistics> cities) {
        int width = CITY_HEADER.length();
        for (CityFloorStatistics city : cities) {
            width = Math.max(width, city.city().length());
        }
        return width;
    }
}
