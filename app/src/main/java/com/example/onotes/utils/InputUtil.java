package com.example.onotes.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by cwj Apr.06.2017 9:53 PM
 */

public class InputUtil {

    public static InputFilter filterspace() {
        // 不能输入空格
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
                if (TextUtils.equals(source, " ")) {
                    return "";
                }

                if (TextUtils.equals(source, "  ")) {
                    return "";
                }
                return null;
            }
        };
        return filter;
    }
}
