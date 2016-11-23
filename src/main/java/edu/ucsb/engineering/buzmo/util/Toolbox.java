package edu.ucsb.engineering.buzmo.util;

public class Toolbox {
    public static String getQStr(int num) {
        String str = "";
        for (int i = 0; i < num; i++) {
            str += "?";
            if (i != num - 1) {
                str += ",";
            }
        }
        return str;
    }
}
