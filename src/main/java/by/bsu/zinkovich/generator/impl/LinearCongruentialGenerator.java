package by.bsu.zinkovich.generator.impl;


import org.apache.commons.math3.random.AbstractRandomGenerator;

public class LinearCongruentialGenerator extends AbstractRandomGenerator {

    private long multiplier;
    private long addend;
    private long mask;

    private long oldseed;

    public LinearCongruentialGenerator() {
        this(0, 17, 3, 2097152);
    }

    public LinearCongruentialGenerator(long seed, long multiplier, long addend, long mask) {
        this.oldseed = seed;
        this.multiplier = multiplier;
        this.addend = addend;
        this.mask = mask;
    }

    @Override
    public void setSeed(long seed) {
        this.oldseed = seed;
    }

    @Override
    public double nextDouble() {
        oldseed = (multiplier * oldseed + addend) % mask;
        return (double)oldseed / mask;
    }
}
