package org.schleger.btc_collider.addresses;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.schleger.btc_collider.AddressUtils;

import java.util.Arrays;

public class AddressHashingStrategyTest {

    private static AddressHashingStrategy s = new AddressHashingStrategy();

    @Test
    public void acceptSameObject(){
        byte[] a1 = AddressUtils.getAddressHash("1EHNa6Q4Jz2uvNExL497mE43ikXhwF6kZm");
        Assertions.assertTrue(s.equals(a1, a1));
    }

    @Test
    public void acceptSameAddresses(){
        byte[] a1 = AddressUtils.getAddressHash("1EHNa6Q4Jz2uvNExL497mE43ikXhwF6kZm");
        byte[] a1_copy = Arrays.copyOf(a1, a1.length);
        Assertions.assertTrue(s.equals(a1, a1_copy));
    }

    @Test
    public void declineDifferentAddresses(){
        byte[] a1 = AddressUtils.getAddressHash("1EHNa6Q4Jz2uvNExL497mE43ikXhwF6kZm");
        byte[] a2 = AddressUtils.getAddressHash("1LagHJk2FyCV2VzrNHVqg3gYG4TSYwDV4m");
        Assertions.assertFalse(s.equals(a1, a2));
    }
}
