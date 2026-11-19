package io.github.stevankzt.analyzer.parser;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ParserRegistry {

    private final Map<String, AddressParser> parsersByExtension = new LinkedHashMap<>();

    public ParserRegistry register(String extension, AddressParser parser) {
        parsersByExtension.put(extension.toLowerCase(Locale.ROOT), parser);
        return this;
    }

    public Optional<AddressParser> find(Path file) {
        return extensionOf(file).map(parsersByExtension::get);
    }

    public Set<String> supportedExtensions() {
        return Collections.unmodifiableSet(parsersByExtension.keySet());
    }

    private static Optional<String> extensionOf(Path file) {
        Path fileName = file.getFileName();
        if (fileName == null) {
            return Optional.empty();
        }
        String name = fileName.toString();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return Optional.empty();
        }
        return Optional.of(name.substring(dot + 1).toLowerCase(Locale.ROOT));
    }
}
