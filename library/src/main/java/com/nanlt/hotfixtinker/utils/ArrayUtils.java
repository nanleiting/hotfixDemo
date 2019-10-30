package com.nanlt.hotfixtinker.utils;

import java.lang.reflect.Array;

public class ArrayUtils {
    /**
     * 合并数组
     * @param arrayLhs 前数组 （插队数组）
     * @param arrayRhs 后数组（已有的数组）
     * @return
     */
    public static Object combineArray(Object arrayLhs, Object arrayRhs) {
        // 获得 一个数组 的Class 对象 ，通过 Array.newInstance() 可以反射生成数组对象
        Class<?>localClass =arrayLhs.getClass().getComponentType();
        //前 数组 长度
        int i= Array.getLength(arrayLhs);
        //新 数组 总长度  = 前数组的长度+ 后数组的长度
        int j =i+Array.getLength(arrayRhs);
        //生成数组对象
        Object result =Array.newInstance(localClass,j);
        for (int k = 0; k <j ; k++) {
          if(k<i){
              Array.set(result,k,Array.get(arrayLhs,k));
          }else{
              Array.set(result,k,Array.get(arrayRhs,k-i));
          }
        }
        return  result;
    };
}
