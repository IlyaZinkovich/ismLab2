package by.bsu.zinkovich.generator.impl;

import by.bsu.zinkovich.generator.BinaryGenerator;

public class LFSRGenerator implements BinaryGenerator {

    private long register = 1L;
    private int[] coefficients;

    public LFSRGenerator(int... coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public int nextBit() {
        long temp = 0;
        for(int coefficient : coefficients) {
            temp ^= (register >> (coefficient - 1));
        }
        temp &= 1L;
        register = (temp << (coefficients[0] - 1)) | (register >> 1);

        return (int) (register & 1L);
    }


    public long getRegister() {
        return register;
    }
}
