package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.OSUtils.isWindows;

public class ArgQuoter {

    public static String quote(String s) {
        if (!isWindows() || !needQuoting(s)) {
            return s;
        }

        s = s.replaceAll("([\\\\]*)\"", "$1$1\\\\\"");
        s = s.replaceAll("([\\\\]*)\\z", "$1$1");
        return "\"" + s + "\"";
    }

    private static boolean needQuoting(final String s) {
        if (s == null) {
            return false;
        }

        final int len = s.length();

        if (len == 0) {
            // empty string have to be quoted
            return true;
        }

        for (int i = 0; i < len; i++) {
            switch (s.charAt(i)) {
                case ' ':
                case '\t':
                case '\\':
                case '"':
                    return true;
            }
        }

        return false;
    }
}
