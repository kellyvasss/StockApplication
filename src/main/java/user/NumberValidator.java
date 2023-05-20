package user;

import java.util.ArrayList;

public class NumberValidator {

    // Kontrollerar om input är av numerisk value
    public static Boolean isNumeric(String id) {
        try {
            Long a = Long.valueOf(id);
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
        Integer controllNumber = id[9]; // sista siffran är ett kontrollnummer
        Integer numbersToAdd = 0;
        Integer multiplier;
        ArrayList<Integer> controllNumbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            multiplier = (i % 2 == 0) ? 2 : 1; // multiplier kmr först vara 2 sen 1 sen 2 osv
            Integer multiplierWithID = id[i] * multiplier;

            if (multiplierWithID >= 10) { // om aktuell siffra är mer än 10, ta bort 9 (ex 18 = 9, 12=3)
                controllNumbers.add(multiplierWithID - 9);
            } else {
                controllNumbers.add(multiplierWithID);
            }
        }
        for (Integer digit : controllNumbers) { // för varje int(0-9) i array lägg till i det slutliga numbersToAdd
            numbersToAdd += digit;
        }
        return (numbersToAdd + controllNumber) % 10 == 0; // om numbersToAdd + kontrollsiffran % 10 = giltligt personnr
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
