package by.bsu.zinkovich.util;


public class LFSRUtil {

    private LFSRUtil() {}

    public static int minLength(int[] array) {
        final int N = array.length;
        int[] b = new int[N];
        int[] c = new int[N];
        int[] t = new int[N];
        b[0] = 1;
        c[0] = 1;
        int l = 0;
        int m = -1;
        for (int n = 0; n < N; n++) {
            int d = 0;
            for (int i = 0; i <= l; i++) {
                d ^= c[i] * array[n - i];
            }
            if (d == 1) {
                System.arraycopy(c, 0, t, 0, N);
                int N_M = n - m;
                for (int j = 0; j < N - N_M; j++) {
                    c[N_M + j] ^= b[j];
                }
                if (l <= n / 2) {
                    l = n + 1 - l;
                    m = n;
                    System.arraycopy(t, 0, b, 0, N);
                }
            }
        }
        return l;
    }
}
