package com.zhuli.repair.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.zhuli.repair.LogInfo;
import com.zhuli.repair.RepairUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 热修复
 */
public class FixDexUtil {

    static {
        System.loadLibrary("native-lib");
    }

    public static final String DEX_SUFFIX = ".dex";
    public static final String APK_SUFFIX = ".apk";
    public static final String JAR_SUFFIX = ".jar";
    public static final String ZIP_SUFFIX = ".zip";

    //解压路径文件名
    public static final String OPTIMIZE_DEX = "dex";
    public static final String OPTIMIZE_RES = "res";
    public static final String OPTIMIZE_APK = "apk";

    /**
     * 获取下载路径
     *
     * @param context
     * @param type
     * @return
     */
    public static String getDownLoadPath(Context context, int type) {
        //下载路径
        String dexPath;
        if (type == RepairUtil.UPDATE_TYPE_REPAIR) {
            dexPath = context.getExternalFilesDir(OPTIMIZE_DEX).getAbsolutePath();

        } else if (type == RepairUtil.UPDATE_TYPE_RES) {
            dexPath = context.getExternalFilesDir(OPTIMIZE_RES).getAbsolutePath();

        } else if (type == RepairUtil.UPDATE_TYPE_APK) {
            dexPath = context.getExternalFilesDir(OPTIMIZE_APK).getAbsolutePath();

        } else {
            dexPath = context.getExternalFilesDir("").getAbsolutePath();

        }

        return dexPath;
    }

    /**
     * @author bthvi
     * @time 2018/6/25 0025 15:51
     * @desc 验证是否需要热修复
     */
    public static HashSet<File> isGoingToFix(File fileDir) {
        // 遍历所有的修复dex , 因为可能是多个dex修复包
        File[] listFiles = fileDir.listFiles();
        if (listFiles != null) {
            LogInfo.e("目录下文件数量=" + listFiles.length);
            //需要修复的dex文件
            HashSet<File> loadedDex = new HashSet<>();
            for (File file : listFiles) {
                LogInfo.e("文件名称=" + file.getName());
                if (file.getName().startsWith("classes") && (file.getName().endsWith(DEX_SUFFIX)
                        || file.getName().endsWith(APK_SUFFIX)
                        || file.getName().endsWith(JAR_SUFFIX)
                        || file.getName().endsWith(ZIP_SUFFIX))) {
                    // 存入集合
                    loadedDex.add(file);
                }
            }
            return loadedDex;
        }
        return null;
    }

    /**
     * 将dex合并到原有的dex之前
     *
     * @param context
     * @param loadedDex
     */
    public static void doDexInject(Context context, HashSet<File> loadedDex) {
        if (loadedDex == null || loadedDex.size() <= 0)
            return;

        String optimizeDir = context.getFilesDir().getAbsolutePath() + File.separator + OPTIMIZE_DEX;
        LogInfo.e("补丁包解压路径=" + optimizeDir);
        // data/data/包名/files/optimize_dex（这个必须是自己程序下的目录）
        File fopt = new File(optimizeDir);
        boolean outPath = fopt.exists() ? fopt.delete() : fopt.mkdirs();
        try {
            // 1.加载应用程序dex的Loader
            PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();

            for (File dex : loadedDex) {
                // 2.加载指定的修复的dex文件的Loader
                DexClassLoader dexLoader = new DexClassLoader(
                        dex.getAbsolutePath(),// 修复好的dex（补丁）所在目录
                        fopt.getAbsolutePath(),// 存放dex的解压目录（用于jar、zip、apk格式的补丁）
                        null,// 加载dex时需要的库
                        pathLoader// 父类加载器
                );

                // 3.开始合并，合并的目标是Element[],重新赋值补丁包的值，依次反射即可
                // BaseDexClassLoader中有 变量: DexPathList pathList
                // DexPathList中有 变量 Element[] dexElements

                //3.1 准备好pathList的引用
                Object dexPathList = getPathList(dexLoader);
                Object pathPathList = getPathList(pathLoader);
                //3.2 从pathList中反射出element集合
                Object leftDexElements = getDexElements(dexPathList);
                Object rightDexElements = getDexElements(pathPathList);
                //3.3 合并两个dex数组
                Object dexElements = combineArray(leftDexElements, rightDexElements);
                // 重写给PathList里面的Element[] dexElements;赋值
                Object pathList = getPathList(pathLoader);
                // 一定要重新获取，不要用pathPathList，会报错
                setField(pathList, pathList.getClass(), "dexElements", dexElements);
            }
            LogInfo.e("修复完成，重启应用！");
            ((Activity) context).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射给对象中的属性重新赋值
     */
    private static void setField(Object obj, Class<?> cl, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cl.getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(obj, value);
    }

    /**
     * 反射得到对象中的属性值
     */
    private static Object getField(Object obj, Class<?> cl, String field) throws NoSuchFieldException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    /**
     * 反射得到类加载器中的pathList对象
     */
    private static Object getPathList(Object baseDexClassLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 反射得到pathList中的dexElements
     */
    private static Object getDexElements(Object pathList) throws NoSuchFieldException, IllegalAccessException {
        return getField(pathList, pathList.getClass(), "dexElements");
    }

    /**
     * 数组合并
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> clazz = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);// 得到左数组长度（补丁数组）
        int j = Array.getLength(arrayRhs);// 得到原dex数组长度
        int k = i + j;// 得到总数组长度（补丁数组+原dex数组）
        Object result = Array.newInstance(clazz, k);// 创建一个类型为clazz，长度为k的新数组
        System.arraycopy(arrayLhs, 0, result, 0, i);
        System.arraycopy(arrayRhs, 0, result, i, j);
        return result;
    }


}

