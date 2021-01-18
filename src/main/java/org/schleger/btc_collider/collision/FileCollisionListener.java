package org.schleger.btc_collider.collision;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCollisionListener implements CollisionListener {

    private static final Logger LOG = LogManager.getLogger();
    private final String dir;
    private int count = 0;

    public FileCollisionListener(String dir){
        this.dir = dir;
    }

    @Override
    public void collisionEvent(BigInteger key) {
        count++;
        String keyStr = key.toString(16);
        String filename = count + ".txt";
        Path p = Path.of(dir, filename);
        try {
            Files.write(p, keyStr.getBytes());
        } catch (IOException e) {
            LOG.error("Could not write collision to disk", e);
        }
    }
}
