// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.List;

/**
 *
 * @author Zqq
 */
public class StringUtil {

    public static String concat(String s1, String s2, String s3) {
        return s1.concat(s2).concat(s3);
    }

    public static String concat(String... strings) {
        int len = 0;
        int i;
        final int size;
        i = size = strings.length;
        while (i != 0) {
            --i;
            len += strings[i].length();
        }
        StringBuilder sb = new StringBuilder(len);
        for (i = 0; i < size; i++) {
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    public static String concat(String s1, String s2, String s3, String s4) {
        int len = s1.length() + s2.length() + s3.length() + s4.length();
        return new StringBuilder(len)
                .append(s1)
                .append(s2)
                .append(s3)
                .append(s4)
                .toString();
    }

    public static String concat(String string, int number) {
        return string.concat(Integer.toString(number));
    }

    public static String concatObjectClass(String string, Object object) {
        return string.concat(object != null ? object.getClass().getName() : "[null]");
    }

    public static String concat(String string, Object object) {
        return string.concat(object != null ? object.toString() : "null");
    }

    /**
     * Replaces all occurrences of a character in a string.
     *
     * @param s input string
     * @param sub character to replace
     * @param with character to replace with
     * @return String
     */
    public static String replaceChar(String s, char sub, char with) {
        int startIndex;
        if ((startIndex = s.indexOf(sub)) < 0) {
            return s;
        }
        char[] str = s.toCharArray();
        for (int i = startIndex; i < str.length; i++) {
            if (str[i] == sub) {
                str[i] = with;
            }
        }
        return new String(str);
    }

    /**
     * Tests if this string ends with the specified suffix.
     *
     * @param src String to test
     * @param subS suffix
     *
     * @return <code>true</code> if the character sequence represented by the
     * argument is a suffix of the character sequence represented by this
     * object; <code>false</code> otherwise.
     */
    public static boolean endsWithIgnoreCase(String src, String subS) {
        String sub = subS.toLowerCase();
        int sublen = sub.length();
        int j = 0;
        int i = src.length() - sublen;
        if (i < 0) {
            return false;
        }
        while (j < sublen) {
            char source = Character.toLowerCase(src.charAt(i));
            if (sub.charAt(j) != source) {
                return false;
            }
            j++;
            i++;
        }
        return true;
    }

    /**
     * Cuts prefix if exists.
     */
    public static String cutPrefix(String string, String prefix) {
        if (string.startsWith(prefix)) {
            string = string.substring(prefix.length());
        }
        return string;
    }

    public static String cutAndLowerFirst(String string, int from) {
        return String.valueOf(CharUtil.toLowerAscii(string.charAt(from))).concat(string.substring(from + 1));
    }

    /**
     * Joins list of iterable elements. Separator string may be
     * <code>null</code>.
     */
    public static String join(List elements, String separator) {
        if (elements == null) {
            return "";
        }
        int size = elements.size();
        if (size == 0) {
            return "";
        } else if (size == 1) {
            return String.valueOf(elements.get(0));
        }

        int len = separator.length() * (size - 1);
        String[] strings = new String[size];
        int i = 0;
        String str;
        for (Object element : elements) {
            str = String.valueOf(element);
            len += str.length();
            strings[i] = str;
            i++;
        }

        StringBuilder sb = new StringBuilder(len);
        boolean notfirst = false;
        for (int j = 0; j < size; j++) {
            if (notfirst) {
                sb.append(separator);
            } else {
                notfirst = true;
            }
            sb.append(strings[j]);
        }
        return sb.toString();
    }

    public static String prefixChar(String string, char prefix) {
        if (string != null && string.length() > 0) {
            return string.charAt(0) == prefix
                    ? string
                    : prefix + string;
        } else {
            return String.valueOf(prefix);
        }
    }

    public static void trimAll(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (string != null) {
                strings[i] = string.trim();
            }
        }
    }

    /**
     * Splits a string in several parts (tokens) that are separated by single
     * delimiter characters. Delimiter is always surrounded by two strings.
     *
     * @param src source to examine
     * @param delimiter delimiter character
     *
     * @return array of tokens
     */
    public static String[] splitc(String src, char delimiter) {
        if (src.length() == 0) {
            return new String[]{""};
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (srcc[0] == delimiter) {	// string starts with delimiter
            end[0] = 0;
            count++;
            s = CharUtil.findFirstDiff(srcc, 1, delimiter);
            if (s == -1) {							// nothing after delimiters
                return new String[]{"", ""};
            }
            start[1] = s;							// new start
        }
        while (true) {
            // find new end
            e = CharUtil.findFirstEqual(srcc, s, delimiter);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = CharUtil.findFirstDiff(srcc, e, delimiter);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    public static String[] splitc(String src, char[] delimiters) {
        if ((delimiters.length == 0) || (src.length() == 0)) {
            return new String[]{src};
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (CharUtil.equalsOne(srcc[0], delimiters)) {	// string starts with delimiter
            end[0] = 0;
            count++;
            s = CharUtil.findFirstDiff(srcc, 1, delimiters);
            if (s == -1) {							// nothing after delimiters
                return new String[]{"", ""};
            }
            start[1] = s;							// new start
        }
        while (true) {
            // find new end
            e = CharUtil.findFirstEqual(srcc, s, delimiters);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = CharUtil.findFirstDiff(srcc, e, delimiters);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    public static boolean isBlank(String string) {
        if (string == null) {
            return true;
        }
        for (int i = 0, size = string.length(); i < size; i++) {
            if (CharUtil.isWhitespace(string.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}
