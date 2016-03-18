package by.bsu.zinkovich.generator;


public class BinaryMatrix {

    private int M;
    private int Q;
    private double[][] A;
    private int m;

    public BinaryMatrix(double[][] matrix, int rows, int cols) {

        this.M = rows;
        this.Q = cols;
        this.A = matrix;
        this.m = Math.min(rows, cols);
    }

    public int compute_rank() {
        int i = 0;
        int found = 0;
        while (i < this.m - 1) {
            if (this.A[i][i] == 1) {
                this.perform_row_operations(i, true);
            } else {
                found = find_unit_element_swap(i, true);

                if (found == 1) {
                    this.perform_row_operations(i, true);
                }
            }
            i += 1;
        }

        i = this.m - 1;
        while (i > 0) {
            if (this.A[i][i] == 1) {
                this.perform_row_operations(i, false);
            } else {
                if (this.find_unit_element_swap(i, false) == 1) {
                    this.perform_row_operations(i, false);
                }
            }
            i -= 1;
        }

        return this.determine_rank();
    }

    public void perform_row_operations(int i, boolean forward_elimination) {
        int j = 0;
        if (forward_elimination) {
            j = i + 1;
            while (j < this.M) {
                if (this.A[j][i] == 1) {
                    for (int k = 0; k < A[j].length; k++) {
                        this.A[j][k] = (this.A[j][k] + this.A[i][k]) % 2;
                    }
                }
                j += 1;
            }
        } else {
            j = i - 1;
            while (j >= 0) {
                if (this.A[j][i] == 1) {
                    for (int k = 0; k < A[j].length; k++) {
                        this.A[j][k] = (this.A[j][k] + this.A[i][k]) % 2;
                    }
                }
                j -= 1;
            }
        }
    }

    public int find_unit_element_swap(int i, boolean forward_elimination) {
        int row_op = 0;
        int index;
        if (forward_elimination) {
            index = i + 1;
            while (index < this.M && this.A[index][i] == 0) {
                index += 1;
            }
            if (index < this.M) {
                row_op = this.swap_rows(i, index);
            }
        } else {
            index = i - 1;
            while (index >= 0 && this.A[index][i] == 0) {
                index -= 1;
            }
            if (index >= 0) {
                row_op = this.swap_rows(i, index);
            }
        }
        return row_op;
    }

    public int swap_rows(int i, int ix){
        double[] temp = this.A[i];
        this.A[i] = this.A[ix];
        this.A[ix] = temp;
        return 1;
    }

    public int determine_rank() {
        int rank = this.m;
        int i = 0;
        while (i < this.M) {
            int all_zeros = 1;
            for (int j = 0; i < this.Q; i++) {
                if (this.A[i][j] == 1)
                    all_zeros = 0;
            }
            if (all_zeros == 1) {
                rank -= 1;
            }
            i += 1;
        }
        return rank;
    }
}
