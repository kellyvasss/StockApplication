package user;

import java.util.ArrayList;

public class NumberValidator {

    // Kontrollerar om input är av numerisk value
    public static Boolean isNumeric(String id) {
        try {
            Integer a = Integer.valueOf(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // Kontrollerar om längden är 10
    public static Boolean isLenTen(String id) {
        if (id.length() == 10) {
            return true;
        } return false;
    }
    // returnerar true om det är ett giltligt personnummer och false om det inte stämmer
    public static boolean controllID(Integer[] id) {
        Integer controllNumber = id[9];
        Integer numbersToAdd = 0;
        Integer multiplier;
        ArrayList<Integer> controllNumbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            multiplier = (i % 2 == 0) ? 2 : 1;
            Integer multiplierWithID = id[i] * multiplier;

            if (multiplierWithID >= 10) {
                controllNumbers.add(multiplierWithID - 9);
            } else {
                controllNumbers.add(multiplierWithID);
            }
        }
        for (Integer digit : controllNumbers) {
            numbersToAdd += digit;
        }
        return (numbersToAdd + controllNumber) % 10 == 0;
    }
    // gör om input till en array Integer
    public static Integer[] toIntArray(String id) {
        Integer[] a = new Integer[10];
        for(int i = 0; i<id.length(); i++) {
            char c = id.charAt(i);
            if(Character.isDigit(c)) {
                int n = Character.getNumericValue(c);
                a[i] = n;
            }
        } return a;
    }

}
