package org.schleger.btc_collider.addresses;

import gnu.trove.strategy.HashingStrategy;

import java.util.Arrays;

public class AddressHashingStrategy implements HashingStrategy<byte[]> {
    @Override
    public int computeHashCode(byte[] bytes) {
        return Arrays.hashCode(bytes);
    }

    @Override
    public boolean equals(byte[] bytes, byte[] other) {
        return Arrays.equals(bytes, other);
    }
}
