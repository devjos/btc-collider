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
        Path p;
        do{
            count++;
            String filename = count + ".txt";
            p = Path.of(dir, filename);
        } while (Files.exists(p));

        try {
            Files.write(p, key.toString(16).getBytes());
        } catch (IOException e) {
            LOG.error("Could not write collision to disk", e);
        }
    }
}
