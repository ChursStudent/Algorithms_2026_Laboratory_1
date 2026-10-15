package lab;

import java.io.IOException;

import static lab.Entropy.prob;

public class ArithmeticEncoding {
    public static double ae(String S, double[] P, int Max) {
        int Len = S.length();
        double[] Interval = new double[2];
        double[] Cumulative = new double[Max + 1];
        for (int i = 0; i < Max; i++) {
            Cumulative[i + 1] += Cumulative[i] + P[i];
        }
        double[] IntervalPartition = new double[Max + 1];
        for (int i = 0; i < Max; i++) {
            IntervalPartition[i] = Cumulative[i];
        }
        for (int i = 0; i < Len; i++) {
            Interval[0] = IntervalPartition[S.charAt(i)];
            Interval[1] = IntervalPartition[S.charAt(i) + 1];
            for (int j = 0; j < Max; j++) {
                IntervalPartition[j] = (Interval[1] - Interval[0]) * Cumulative[j] + Interval[0];
            }
        }
        System.out.println("Границы интервала: " + Interval[0] + ", " + Interval[1]);
        return (Interval[0] + Interval[1]) / 2;
    }

    public static void main (String[] args) throws IOException {
        StringBuilder Text = new StringBuilder();
        Text.append("a".repeat(186)).append("b".repeat(10000));
        double TextDone = ae(Text.toString(), prob(Text.toString(), 256, 1), 256);
        System.out.println("Результат арифм. кодирования = " + TextDone);
    }
}
