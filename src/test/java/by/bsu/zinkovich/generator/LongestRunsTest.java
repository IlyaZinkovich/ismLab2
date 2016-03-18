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


public class LongestRunsTest {

    @Test
    public void longestRunsExample() {
        int[] bits = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
                0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1,
                0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0};
        longestRuns(bits);
    }

    @Test
    public void longestRunsLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        longestRuns(lfsrGenerator.nextBit(10000));
    }

    @Test
    public void longestRunsFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        longestRuns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        longestRuns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        longestRuns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void longestRunsSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        longestRuns(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }


    private void longestRuns(int[] bin_data) {
        int k = 3, m = 8;
        double[] v_values = new double[128];
        double[] pik_values = new double[128];
        if (bin_data.length < 6272) {
            k = 3;
            m = 8;
            v_values = new double[]{1, 2, 3, 4};
            pik_values = new double[]{0.21484375, 0.3671875, 0.23046875, 0.1875};
        }
        else if (bin_data.length < 75000) {
            k = 5;
            m = 128;
            v_values = new double[]{4, 5, 6, 7, 8, 9};
            pik_values = new double[]{0.1174035788, 0.242955959, 0.249363483, 0.17517706, 0.102701071, 0.112398847};
        }
        else {
            k = 6;
            m = 10000;
            v_values = new double[]{10, 11, 12, 13, 14, 15, 16};
            pik_values = new double[]{0.0882, 0.2092, 0.2483, 0.1933, 0.1208, 0.0675, 0.0727};
        }

        double[] pik = new double[]{0.2148, 0.3672, 0.2305, 0.1875};
        int num_blocks = (int) Math.floor(bin_data.length / m);
        double[] frequencies = new double[k + 1];
        int block_start = 0;
        int block_end = m;
        for (int i = 0; i < num_blocks; i++) {
            int[] block_data = Arrays.copyOfRange(bin_data, block_start, block_end);
            int max_run_count = 0;
            int run_count = 0;
            for (int j = 0; j < m; j++) {
                if (block_data[j] == 1) {
                    run_count += 1;
                    max_run_count = Math.max(max_run_count, run_count);
                } else {
                    max_run_count = Math.max(max_run_count, run_count);
                    run_count = 0;
                }
            }
            max_run_count = Math.max(max_run_count, run_count);
            if (max_run_count < v_values[0]) {
                frequencies[0] += 1;
            }
            for (int j = 0; j < k; j++) {
                if (max_run_count == v_values[j]) {
                    frequencies[j] += 1;
                }
            }
            if (max_run_count > v_values[k - 1]) {
                frequencies[k] += 1;
            }
            block_start += m;
            block_end += m;
        }
        double chi_squared = 0;
        for (int i = 0; i < frequencies.length; i++) {
            chi_squared += (pow(frequencies[i] - (num_blocks * pik_values[i]), 2.0)) / (num_blocks * pik_values[i]);
        }

        double p = igamc(k / 2, chi_squared/2);

        System.out.println(p);

        assertTrue(p >= 0.01);
    }
}
