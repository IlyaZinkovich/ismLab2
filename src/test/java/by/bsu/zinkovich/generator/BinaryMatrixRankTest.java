package by.bsu.zinkovich.generator;

import by.bsu.zinkovich.generator.impl.LFSRGenerator;
import by.bsu.zinkovich.generator.impl.LinearCongruentialGenerator;
import by.bsu.zinkovich.generator.impl.MacLarenMarsagliaGenerator;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import static java.lang.Math.pow;
import static org.junit.Assert.assertTrue;

public class BinaryMatrixRankTest {

    @Test
    public void binaryMatrixRankLFSR() {
        LFSRGenerator lfsrGenerator = new LFSRGenerator(32, 31, 30, 28, 26, 1);
        System.out.println("LFSR:");
        binaryMatrixRank(lfsrGenerator.nextBit(10000));
    }

    @Test
    public void binaryMatrixRankFirstLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator(4, 7, 3, 11);
        System.out.println("FirstLinearCongruential:");
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void binaryMatrixRankSecondLinearCongruential() {
        RandomGenerator generator = new LinearCongruentialGenerator();
        System.out.println("SecondLinearCongruential:");
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void binaryMatrixRankFirstMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(first, second, 10);
        System.out.println("FirstMaclarenMarsaglia:");
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }

    @Test
    public void binaryMatrixRankSecondMaclarenMarsaglia() {
        RandomGenerator first = new LinearCongruentialGenerator(4, 7, 3, 11);
        RandomGenerator second = new LinearCongruentialGenerator();
        RandomGenerator generator = new MacLarenMarsagliaGenerator(second, first, 10);
        System.out.println("SecondMaclarenMarsaglia:");
        binaryMatrixRank(new UniformIntegerDistribution(generator, 0, 1).sample(10000));
    }


    private void binaryMatrixRank(int[] bin_data) {
        int M = 32, Q = 32;
        int n = 10000;
        int num_m = n / (M * Q);
        double piks[] = {0.2888, 0.5776, 0.1336};
        double[] max_ranks = {0, 0, 0};

        for (int i = 0; i < num_m; i++) {
            int[][] matrix = new int[M][];
            for (int j = 0; j < M; j++) {
                matrix[j] = new int[Q];
                for (int k = 0; k < Q; k++) {
                    matrix[j][k] = bin_data[i * M * Q + j * M + k];
                }
            }
            int rank = computeRank(M, Q, matrix);
            if (rank == M) {
                max_ranks[0]++;
            } else if (rank == M - 1) {
                max_ranks[1]++;
            } else {
                max_ranks[2]++;
            }
        }

        double chi = 0.0;
        for (int i = 0; i < piks.length; i++) {
            chi += pow((max_ranks[i] - piks[i] * num_m), 2.0) / (piks[i] * num_m);
        }

        double p = Math.exp(-chi / 2);

        System.out.println(p);

        assertTrue(p >= 0.01);
    }

    private static int computeRank(int M, int Q, int[][] matrix) {
        int i, rank, m = Math.min(M, Q);
        for (i = 0; i < m - 1; i++) {
            if (matrix[i][i] == 1) {
                perform_elementary_row_operations(0, i, M, Q, matrix);
            } else {
                if (find_unit_element_and_swap(0, i, M, Q, matrix) == 1) {
                    perform_elementary_row_operations(0, i, M, Q, matrix);
                }
            }
        }

        for (i = m - 1; i > 0; i--) {
            if (matrix[i][i] == 1) {
                perform_elementary_row_operations(1, i, M, Q, matrix);
            } else {
                if (find_unit_element_and_swap(1, i, M, Q, matrix) == 1) {
                    perform_elementary_row_operations(1, i, M, Q, matrix);
                }
            }
        }

        rank = determine_rank(m, M, Q, matrix);

        return rank;
    }

    private static void perform_elementary_row_operations(int flag, int i, int M, int Q, int[][] A) {
        int j, k;

        if (flag == 0) {
            for (j = i + 1; j < M; j++) {
                if (A[j][i] == 1) {
                    for (k = i; k < Q; k++) {
                        A[j][k] = (A[j][k] + A[i][k]) % 2;
                    }
                }
            }
        } else {
            for (j = i - 1; j >= 0; j--) {
                if (A[j][i] == 1) {
                    for (k = 0; k < Q; k++) {
                        A[j][k] = (A[j][k] + A[i][k]) % 2;
                    }
                }
            }
        }
    }

    private static int find_unit_element_and_swap(int flag, int i, int M, int Q, int[][] A) {
        int index, row_op = 0;

        if (flag == 0) {
            index = i + 1;
            while ((index < M) && (A[index][i] == 0)) {
                index++;
            }
            if (index < M) {
                row_op = swap_rows(i, index, Q, A);
            }
        } else {
            index = i - 1;
            while ((index >= 0) && (A[index][i] == 0)) {
                index--;
            }
            if (index >= 0) {
                row_op = swap_rows(i, index, Q, A);
            }
        }

        return row_op;
    }

    private static int swap_rows(int i, int index, int Q, int[][] A) {
        int p;
        int temp;

        for (p = 0; p < Q; p++) {
            temp = A[i][p];
            A[i][p] = A[index][p];
            A[index][p] = temp;
        }

        return 1;
    }

    private static int determine_rank(int m, int M, int Q, int[][] A) {
        int i, j, rank, allZeroes;
        rank = m;
        for (i = 0; i < M; i++) {
            allZeroes = 1;
            for (j = 0; j < Q; j++) {
                if (A[i][j] == 1) {
                    allZeroes = 0;
                    break;
                }
            }
            if (allZeroes == 1) {
                rank--;
            }
        }
        return rank;
    }

}
