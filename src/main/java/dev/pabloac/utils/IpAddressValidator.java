package dev.pabloac.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpAddressValidator {
    private static final String IPV4_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    /**
     * Matches a String  against a REGEX pattern.
     * @param stringAddress An IP address in String format
     * @return Returns true if the String is a valid IPV4 address
     */
    public static boolean isValidIPV4Address(String stringAddress){
        Pattern ipPattern = Pattern.compile(IPV4_REGEX);
        Matcher matcher = ipPattern.matcher(stringAddress);

        return matcher.matches();
    }
}
