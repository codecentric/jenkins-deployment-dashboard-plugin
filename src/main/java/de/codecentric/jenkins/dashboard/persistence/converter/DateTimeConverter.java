package de.codecentric.jenkins.dashboard.persistence.converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Andreas Houben
 */
public class DateTimeConverter implements Converter {

    private final static Logger LOGGER = Logger.getLogger(DateTimeConverter.class.getName());

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        DateTime dateTime = (DateTime) o;
        writer.setValue(dateTime.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        DateTime dateTime = DateTime.now();
        try {
            dateTime = DateTime.parse(reader.getValue());
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Could not parse DateTime value {0}. Returning DateTime.now().", reader.getValue());
        }
        return dateTime;
    }

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return DateTime.class.isAssignableFrom(clazz);
    }

}
