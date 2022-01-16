package com.zhuli.repair.utils;

import android.content.Context;

import com.zhuli.repair.LogInfo;
import com.zhuli.repair.Replace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;

import dalvik.system.DexFile;

/**
 * @author bthvi
 * @time 2019/7/20
 * @desc 不用启动APP实现热修复
 */
public class FixDexManager {

    private Context context;

    public FixDexManager(Context context) {
        this.context = context;
    }

    /**
     * 遍历所有的修复dex , 因为可能是多个dex修复包
     *
     * @param loadedDex
     */
    public void doDexInject(HashSet<File> loadedDex) {
        for (File file : loadedDex) {
            loadDex(file);
        }
    }


    /**
     * 加载Dex文件
     *
     * @param file
     */
    public void loadDex(File file) {
        try {
            LogInfo.e("TAG==开始修复=" + file.getAbsolutePath());
            //加载dex文件（资源路径，解压路径，标识符（私有的））
            DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), new File(context.getCacheDir(), "opt").getAbsolutePath(), Context.MODE_PRIVATE);
            //当前的dex里面的class 类名集合
            Enumeration<String> entry = dexFile.entries();
            //加载当前Enumeration的所有类
            while (entry.hasMoreElements()) {
                //拿到Class类名
                String clazzName = entry.nextElement();
                //通过加载得到类，这里不能通过反射，因为当前的dex没有加载到虚拟机内存中
                Class realClazz = dexFile.loadClass(clazzName, context.getClassLoader());
                if (realClazz != null) {
                    fixClazz(realClazz);
                }
            }
            LogInfo.e("TAG==修复完成=");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 修复有bug的方法
     *
     * @param realClazz
     */
    private void fixClazz(Class realClazz) {
        //得到类中所有方法
        Method[] methods = realClazz.getMethods();
        //遍历方法 通过注解 得到需要修复的方法
        for (Method rightMethod : methods) {
            //拿到注解
            Replace replace = rightMethod.getAnnotation(Replace.class);
            if (replace == null) {
                continue;
            }
            //得到类名
            String clazzName = replace.clazz();
            //得到方法名
            String methodName = replace.method();
            LogInfo.e("TAG==有bug的类=" + clazzName + "有bug的方法=" + methodName);
            try {
                //反射得到本地的有bug的方法的类
                Class wrongClazz = Class.forName(clazzName);
                //得到有bug的方法（注意修复包中的方法参数名和参数列表必须一致）
                Method wrongMethod = wrongClazz.getDeclaredMethod(methodName, rightMethod.getParameterTypes());
                //调用native方法替换有bug的方法
                replace(wrongMethod, rightMethod);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public native static void replace(Method wrongMethod, Method rightMethod);

}