package com.seekweb4.chat.api.utils;

import com.seekweb4.chat.common.utils.StringUtils;
import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author: fxq
 * @Date 2024-09-22 11:28
 */
public class PinyinUtils {
    /**
     * 中文首字符字母
     * @param chinese
     * @return
     */
    public static String getFirstLetter(String chinese) {
        if(StringUtils.isBlank(chinese)){
            return "";
        }
        StringBuilder pinyin = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char ch = chinese.charAt(i);
            if (Character.toString(ch).matches("[\\u4e00-\\u9fa5]+")) { // 判断是否为中文字符
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch);
                if (pinyinArray != null) {
                    pinyin.append(pinyinArray[0].charAt(0)); // 获取拼音的首字母
                }
            } else {
                pinyin.append(ch); // 非中文字符保持不变
            }
        }
        return pinyin.toString().substring(0,1).toUpperCase();
    }

//    public static void main(String[] args) {
//        String chinese = "中文首字母";
//        String firstLetters = getFirstLetter(chinese);
//        System.out.println(firstLetters); // 输出：zwsym
//    }
}
