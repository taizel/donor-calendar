package org.donorcalendar.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.donorcalendar.model.BloodType;

import java.util.HashMap;

@Converter(autoApply = true)
public class BloodTypePersistenceConverter implements AttributeConverter<BloodType, String> {

    private static final HashMap<String, BloodType> valuesMap = new HashMap<>();

    static {
        BloodType[] values = BloodType.values();
        for (BloodType value : values) {
            valuesMap.put(value.getValue(), value);
        }
    }

    @Override
    public String convertToDatabaseColumn(BloodType bloodType) {
        return bloodType.getValue();
    }

    @Override
    public BloodType convertToEntityAttribute(String value) {
        return valuesMap.get(value);
    }
}
