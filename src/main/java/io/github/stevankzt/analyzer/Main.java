package io.github.stevankzt.analyzer;

import io.github.stevankzt.analyzer.console.ConsoleApplication;
import io.github.stevankzt.analyzer.console.ReportPrinter;
import io.github.stevankzt.analyzer.parser.CsvAddressParser;
import io.github.stevankzt.analyzer.parser.ParserRegistry;
import io.github.stevankzt.analyzer.parser.XmlAddressParser;
import io.github.stevankzt.analyzer.service.AddressFileAnalyzer;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        ParserRegistry parsers = new ParserRegistry()
                .register("csv", new CsvAddressParser())
                .register("xml", new XmlAddressParser());
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, inputCharset()));
        ConsoleApplication application = new ConsoleApplication(
                new AddressFileAnalyzer(parsers), new ReportPrinter(System.out), input, System.out);
        application.run();
    }

    private static Charset inputCharset() {
        Console console = System.console();
        return console != null ? console.charset() : Charset.defaultCharset();
    }
}
