package org.schleger.btc_collider;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.schleger.btc_collider.collision.FileCollisionListener;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCollisionListenerTest {

    private static final String DIR = "collisionsTest";

    @BeforeEach
    public void cleanUpDir() throws IOException {
        Path dir = Path.of(DIR);
        FileUtils.cleanDirectory(dir.toFile());
    }

    @Test
    public void writesToFile1() throws IOException {
        Path p = Path.of(DIR, "1.txt");

        FileCollisionListener l = new FileCollisionListener(DIR);
        l.collisionEvent(BigInteger.ONE);

        Assertions.assertTrue(Files.exists(p));
        Assertions.assertEquals("1" ,Files.readString(p));
    }

    @Test
    public void writesToFileBig() throws IOException {
        Path p = Path.of(DIR, "1.txt");

        FileCollisionListener l = new FileCollisionListener(DIR);
        BigInteger key = new BigInteger("e6a362738be", 16);
        l.collisionEvent(key);

        Assertions.assertTrue(Files.exists(p));
        Assertions.assertEquals("e6a362738be" ,Files.readString(p));
    }

    @Test
    public void doesNotOverwriteExisting() throws IOException {
        Path existingPath = Path.of(DIR, "1.txt");
        String existingContent = "test";
        Files.write(existingPath, existingContent.getBytes());

        FileCollisionListener l = new FileCollisionListener(DIR);
        l.collisionEvent(BigInteger.ONE);

        Assertions.assertTrue(Files.exists(existingPath));
        Assertions.assertEquals(existingContent, Files.readString(existingPath));

        Path secondPath = Path.of(DIR, "2.txt");
        Assertions.assertTrue(Files.exists(secondPath));
        Assertions.assertEquals("1", Files.readString(secondPath));
    }
}
