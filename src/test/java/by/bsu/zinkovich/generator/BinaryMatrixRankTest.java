package by.bsu.zinkovich.generator;

import by.bsu.zinkovich.generator.impl.LFSRGenerator;
import by.bsu.zinkovich.generator.impl.LinearCongruentialGenerator;
import by.bsu.zinkovich.generator.impl.MacLarenMarsagliaGenerator;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.util.Arrays;

import static java.lang.Math.pow;
import static org.junit.Assert.assertTrue;

public class BinaryMatrixRankTest {

    @Test
    public void binaryMatrixRankExample() {
        int[] bits = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
                0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1,
                0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0};
        binaryMatrixRank(bits);
    }

    @Test
    public void binaryMatrixRankLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        binaryMatrixRank(lfsrGenerator.nextBit(10000));
    }

    @Test
    public void binaryMatrixRankFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void binaryMatrixRankSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void binaryMatrixRankFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void binaryMatrixRankSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }


    private void binaryMatrixRank(int[] bin_data) {
        int size = 32;
        int n = bin_data.length;
        int block_size = size * size;
        int num_m = (int) Math.floor(n / (size * size));
        int block_start = 0;
        int block_end = block_size;
        double[][] m = new double[128][128];
        double[] max_ranks = new double[]{0, 0, 0};
        for (int im = 0; im < num_m; im++) {
            int[] block_data = Arrays.copyOfRange(bin_data, block_start, block_end);
            double[] block = new double[block_data.length];
            for (int i = 0; i < block_data.length; i++) {
                if (block_data[i] == 1) {
                    block[i] = 1.0;
                }
            }
            m = shape(block, size);
            BinaryMatrix ranker = new BinaryMatrix(m, size, size);
            int rank = ranker.compute_rank();
            if (rank == size) {
                max_ranks[0] += 1;
            }
            else if (rank == (size - 1)) {
                max_ranks[1] += 1;
            }
            else {
                max_ranks[2] += 1;
            }
            block_start += block_size;
            block_end += block_size;


        }
        double[] piks = new double[]{1.0, 0.0, 0.0};
        for (int x = 1; x < 50; x++) {
            piks[0] *= 1 - (1.0 / (Math.pow(2, x)));
            piks[1] = 2 * piks[0];
            piks[2] = 1 - piks[0] - piks[1];
        }
        double chi = 0.0;
        for (int i = 0; i < piks.length; i++) {
            chi += pow((max_ranks[i] - piks[i] * num_m), 2.0) / (piks[i] * num_m);
        }

        double p = Math.exp(-chi / 2);

        System.out.println(p);

        assertTrue(p >= 0.01);
    }

    public static double[][] shape(double[] arr, int size) {
        double[][] matrix = new double[size][size];
        int counter = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = arr[counter++];
            }
        }
        return matrix;
    }

}
