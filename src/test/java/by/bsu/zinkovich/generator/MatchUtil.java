package by.bsu.zinkovich.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Aspire on 12.03.2016.
 */
public class MatchUtil {

    private MatchUtil() {}

    public static Map<String, Integer> createMatchMap(int[] array, int matchLength, int m) {
        Map<String, Integer> result = new HashMap<>();
        int length = matchLength != 1 ? array.length - matchLength + 1 : array.length - m + 1;
        for(int i = 0; i < length; i++) {
            String key = "";
            for(int j = 0; j < matchLength; j++) {
                key += array[i + j];
            }
            Integer value = result.get(key);
            result.put(key, value != null ? (value+1) : 1);
        }

        return result;
    }

    public static String createMatchString(List<Integer> list) {
        return list.stream().map(i -> i.toString()).collect(Collectors.joining());
    }
}
