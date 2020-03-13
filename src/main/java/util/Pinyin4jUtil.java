package util;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Pinyin4jUtil {
    //中文字符格式
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";
    //拼音格式类
    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();
    static {
        //设置拼音小写
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //设置不带音调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //设置带v字符
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static String[] get(String hanyu) throws BadHanyuPinyinOutputFormatCombination {
        String[] array = new String[2];
        //全拼
        StringBuilder pinyin = new StringBuilder();
        //拼音首字母
        StringBuilder pinyin_first = new StringBuilder();
        for (int i = 0; i < hanyu.length(); i++) {
            try {
              String[] pinyins = PinyinHelper.
                      toHanyuPinyinStringArray(hanyu.charAt(i),FORMAT);
              //中文字符返回的字符串数组,可能为null或者长度为0
              //返回原始的字符
              if(pinyins == null || pinyins.length==0){
                  pinyin.append(hanyu.charAt(i));
                  pinyin_first.append(hanyu.charAt(i));
              }else{
                  //可以转换为拼音,只取第一个字符串为拼音
                  pinyin.append(pinyins[0]);
                  pinyin_first.append(pinyins[0].charAt(0));
              }
            } catch (Exception e){
                //出现异常,返回原始字符
                pinyin.append(hanyu.charAt(i));
                pinyin_first.append(hanyu.charAt(i));
            }
        }
        array[0]=pinyin.toString();
        array[1]=pinyin_first.toString();
        return array;
    }
    public static String[][] get(String hanyu, boolean fullspell) {
        String[][] array=new String[hanyu.length()][];
        for (int i = 0; i < hanyu.length(); i++) {
            try {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(hanyu.charAt(i),FORMAT);
                //中文字符串返回的数组可能为null或拼音的长度为0
                if(pinyins == null || pinyins.length == 0){
                    array[i] = new String[]{String.valueOf(hanyu.charAt(i))};
                } else {//去重操作
                      array[i] = unique(pinyins, fullspell);
                }
            } catch (Exception e) {
                array[i] = new String[]{String.valueOf(hanyu.charAt(i))};
            }
        }
        return array;
    }

    //拼音去重操作
    //fullspell true为全拼 false取首字母
    private static String[] unique(String[] pinyins, boolean fullspell) {
        Set<String> set= new HashSet<>();
        for (int i = 0; i < pinyins.length; i++) {
            if (fullspell) {
                set.add(pinyins[i]);
            } else {
                set.add(String.valueOf(pinyins[i].charAt(0)));
            }
        }
        return  set.toArray(new String[set.size()]);
    }

    //判断字符是否包含中文
    public static boolean containChinese(String str){
        return str.matches(".*"+CHINESE_PATTERN+".*");
    }

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
//        for(String[] array : get("中华人民a共1和国", true)) {
//            System.out.println(Arrays.toString(array));
//        }
        System.out.println(containChinese("abc"));
        System.out.println(containChinese("a啊c"));

    }
}
