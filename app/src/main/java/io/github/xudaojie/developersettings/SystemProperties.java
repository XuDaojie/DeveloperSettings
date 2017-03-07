package io.github.xudaojie.developersettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hacky way to call the hidden SystemProperties class API
 */
class SystemProperties {
    private static Method sSystemPropertiesGetMethod = null;
    private static Method sSystemPropertiesSetMethod = null;

    public static String get(final String name) {
        if (sSystemPropertiesGetMethod == null) {
            try {
                final Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
                if (systemPropertiesClass != null) {
                    sSystemPropertiesGetMethod =
                            systemPropertiesClass.getMethod("get", String.class);
                }
            } catch (final ClassNotFoundException e) {
                // Nothing to do
            } catch (final NoSuchMethodException e) {
                // Nothing to do
            }
        }
        if (sSystemPropertiesGetMethod != null) {
            try {
                return (String) sSystemPropertiesGetMethod.invoke(null, name);
            } catch (final IllegalArgumentException e) {
                // Nothing to do
            } catch (final IllegalAccessException e) {
                // Nothing to do
            } catch (final InvocationTargetException e) {
                // Nothing to do
            }
        }
        return null;
    }

    public static void set(final String key, final String value) {
        if (sSystemPropertiesSetMethod == null) {
            try {
                final Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
                if (systemPropertiesClass != null) {
                    sSystemPropertiesSetMethod =
                            systemPropertiesClass.getMethod("set", String.class, String.class);
                }
            } catch (final ClassNotFoundException e) {
                // Nothing to do
            } catch (final NoSuchMethodException e) {
                // Nothing to do
            }
        }
        if (sSystemPropertiesSetMethod != null) {
            try {
                sSystemPropertiesSetMethod.invoke(null, key, value);
            } catch (final IllegalArgumentException e) {
                // Nothing to do
            } catch (final IllegalAccessException e) {
                // Nothing to do
            } catch (final InvocationTargetException e) {
                // Nothing to do
            }
        }
    }

}
