package tech.Astolfo.AstolfoCaffeine.main.util.maths;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Maths {
    public static class Rounding {
        // Round a double as you normally would, add depending on the decimal being .5 or not
        public static double round(double value, int places) {
            return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
        }

        // Round a double up irregardless of the decimals in-front of the point being rounded from
        public static double ceil(double value, int places) {
            return BigDecimal.valueOf(value).setScale(places, RoundingMode.CEILING).doubleValue();
        }

        // Round a double down irregardless of the decimals in-front of the point being rounded from
        public static double floor(double value, int places) {
            return BigDecimal.valueOf(value).setScale(places, RoundingMode.FLOOR).doubleValue();
        }
    }

    public static class Validation {
        // Check if a string can be converted to a number without throwing NumberFormatException
        public static boolean isNumeric(String str) {
            return str.matches("-?\\d+(\\.\\d+)?");
        }
    }

    public static class Equation {
        // Astolfo's Theorem of Maximum Pagination
        public static int astolfoTheory(int page, int itemsPerPage, int slot) {
            return page * itemsPerPage - slot;
        }
    }
}

