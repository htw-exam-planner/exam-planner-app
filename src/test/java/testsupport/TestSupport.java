package testsupport;

import java.lang.reflect.Field;

public class TestSupport {
    public static <C, V> void setStaticValue(Class<C> targetClass, String field, V value) throws NoSuchFieldException, IllegalAccessException {
        Field fieldReference = targetClass.getDeclaredField(field);
        fieldReference.setAccessible(true);
        fieldReference.set(null, value);
        fieldReference.setAccessible(false);
    }
}