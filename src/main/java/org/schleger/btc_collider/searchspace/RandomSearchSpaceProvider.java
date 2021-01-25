package org.schleger.btc_collider.searchspace;

import java.io.IOException;
import java.math.BigInteger;

public class RandomSearchSpaceProvider implements SearchSpaceProvider {

    private static final BigInteger INTERVAL = BigInteger.valueOf(500_000);

    @Override
    public SearchSpace nextSearchSpace() throws IOException {
        return SearchSpace.random(INTERVAL);
    }

    @Override
    public void done(SearchSpace searchSpace) throws IOException {
        //ignore
    }
}
