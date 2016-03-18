package by.bsu.zinkovich.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum Exponent {

    INSTANCE;

    private int[] bits;

    Exponent() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/e.txt"));
            bits = new int[bytes.length];
            for(int i = 0; i < bytes.length; i++) {
                bits[i] = bytes[i] - 48;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getBits() {
        return bits;
    }
}
