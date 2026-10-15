package lab;

import java.util.ArrayList;
import java.util.List;

public class LZ77 {
    private int offset;
    private int length;
    private char next;

    LZ77(int a, int b, char c) {
        this.offset = a;
        this.length = b;
        this.next = c;
    }

    public record Pair<O, L>(O offset, L length) {
    }

    static Pair<Integer,Integer> findMatching(String Buffer, int Position, String Text, int Max) {
        StringBuilder sub = new StringBuilder();
        int offset = 0;
        int length = 0;
        for (int i = Position; i < Text.length(); i++) {
            if (Buffer.contains(sub.toString() + Text.charAt(i))) {
                sub.append(Text.charAt(i));
                offset = Buffer.length() - Buffer.indexOf(sub.toString());
                length++;
            }
            else {
                break;
            }
        }
        if (offset == length && length != 0) {
            int length1 = length;
            while (Position + length + length1 < Text.length()) {
                if (Text.substring(Position + length1, Position + length + length1).equals(Text.substring(Position, Position + length))) {
                    length1 += length;
                }
                else {
                    break;
                }
            }
            length = length1;
        }
        return new Pair<>(offset, length);
    }

    static String lz77(String S, int Max) {
        List<LZ77> Answer = new ArrayList<>();
        int Position = 0;
        Pair<Integer,Integer> BufferOutput;
        int offset;
        int length;
        StringBuilder Buffer = new StringBuilder();
        while (Position < S.length()) {
            BufferOutput = findMatching(Buffer.toString(), Position, S, Max);
            offset = BufferOutput.offset;
            length = BufferOutput.length;
            for (int i = Position; i <= Position + length; i++) {
                Buffer.append(S.charAt(i));
                if (Buffer.length() > Max) {
                    Buffer.deleteCharAt(0);
                }
            }
            Position += length;
            Answer.add(new LZ77(offset,length,S.charAt(Position)));
            Position++;
        }
        StringBuilder Encoded = new StringBuilder();
        for (LZ77 Elem : Answer) {
            Encoded.append('('+(char)Elem.offset+','+(char)Elem.length+','+Elem.next+')');
        }
        return Encoded.toString();
    }

    static String ilz77(String S) {
        List<LZ77> list = new ArrayList<>();
        int offset;
        int length;
        char next;
        for (int i = 0; i < S.length(); i++) {
            if (S.charAt(i) == '(') {
                i++;
                offset = S.charAt(i);
                i += 2;
                length = S.charAt(i);
                i += 2;
                next = S.charAt(i);
                list.add(new LZ77 (offset, length, next));
            }
        }
        StringBuilder Decoded = new StringBuilder();
        int start;
        for (LZ77 Elem : list) {
            if (Elem.length > 0) {
                start = Decoded.length() - Elem.offset;
                for (int i = 0; i < Elem.length; i++) {
                    Decoded.append(Decoded.charAt(start + i));
                }
            }
            Decoded.append(Elem.next);
        }
        return Decoded.toString();
    }
}
