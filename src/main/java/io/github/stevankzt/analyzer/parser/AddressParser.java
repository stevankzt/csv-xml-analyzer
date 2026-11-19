package io.github.stevankzt.analyzer.parser;

import io.github.stevankzt.analyzer.model.Address;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface AddressParser {

    ParseSummary parse(Path file, Consumer<Address> consumer) throws AddressParseException;
}
