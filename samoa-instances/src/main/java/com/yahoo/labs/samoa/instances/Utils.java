package com.yahoo.labs.samoa.instances;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public class Utils {
    public static int maxIndex(double[] doubles) {

        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || (doubles[i] > maximum)) {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }

    public static String quote(String string) {
        boolean quote = false;

        // backquote the following characters
        if ((string.indexOf('\n') != -1) || (string.indexOf('\r') != -1) || (string.indexOf('\'') != -1) || (string.indexOf('"') != -1)
                || (string.indexOf('\\') != -1) || (string.indexOf('\t') != -1) || (string.indexOf('%') != -1) || (string.indexOf('\u001E') != -1)) {
            string = backQuoteChars(string);
            quote = true;
        }

        // Enclose the string in 's if the string contains a recently added
        // backquote or contains one of the following characters.
        if ((quote == true) || (string.indexOf('{') != -1) || (string.indexOf('}') != -1) || (string.indexOf(',') != -1) || (string.equals("?"))
                || (string.indexOf(' ') != -1) || (string.equals(""))) {
            string = ("'".concat(string)).concat("'");
        }

        return string;
    }

    public static String backQuoteChars(String string) {

        int index;
        StringBuffer newStringBuffer;

        // replace each of the following characters with the backquoted version
        char charsFind[] = { '\\', '\'', '\t', '\n', '\r', '"', '%', '\u001E' };
        String charsReplace[] = { "\\\\", "\\'", "\\t", "\\n", "\\r", "\\\"", "\\%", "\\u001E" };
        for (int i = 0; i < charsFind.length; i++) {
            if (string.indexOf(charsFind[i]) != -1) {
                newStringBuffer = new StringBuffer();
                while ((index = string.indexOf(charsFind[i])) != -1) {
                    if (index > 0) {
                        newStringBuffer.append(string.substring(0, index));
                    }
                    newStringBuffer.append(charsReplace[i]);
                    if ((index + 1) < string.length()) {
                        string = string.substring(index + 1);
                    } else {
                        string = "";
                    }
                }
                newStringBuffer.append(string);
                string = newStringBuffer.toString();
            }
        }

        return string;
    }
}
