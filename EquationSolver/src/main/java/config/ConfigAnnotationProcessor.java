package config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class ConfigAnnotationProcessor {
    public final static Map<Class<?>, Class<?>> map = new HashMap<>();
    static {
        map.put(boolean.class, Boolean.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(char.class, Character.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
    }

    public static <T> T process(Class<T> clazz) {
        if (clazz.getAnnotation(Config.class) == null)
            throw new IllegalArgumentException("Class not marked as config");
        String configName = resolveConfigName(clazz);
        var prop = loadProperties(configName);

        T obj = buildEmptyConfig(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            boolean canAccess = field.canAccess(obj);
            if (!canAccess)
                field.setAccessible(true);
            Value v = field.getAnnotation(Value.class);
            String preInjection = prop.getProperty(v.name().isEmpty() ? field.getName().toLowerCase() : v.name());
            Class<?> fieldType = field.getType();
            Object injection = resolveInjection(fieldType, preInjection);
            try {
                Field.class.getMethod("set", Object.class, Object.class)
                        .invoke(field, obj, injection);
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse value", e);
            }
            field.setAccessible(canAccess);
        }
        return obj;
    }

    private static <T> T buildEmptyConfig(Class<T> clazz) {
        T obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create config instance of " + clazz.getSimpleName(), e);
        }
        return obj;
    }
    private static Object resolveInjection(Class<?> fieldType, String preInjection) {
        var type = fieldType.isPrimitive() ? map.get(fieldType) : fieldType;
        String methodName = "parse" + type.getSimpleName();
        try {
            var parse = type.getDeclaredMethod(methodName, String.class);
            return parse.invoke(null, preInjection);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find method with name " + methodName, e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Cannot invoke method with passed args " + methodName, e);
        }
    }
    private static <T> String resolveConfigName(Class<T> clazz) {
        String configName = clazz.getAnnotation(Config.class).value();
        if (configName.isEmpty()) {
            configName = clazz.getSimpleName();
        }
        return configName.toLowerCase(Locale.ROOT) + ".properties";
    }
    private static Properties loadProperties(String configName) {
        var prop = new Properties();
        var resource =
                Thread
                        .currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(configName);
        if (resource == null)
            throw new IllegalArgumentException("Could not find configuration file with name " + configName);
        try {
            prop.load(resource);
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties file with name " + configName, e);
        }
        return prop;
    }
}
