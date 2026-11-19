package io.github.stevankzt.analyzer.console;

import io.github.stevankzt.analyzer.parser.AddressParseException;
import io.github.stevankzt.analyzer.service.AddressFileAnalyzer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

public final class ConsoleApplication {

    private static final Set<String> EXIT_COMMANDS = Set.of("exit", "quit", "q", "выход");
    private static final String PROMPT = "> ";
    private static final char QUOTE = '"';
    private static final char APOSTROPHE = '\'';

    private final AddressFileAnalyzer analyzer;
    private final ReportPrinter printer;
    private final BufferedReader input;
    private final PrintStream out;

    public ConsoleApplication(AddressFileAnalyzer analyzer, ReportPrinter printer,
                              BufferedReader input, PrintStream out) {
        this.analyzer = analyzer;
        this.printer = printer;
        this.input = input;
        this.out = out;
    }

    public void run() {
        printGreeting();
        while (true) {
            out.println();
            out.println("Введите путь до файла-справочника (.csv или .xml) или команду завершения:");
            out.print(PROMPT);
            out.flush();
            String line = readLine();
            if (line == null) {
                out.println();
                break;
            }
            String command = line.strip();
            if (command.isEmpty()) {
                continue;
            }
            if (EXIT_COMMANDS.contains(command.toLowerCase(Locale.ROOT))) {
                break;
            }
            processFile(command);
        }
        out.println("Работа завершена.");
    }

    private void printGreeting() {
        out.println("Анализатор справочников адресов");
        out.println("Для завершения введите exit");
    }

    private String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            out.println("Ошибка чтения ввода: " + e.getMessage());
            return null;
        }
    }

    private void processFile(String rawPath) {
        try {
            Path file = Path.of(unquote(rawPath));
            printer.print(analyzer.analyze(file));
        } catch (InvalidPathException e) {
            printError("Некорректный путь: " + e.getMessage());
        } catch (AddressParseException e) {
            printError(e.getMessage());
        } catch (OutOfMemoryError e) {
            printError("Недостаточно памяти для обработки файла");
        } catch (RuntimeException e) {
            printError("Непредвиденная ошибка при обработке файла: " + e);
        }
    }

    private void printError(String message) {
        out.println("Ошибка: " + message);
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if (first == last && (first == QUOTE || first == APOSTROPHE)) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
