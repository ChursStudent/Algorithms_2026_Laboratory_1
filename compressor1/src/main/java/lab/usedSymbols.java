package lab;

import java.util.ArrayList;
import java.util.List;

public class usedSymbols {
    public static List<Character> getSymList (String Text) {
        byte[] Alphabet = new byte[256];
        for (int i = 0; i < Text.length(); i++) {
            Alphabet[Text.charAt(i)] = 1;
        }
        List<Character> Output = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            if (Alphabet[i] == 1) {
                Output.add((char)i);
            }
        }
        return Output;
    }
    public static void main (String[] Args) {
        String S = "ldkgkjjksghrirgbb559ij3&&?qÿ";
        List<Character> used = getSymList(S);
        for (Character c : used) {
            System.out.println(c);
        }
    }
}
