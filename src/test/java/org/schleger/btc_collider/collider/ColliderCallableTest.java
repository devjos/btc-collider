package org.schleger.btc_collider.collider;

import gnu.trove.set.hash.TCustomHashSet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
        FileAddressesProvider p = new FileAddressesProvider(Path.of("addresses", "address_puzzle.txt.gz"));
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
}
