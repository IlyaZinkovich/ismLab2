package by.bsu.zinkovich.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BitsUtil {

    private BitsUtil() {}

    public static int getBit(long number, int n) {
        return (int) ((number >> (n - 1)) & 1L);
    }

    public static List<List<Integer>> generateBinaryArraysOfLength(int length) {
        if(length == 1) {
            List<List<Integer>> container = new ArrayList<>();
            container.add(Arrays.asList(0));
            container.add(Arrays.asList(1));
            return container;
        } else {
            List<List<Integer>> container = generateBinaryArraysOfLength(length-1);
            List<List<Integer>> toReturn = new ArrayList<>();
            for(List<Integer> list : container) {
                List<Integer> withZero = new ArrayList<>(list);
                withZero.add(0);
                List<Integer> withOne = new ArrayList<>(list);
                withOne.add(1);
                toReturn.add(withZero);
                toReturn.add(withOne);
            }
            return toReturn;
        }
    }

    private static boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = (1 << i & number) != 0;
        }
        return ret;
    }

}
