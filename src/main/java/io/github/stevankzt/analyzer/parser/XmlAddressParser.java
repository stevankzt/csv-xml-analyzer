package io.github.stevankzt.analyzer.parser;

import io.github.stevankzt.analyzer.model.Address;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class XmlAddressParser implements AddressParser {

    private static final String ITEM_ELEMENT = "item";
    private static final String CITY_ATTRIBUTE = "city";
    private static final String STREET_ATTRIBUTE = "street";
    private static final String HOUSE_ATTRIBUTE = "house";
    private static final String FLOOR_ATTRIBUTE = "floor";
    private static final int BUFFER_SIZE = 1 << 16;

    private final XMLInputFactory factory;

    public XmlAddressParser() {
        factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    @Override
    public ParseSummary parse(Path file, Consumer<Address> consumer) throws AddressParseException {
        long accepted = 0;
        long rejected = 0;
        try (InputStream input = new BufferedInputStream(Files.newInputStream(file), BUFFER_SIZE)) {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                while (reader.hasNext()) {
                    if (reader.next() != XMLStreamConstants.START_ELEMENT
                            || !ITEM_ELEMENT.equals(reader.getLocalName())) {
                        continue;
                    }
                    Optional<Address> address = toAddress(reader);
                    if (address.isPresent()) {
                        consumer.accept(address.get());
                        accepted++;
                    } else {
                        rejected++;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new AddressParseException("Не удалось прочитать XML-файл: " + e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new AddressParseException("Некорректная структура XML: " + e.getMessage(), e);
        }
        return new ParseSummary(accepted, rejected);
    }

    private static Optional<Address> toAddress(XMLStreamReader reader) {
        return AddressMapper.map(
                reader.getAttributeValue(null, CITY_ATTRIBUTE),
                reader.getAttributeValue(null, STREET_ATTRIBUTE),
                reader.getAttributeValue(null, HOUSE_ATTRIBUTE),
                reader.getAttributeValue(null, FLOOR_ATTRIBUTE));
    }
}
