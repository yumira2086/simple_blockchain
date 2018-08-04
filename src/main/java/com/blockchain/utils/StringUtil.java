package com.blockchain.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/7/17-下午12:18.
 * Description:字符串处理工具类
 */
public class StringUtil {

    /**
     * 判断是否相等
     *
     * @param actual
     * @param expected
     * @return
     */
    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }


    /**
     * 判断给定字符串是否空白串。
     * 空白串是指由空格、制表符、回车符、换行符组成的字符串
     * 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "[]".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 保留两位小数
     *
     * @return
     */
    public static String formatFloat(float f) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(f);//format 返回的是字符串
    }

    /**
     * 获取一个字符串中某个字符的所有位置
     *
     * @param oriStr
     * @param findStr
     * @return
     */
    public static List<Integer> getAllIndexOf(String oriStr, String findStr) {
        List<Integer> indexs = new ArrayList<>();
        int start = 0;
        if (!isEmpty(findStr) && oriStr.contains(findStr)) {
            int index = -1;
            while ((index = oriStr.indexOf(findStr, start)) != -1) {
                start = index + findStr.length();
                indexs.add(index);
            }
        }
        return indexs;
    }


    /**
     * 将String数据存为文件
     */
    public static File getFileFromBytes(String name, String path) {
        byte[] b = name.getBytes();
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(path);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }


    public static String zeros(int n) {
        return repeat('0', n);
    }

    public static String repeat(char value, int n) {
        return new String(new char[n]).replace("\0", String.valueOf(value));

    }




}
