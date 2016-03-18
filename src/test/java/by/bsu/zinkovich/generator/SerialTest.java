package by.bsu.zinkovich.generator;

import by.bsu.zinkovich.generator.impl.LFSRGenerator;
import by.bsu.zinkovich.generator.impl.LinearCongruentialGenerator;
import by.bsu.zinkovich.generator.impl.MacLarenMarsagliaGenerator;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static by.bsu.zinkovich.generator.MatchUtil.createMatchMap;
import static by.bsu.zinkovich.generator.MatchUtil.createMatchString;
import static by.bsu.zinkovich.util.BitsUtil.generateBinaryArraysOfLength;
import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static java.util.Collections.indexOfSubList;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang3.ArrayUtils.*;
import static ucar.unidata.util.SpecialMathFunction.igamc;

public class SerialTest {

    @Test
    public void serialExponent() {
        serialTest(Exponent.INSTANCE.getBits(), 2);
    }

    @Test
    public void serialLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        serialTest(lfsrGenerator.nextBit(1000000), 2);
    }

    @Test
    public void serialFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    @Test
    public void serialSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    @Test
    public void serialFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    @Test
    public void serialSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        serialTest(new UniformIntegerDistribution(generator, 0, 1).sample(1000000), 2);
    }

    public void serialTest(int[] randomBits, int m) {
        int n = randomBits.length;

        int[] extended = addAll(randomBits, Arrays.copyOf(randomBits, m - 1));

        double first = 0;
        if(m > 0) {
            Map<String, Integer> matchMap = createMatchMap(extended, m, m);
            first = generateBinaryArraysOfLength(m).stream().map(i -> matchMap.get(createMatchString(i)))
                                                            .mapToDouble(i -> (double) i * (double) i)
                                                            .sum() * pow(2, m)/n - n;
        }

        double second = 0;
        if(m > 1) {
            Map<String, Integer> matchMap = createMatchMap(extended, m - 1, m);
            second = generateBinaryArraysOfLength(m-1).stream().map(i -> matchMap.get(createMatchString(i)))
                                                               .mapToDouble(i -> (double)i * (double)i)
                                                               .sum() * pow(2, m - 1)/n - n;
        }

        double third = 0;
        if(m > 2) {
            Map<String, Integer> matchMap = createMatchMap(extended, m - 2, m);
            third = generateBinaryArraysOfLength(m-2).stream().map(i -> matchMap.get(createMatchString(i)))
                                                              .mapToDouble(i -> (double)i * (double)i)
                                                              .sum() * pow(2, m - 2)/n - n;
        }

        System.out.println(first);
        System.out.println(second);
        System.out.println(third);

        double firstDelta = first - second;
        double secondDelta = first - 2*second + third;

        System.out.println(firstDelta);
        System.out.println(secondDelta);

        double p1 = igamc(pow(2, m-2), firstDelta);
        double p2 = igamc(pow(2, m-3), secondDelta);

        System.out.println(p1);
        System.out.println(p2);

        assertTrue(p1 >= 0.1);
        assertTrue(p2 >= 0.1);
    }

    private static int numberOfMatches(int[] array, List<Integer> toMatch) {
        int count = 0;
        int globalIndex = 0;
        while(globalIndex <= array.length) {
            int index = indexOfSubList(asList(toObject(array)).subList(globalIndex, array.length), toMatch);
            if(index == -1) {
                break;
            }
            globalIndex += (index+1);
            count++;
        }

        System.out.println(toMatch + " "  + count);

        return count;
    }
}
