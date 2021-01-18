package org.schleger.btc_collider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.schleger.btc_collider.collision.FileCollisionListener;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCollisionListenerTest {

    private static final String DIR = "collisionsTest";

    @Test
    public void writesToFile1() throws IOException {
        Path p = Path.of(DIR, "1.txt");
        Files.deleteIfExists(p);

        FileCollisionListener l = new FileCollisionListener(DIR);
        l.collisionEvent(BigInteger.ONE);

        Assertions.assertTrue(Files.exists(p));
        Assertions.assertEquals("1" ,Files.readString(p));
    }

    @Test
    public void writesToFileBig() throws IOException {
        Path p = Path.of(DIR, "1.txt");
        Files.deleteIfExists(p);

        FileCollisionListener l = new FileCollisionListener(DIR);
        BigInteger key = new BigInteger("e6a362738be", 16);
        l.collisionEvent(key);

        Assertions.assertTrue(Files.exists(p));
        Assertions.assertEquals("e6a362738be" ,Files.readString(p));
    }
}
