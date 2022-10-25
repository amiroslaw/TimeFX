package ovh.miroslaw.timefx;

import java.util.Arrays;

public class EnumHelper {

    private EnumHelper() {
    }

    public static <T extends Enum<T>> String[] getNames(Class<T> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }

    public static<T extends Enum<T>> String getLowerCase(Enum<T> enumType) {
        return enumType.name().toLowerCase();
    }
}
