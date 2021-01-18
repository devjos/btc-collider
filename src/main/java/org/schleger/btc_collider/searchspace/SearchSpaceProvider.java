package org.schleger.btc_collider.searchspace;

import java.io.IOException;
import java.math.BigInteger;

public interface SearchSpaceProvider {

    SearchSpace nextSearchSpace() throws IOException;
    void done(SearchSpace searchSpace) throws IOException;
}
