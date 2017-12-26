package com.ezly.ezly_android.Utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Johnnie on 4/09/15.
 */
public class TextUtils {
    public static boolean isValidEmailAddress(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String capitalize(String string){
        if(string == null){
            return null;
        }

        StringBuffer res = new StringBuffer();
        String result  = string.toLowerCase();
        String[] strArr = result.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            if(stringArray != null && stringArray.length > 0){
                stringArray[0] = Character.toUpperCase(stringArray[0]);
                str = new String(stringArray);

                res.append(str).append(" ");
            }
        }
        return res.toString().trim();
    }

    public static boolean isEmpty(String string){
        return android.text.TextUtils.isEmpty(string);
    }
}
