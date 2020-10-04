package ng.min.gateway.utils;

import java.util.regex.Pattern;

/**
 * Created by Odinaka Onah on 20 Jul, 2020.
 */
public class Validation {

    public static String validateEmail(boolean b, boolean b2) {
        if (!b)
            return "Email is required, must be between 8 - 100 characters";
        if (!b2) return "Email provided is invalid ";
        return null;
    }

    public static String validateLocation(String address, String postalCode, String stateCode) {
        String validError = "";
        if (/*validData(address) &&*/ !validLength(address, 4, 100))
            validError = validError + "valid address is required 4 - 100 characters";
        if (/*validData(postalCode) && */!validLength(postalCode, 4, 8))
            validError = validError + "; valid postal code is required 4 - 8 characters";
        if (/*validData(stateCode) &&*/ !validLength(stateCode, 3))
            validError = validError + "; valid state code is required 3 characters";

        return validError;
    }

    public static String validateFile(String file, String type) {
        if (!validData(file)) {
            return "Please select another " + type;
        }
        return null;
    }

    public static boolean validData(Object obj) {
        if (obj == null || String.valueOf(obj).isEmpty()) {
            return false;
        }
        if (obj.equals("null")) {
            return false;
        }
        if (obj instanceof String) {
            String s = ((String) obj).trim();
            if (s.length() < 1) return false;
        }
        return !obj.equals(0);
    }

    public static boolean validName(String name) {

        if (name.length() < 2) {
            return false;
        }

        if (name.length() > 255) {
            return false;
        }

        //pure alphabets
        return Pattern.compile("[a-zA-Z]+").matcher(name).matches();
    }

    public static boolean validLength(String string, int min, int max) {

        if (!validData(string)) return false;

        if (string.length() < min) {
            return false;
        }

        if (max != 0) {
            return string.length() <= max;
        }

        return true;
    }

    public static boolean validEmail(String emailStr) {
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailStr).find();
    }

    public static String validPassword(String password) {

        /*(?=.*[*.!@$%^&(){}[]:;<>,?/~_+-=|])*/
//        if (!Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,32}$").matcher(password).find()) {
//            builder.append("one digit, one lowercase letter, one uppercase letter ");
//        }
        if (!Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$").matcher(password).find()) {
            return "You password must contain minimum of 8 character and at least one digit, one lowercase letter, one uppercase letter,one special character and must not contain space ";
        }
        return null;
    }

    public static boolean validNumberLength(String numbers, int min, int max) {

        //pure number
        if (validNumber(numbers)) {
            return false;
        }
        return validLength(numbers, min, max);
    }

    public static boolean validNumberLength(String numbers, int digits) {
        if (numbers == null)
            return false;

        //pure number
        if (validNumber(numbers)) {
            return false;
        }

        return validLength(numbers, digits);
    }

    public static boolean validNumber(String numbers) {
        if (!validData(numbers))
            return true;
        //pure number
        return !Pattern.compile("[0-9]+").matcher(numbers).matches();
    }

    public static boolean validLength(String string, int digits) {
        if (!validData(string)) return false;
        if (string.length() < digits) {
            return false;
        }

        if (digits != 0) {
            return string.length() <= digits;
        }
        return true;
    }

    public static void main(String[] args) {
        String email = "m@chucknorris.com";
        boolean validEmail = validEmail(email);
        System.out.println("Is a valid email " + validEmail);

        String pass = "QWEWW62";
        String validPassword = validPassword(pass);
        System.out.println(validPassword);
    }
}
