package app.nehc.batterytool.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtil {
    public static StringBuilder exec(String cmd) {
        String tmp;
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));
            while ((tmp = br.readLine()) != null) {
                builder.append(tmp);
                builder.append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }
}
