package by.bsu.zinkovich.generator;

import by.bsu.zinkovich.generator.impl.LFSRGenerator;
import by.bsu.zinkovich.generator.impl.LinearCongruentialGenerator;
import by.bsu.zinkovich.generator.impl.MacLarenMarsagliaGenerator;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Arrays;

import static java.lang.Math.pow;
import static junit.framework.Assert.assertTrue;
import static ucar.unidata.util.SpecialMathFunction.igamc;

/**
 * Created by Ilya_Zinkovich on 03/18/2016.
 */
public class NonOverlappingPatternsTest {

    @Test
    public void longestRunsLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        System.out.println("LFSR:");
        nonOverlappingPatterns(lfsrGenerator.nextBit(10000));
    }

    @Test
    public void longestRunsFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        System.out.println("FirstLinearCongruential:");
        nonOverlappingPatterns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        System.out.println("SecondLinearCongruential:");
        nonOverlappingPatterns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        System.out.println("FirstMaclarenMarsaglia:");
        nonOverlappingPatterns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        System.out.println("SecondMaclarenMarsaglia:");
        nonOverlappingPatterns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsE() {
        System.out.println("E:");
        nonOverlappingPatterns(Exponent.INSTANCE.getBits());
    }

    private void nonOverlappingPatterns(int[] bin_data) {
        int[] pattern = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1};
        int num_blocks = 100;
        int n = bin_data.length;
        int pattern_size = pattern.length;
        int block_size = (int) Math.floor(n / num_blocks);
        int[] pattern_counts = new int[num_blocks];

        for (int i = 0; i < num_blocks; i++) {
            int block_start = i * block_size;
            int block_end = block_start + block_size;
            int[] block_data = Arrays.copyOfRange(bin_data, block_start, block_end);
            int j = 0;
            while (j < block_size) {
                int[] sub_block = Arrays.copyOfRange(block_data, j, j + pattern_size);
                if (Arrays.equals(sub_block, pattern)) {
                    pattern_counts[i] += 1;
                    j += pattern_size;
                } else {
                    j += 1;
                }
            }
        }

        double mean = (block_size - pattern_size + 1) / pow(2, pattern_size);
        double var = block_size * ((1 / pow(2, pattern_size)) - (((2 * pattern_size) - 1) / (pow(2, pattern_size * 2))));
        double chi_squared = 0;
        for (int i = 0; i < num_blocks; i++) {
            chi_squared += pow(pattern_counts[i] - mean, 2.0) / var;
        }

        double p = igamc(num_blocks / 2, chi_squared / 2);

        System.out.println(p);

        assertTrue(p >= 0.01);
    }
}
