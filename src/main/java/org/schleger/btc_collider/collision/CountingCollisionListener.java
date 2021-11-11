package org.schleger.btc_collider.collision;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class CountingCollisionListener implements CollisionListener {

    private AtomicInteger count = new AtomicInteger();

    @Override
    public void collisionEvent(BigInteger key) {
        count.incrementAndGet();
    }

    public int getCollisionCount(){
        return count.get();
    }
}
