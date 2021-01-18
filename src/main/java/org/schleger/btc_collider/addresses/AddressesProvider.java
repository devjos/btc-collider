package org.schleger.btc_collider.addresses;

import gnu.trove.set.hash.TCustomHashSet;

import java.io.IOException;

public interface AddressesProvider {
    TCustomHashSet<byte[]> provideAddresses() throws IOException;
}
