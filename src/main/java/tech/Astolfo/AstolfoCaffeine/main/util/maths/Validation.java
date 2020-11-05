package tech.Astolfo.AstolfoCaffeine.main.util.maths;

public class Validation {
    // Check if a string can be converted to a number without throwing NumberFormatException
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
