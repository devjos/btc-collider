package org.schleger.btc_collider.collider;

import gnu.trove.set.hash.TCustomHashSet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.schleger.btc_collider.AddressUtils;
import org.schleger.btc_collider.addresses.FileAddressesProvider;

import java.math.BigInteger;
import java.nio.file.Path;
import java.security.Security;
import java.util.List;

public class ColliderCallableTest {

    static{
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Test
    public void privateKey3() throws Exception {
        FileAddressesProvider p = new FileAddressesProvider(Path.of("addresses", "address_pk3.txt.gz"));
        TCustomHashSet<byte[]> addresses = p.provideAddresses();

        ColliderCallable colliderCallable = new ColliderCallable(addresses, BigInteger.ONE, BigInteger.TEN);
        ColliderResult result = colliderCallable.call();

        List<BigInteger> collisions = result.getCollisions();
        Assertions.assertEquals(1, collisions.size());
        Assertions.assertEquals(3, collisions.get(0).intValue());
    }

    @Test
    public void puzzleTransactions() throws Exception {
        FileAddressesProvider p = new FileAddressesProvider(Path.of("addresses", "puzzle_3_to_7.txt.gz"));
        TCustomHashSet<byte[]> addresses = p.provideAddresses();

        ColliderCallable colliderCallable = new ColliderCallable(addresses, BigInteger.ONE, BigInteger.valueOf(100));
        ColliderResult result = colliderCallable.call();

        List<BigInteger> collisions = result.getCollisions();
        Assertions.assertEquals(5, collisions.size());
        
        int[] expected = {7, 8, 21, 49, 76};
        for (int i=0; i<collisions.size(); i++){
            Assertions.assertEquals(expected[i], collisions.get(i).intValue());
        }
    }

    @Test
    public void puzzleTransaction54() throws Exception {
        //assert that puzzle transaction 54 is found in file with three puzzle transactions
        FileAddressesProvider p = new FileAddressesProvider(Path.of("addresses", "puzzle_52_to_54.txt.gz"));
        TCustomHashSet<byte[]> addresses = p.provideAddresses();

        BigInteger start = new BigInteger("efae164cb9e1c", 16);
        BigInteger end = new BigInteger("efae164cb9e5c", 16);

        ColliderCallable colliderCallable = new ColliderCallable(addresses, start, end);
        ColliderResult result = colliderCallable.call();

        List<BigInteger> collisions = result.getCollisions();
        Assertions.assertEquals(1, collisions.size());

        Assertions.assertEquals("efae164cb9e3c", collisions.get(0).toString(16));

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB3kEt3BU5TJooQ",
            "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB4gHj2LY81dM4N",
            "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5mG3VbvRiMXFMAi2euC7",
            "5HpHagT65TZzG1PH3CSu63k8DbpvD8sovht9C5oEoZSk2JTBpU4",
            "5Km2kuu7vtFDPpxywn4u3NLpbr5jKpTB3jsuDU2KYEqeHZGfNdG",
    })
    public void findWIF(String wif) throws Exception {
        FileAddressesProvider p = new FileAddressesProvider(Path.of("addresses", "wif", wif + ".txt.gz"));
        TCustomHashSet<byte[]> addresses = p.provideAddresses();

        BigInteger privateKey = AddressUtils.wifToPrivateKey(wif);

        BigInteger start = privateKey.subtract(BigInteger.TEN);
        BigInteger end = privateKey.add(BigInteger.TEN);

        ColliderCallable colliderCallable = new ColliderCallable(addresses, start, end);
        ColliderResult result = colliderCallable.call();

        List<BigInteger> collisions = result.getCollisions();
        Assertions.assertEquals(2, collisions.size());

        Assertions.assertEquals(privateKey, collisions.get(0));
        Assertions.assertEquals(privateKey, collisions.get(1));
    }
}
