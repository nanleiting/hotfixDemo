package com.nanlt.hotfixtinker;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.nanlt.hotfixtinker.utils.ArrayUtils;
import com.nanlt.hotfixtinker.utils.Constants;
import com.nanlt.hotfixtinker.utils.ReflectUtils;

import java.io.File;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 修复工具类
 */
public class FixDexUtils {
    //有可能有多个 修复包修复
    private static HashSet<File> loadedDex = new HashSet<>();

//    static {
//
//    }

    public static void loadFixedDex(Context context) {
        Log.i("nanhai","---2---->loadFixedDex");
        loadedDex.clear();
        Log.i("nanhai","---3---->loadedDex--hashCode-->"+loadedDex.getClass().hashCode());
        File fileDir = context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        File[] files = fileDir.listFiles();
        //找到 筛选 后的dex （修复包）加入 loadedDex 集合
        for (File file : files) {
            if(file.getName().endsWith(Constants.DEX_SUFFIX)&& !TextUtils.equals("classes.dex",file.getName())){
                loadedDex.add(file);
            }
        }
        //创建类加载器
        createDexClassLoader(context,fileDir);
    }

    private static void createDexClassLoader(Context context, File fileDir) {
        Log.i("nanhai","---4---->loadedDex--hashCode-->"+loadedDex.getClass().hashCode());
        //创建了 一个 odex 目录 将dex 解压 。。。。   class 文件
        String optimizedDir =fileDir.getAbsolutePath()+File.separator+"opt_dex";
        File fopt =new File(optimizedDir);
        if(!fopt.exists()){
            //创建一个多级目录
            fopt.mkdirs();
        }
        if(loadedDex.size()<=0){
            return;
        }
        for (File dex:loadedDex ) {
            // 每 发现一个修复 包 ，创建 一次 类加载器（自有）
            DexClassLoader classLoader =new DexClassLoader(dex.getAbsolutePath(),
                    optimizedDir,null, context.getClassLoader());
            //每循环一次，修复一次
            hotfix(classLoader,context);
        }

    }

    private static void hotfix(DexClassLoader classLoader, Context context) {
        //1获取系统的 类加载器
        Log.i("nanhai","---5---->context-->"+context.getClass().hashCode());
        Log.i("nanhai","---6---->classLoader-->"+classLoader.getClass().hashCode());
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            //2获取 自有的 dexElements数组
            Object myElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(classLoader));
            //3获取 系统的 dexElements数组
            Object systemElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(pathClassLoader));
            //4合并后，生成新的dexElements 数组
            Object dexElements = ArrayUtils.combineArray(myElements,systemElements);
            //5获取系统的 pathList 属性
            Object systemPathList = ReflectUtils.getPathList(pathClassLoader);
            //6将新的 dexElements 数组， 通过 反射的技术 给系统的 pathList 属性赋值
            ReflectUtils.setField(systemPathList,systemPathList.getClass(),dexElements);
            Log.i("nanhai","---7---->classLoader-->"+classLoader.getClass().hashCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
