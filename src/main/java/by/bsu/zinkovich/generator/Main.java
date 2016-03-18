package by.bsu.zinkovich.generator;

import by.bsu.zinkovich.generator.impl.LFSRGenerator;
import by.bsu.zinkovich.generator.impl.SelfShrinkingGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args)  throws IOException{
        SelfShrinkingGenerator selfShrinkingGenerator = new SelfShrinkingGenerator();
        LFSRGenerator firstGenerator = new LFSRGenerator(19, 18, 17, 14, 1);
        LFSRGenerator secondGenerator = new LFSRGenerator(22, 21, 1);
        LFSRGenerator thirdGenerator = new LFSRGenerator(23, 22, 21, 8, 1);

        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/input.txt"));
        byte[] codes = new byte[bytes.length];

        for(int i = 0; i < codes.length; i++) {
            codes[i] = createByte(selfShrinkingGenerator);
        }

        for(int i = 0; i < bytes.length; i++) {
            bytes[i] ^= codes[i];
        }

        Files.write(Paths.get("src/main/resources/output.txt"), bytes);
    }

    private static byte createByte(SelfShrinkingGenerator selfShrinkingGenerator) {
        String str = "";
        for(int i = 0; i < 8; i++) {
            str += selfShrinkingGenerator.nextBit();
        }
        str += 'b';
        return Byte.parseByte(str);
    }
}
