package by.bsu.zinkovich.generator.impl;


import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MacLarenMarsagliaGenerator extends AbstractRandomGenerator
{
    private RandomGenerator first;
    private RandomGenerator second;

    private List<Double> buffer;
    private int limit;

    public MacLarenMarsagliaGenerator(RandomGenerator first, RandomGenerator second, int limit)
    {
        this.first = first;
        this.second = second;
        this.limit = limit;

        buffer = Stream.generate(first::nextDouble).limit(limit).collect(toList());
    }

    @Override
    public double nextDouble()
    {
        int index = (int)(second.nextDouble()*limit);
        double result = buffer.get(index);
        buffer.set(index, first.nextDouble());

        return result;
    }

    @Override
    public void setSeed(long seed) {}
}
