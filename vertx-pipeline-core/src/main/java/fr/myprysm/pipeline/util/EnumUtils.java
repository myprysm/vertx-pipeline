package fr.myprysm.pipeline.util;

public interface EnumUtils {

    static <T extends Enum<T>> T fromString(String stringValue, Class<T> enumClass) {
        try {
            return Enum.valueOf(enumClass, stringValue);
        } catch (Exception exc) {
            return null;
        }
    }
}
