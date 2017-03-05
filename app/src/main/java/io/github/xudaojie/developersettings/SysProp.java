package io.github.xudaojie.developersettings;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xdj on 2017/3/3.
 */

public class SysProp {

    private static Method sysPropGet;
    private static Method sysPropSet;

    private SysProp() {
    }

    static {
        try {
            Class<?> S = Class.forName("android.os.SystemProperties");
            Method M[] = S.getMethods();
            for (Method m : M) {
                String n = m.getName();
                if (n.equals("get")) {
                    sysPropGet = m;
                } else if (n.equals("set")) {
                    sysPropSet = m;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String get(String name, String default_value) {
        try {
            return (String) sysPropGet.invoke(null, name, default_value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return default_value;
    }

    public static void set(String name, String value) {
        try {
            sysPropSet.invoke(null, name, value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void set(Context context, String key, String val) throws IllegalArgumentException {
        try {

            @SuppressWarnings("rawtypes")
            Class SystemProperties = Class.forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = String.class;

            Method set = SystemProperties.getMethod("set", paramTypes);

            //Parameters
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new String(val);

            set.invoke(SystemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
        }
    }
}