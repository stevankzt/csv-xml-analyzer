package io.github.stevankzt.analyzer.parser;

import io.github.stevankzt.analyzer.model.Address;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public final class CsvAddressParser implements AddressParser {

    private static final char SEPARATOR = ';';
    private static final char QUOTE = '"';
    private static final char BOM = '﻿';
    private static final int FIELD_COUNT = 4;
    private static final int BUFFER_SIZE = 1 << 16;
    private static final String HEADER_FIRST_FIELD = "city";

    private final Charset charset;

    public CsvAddressParser() {
        this(StandardCharsets.UTF_8);
    }

    public CsvAddressParser(Charset charset) {
        this.charset = charset;
    }

    @Override
    public ParseSummary parse(Path file, Consumer<Address> consumer) throws AddressParseException {
        long accepted = 0;
        long rejected = 0;
        String[] fields = new String[FIELD_COUNT];
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file), charset), BUFFER_SIZE)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    line = stripBom(line);
                    if (isHeader(line, fields, buffer)) {
                        continue;
                    }
                }
                if (line.isBlank()) {
                    continue;
                }
                Optional<Address> address = toAddress(line, fields, buffer);
                if (address.isPresent()) {
                    consumer.accept(address.get());
                    accepted++;
                } else {
                    rejected++;
                }
            }
        } catch (IOException e) {
            throw new AddressParseException("Не удалось прочитать CSV-файл: " + e.getMessage(), e);
        }
        return new ParseSummary(accepted, rejected);
    }

    private static String stripBom(String line) {
        return !line.isEmpty() && line.charAt(0) == BOM ? line.substring(1) : line;
    }

    private static boolean isHeader(String line, String[] fields, StringBuilder buffer) {
        return split(line, fields, buffer) && HEADER_FIRST_FIELD.equalsIgnoreCase(fields[0].strip());
    }

    private static Optional<Address> toAddress(String line, String[] fields, StringBuilder buffer) {
        if (!split(line, fields, buffer)) {
            return Optional.empty();
        }
        return AddressMapper.map(fields[0], fields[1], fields[2], fields[3]);
    }

    private static boolean split(String line, String[] fields, StringBuilder buffer) {
        int length = line.length();
        int position = 0;
        int count = 0;
        while (true) {
            if (count == fields.length) {
                return false;
            }
            buffer.setLength(0);
            if (position < length && line.charAt(position) == QUOTE) {
                position = readQuoted(line, position + 1, buffer);
                if (position < 0 || position < length && line.charAt(position) != SEPARATOR) {
                    return false;
                }
            } else {
                while (position < length && line.charAt(position) != SEPARATOR) {
                    buffer.append(line.charAt(position++));
                }
            }
            fields[count++] = buffer.toString();
            if (position >= length) {
                return count == fields.length;
            }
            position++;
        }
    }

    private static int readQuoted(String line, int start, StringBuilder buffer) {
        int length = line.length();
        int position = start;
        while (position < length) {
            char current = line.charAt(position++);
            if (current != QUOTE) {
                buffer.append(current);
            } else if (position < length && line.charAt(position) == QUOTE) {
                buffer.append(QUOTE);
                position++;
            } else {
                return position;
            }
        }
        return -1;
    }
}
