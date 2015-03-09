package com.chanyouji.android.map;


public class Utils {
    public static String colorToRgb(int color) {
        int red = ((color & 0x00FF0000) >> 16);
        int green = ((color & 0x0000FF00) >> 8);
        int blue = color & 0x000000FF;
        StringBuilder sb = new StringBuilder("#");
        String redString = Integer.toHexString(red);
        if("0".equals(redString)){
        	redString += "0";
        }
        sb.append(redString);
        String gString = Integer.toHexString(green);
        if("0".equals(gString)){
        	gString += "0";
        }
        sb.append(gString);
        String bString = Integer.toHexString(blue);
        if("0".equals(bString)){
        	bString += "0";
        }
        sb.append(bString);
        return sb.toString();
    }
}
