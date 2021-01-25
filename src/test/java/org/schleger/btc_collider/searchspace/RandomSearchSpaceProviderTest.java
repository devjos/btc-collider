package org.schleger.btc_collider.searchspace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

public class RandomSearchSpaceProviderTest {

    @Test
    public void simple() throws IOException {
        SearchSpaceProvider p = new RandomSearchSpaceProvider();
        SearchSpace s = p.nextSearchSpace();
        BigInteger fromInclusive = s.getFromInclusive();
        BigInteger toExclusive = s.getToExclusive();

        Assertions.assertEquals(1, fromInclusive.signum());
        Assertions.assertEquals(fromInclusive.add(BigInteger.valueOf(500_000)), toExclusive);
    }
}
