package org.schleger.btc_collider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.Security;

public class AddressHashTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testFromP2PK(){
        //example from https://en.bitcoin.it/wiki/Technical_background_of_version_1_Bitcoin_addresses
        byte[] addressHash = AddressUtils.getAddressHash("1PMycacnJaSqwwJqjawXBErnLsZ7RkXUAs");
        byte[] result = Hex.decode("f54a5851e9372b87810a8e60cdd2e7cfd80b6e31");
        Assertions.assertArrayEquals(result, addressHash);
    }

    @Test
    public void testFromP2SH(){
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            AddressUtils.getAddressHash("35hK24tcLEWcgNA4JxpvbkNkoAcDGqQPsP");
        });
    }

    @Test
    public void testFromMultipleP2PK(){
        String[] addresses = new String[] {
            "1P5ZEDWTKTFGxQjZphgWPQUpe554WKDfHQ",
            "1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF",
            "1LdRcdxfbSnmCYYNdeYpUnztiYzVfBEQeC",
            "1AC4fMwgY8j9onSbXEWeH6Zan8QGMSdmtA",
            "1LruNZjwamWJXThX2Y8C2d47QqhAkkc5os",
            "17hf5H8D6Yc4B7zHEg3orAtKn7Jhme7Adx",
            "1NDyJtNTjmwk5xPNhjgAMu4HDHigtobu1s",
            "12XqeqZRVkBDgmPLVY4ZC6Y4ruUUEug8Fx"
        };
        for (String address : addresses ){
            byte[] hash = AddressUtils.getAddressHash(address);
            Assertions.assertEquals(20, hash.length);
        }
    }

}
