package com.nanlt.hotfixtinker.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
    /**
     * 通过反射获取某对象，并设置私有可访问
     *
     * @param obj
     * @param clazz
     * @param field
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static Object getField(Object obj, Class<?> clazz, String field)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field localField = clazz.getDeclaredField(field);//获取 当前类的所有 修饰符的方法
        localField.setAccessible(true);//设置 私有 方法 可以访问
        return localField.get(obj);
    }

    /**
     * 给某属性赋值，并设置私有可访问
     *
     * @param obj   该属性所属类的 对象
     * @param clazz 该属性所属类
     * @param value 值
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setField(Object obj, Class<?> clazz, Object value)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field localField = clazz.getDeclaredField("dexElements");
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    /**
     * 通过反射获取 BaseDexClassLoader 对象中的 PathList 对象
     *
     * @param baseDexClassLoader baseDexClassLoader 对象
     */
    public static Object getPathList(Object baseDexClassLoader)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 通过反射获取 BaseDexClassLoader 对象中的 PathList 对象 ,再获取 dexElements 对象
     *
     * @param paramObject DexPathList pathList; 对象
     */
    public static Object getDexElements(Object paramObject)
            throws  NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }


}
