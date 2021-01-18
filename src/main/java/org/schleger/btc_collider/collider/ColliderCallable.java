package org.schleger.btc_collider.collider;

import com.thoughtworks.qdox.model.expression.Add;
import gnu.trove.set.hash.TCustomHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.schleger.btc_collider.AddressHashes;
import org.schleger.btc_collider.AddressUtils;
import org.schleger.btc_collider.searchspace.SearchSpace;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ColliderCallable implements Callable<ColliderResult> {

    private static final Logger LOG = LogManager.getLogger();
    private final TCustomHashSet<byte[]> addresses;
    private final SearchSpace searchSpace;

    public ColliderCallable(TCustomHashSet<byte[]> addresses, BigInteger fromInclusive, BigInteger toExclusive){
        this.addresses = addresses;
        this.searchSpace = new SearchSpace(fromInclusive, toExclusive);
    }

    public ColliderCallable(TCustomHashSet<byte[]> addresses, SearchSpace searchSpace) {
        this.addresses = addresses;
        this.searchSpace = searchSpace;
    }

    @Override
    public ColliderResult call() throws Exception {
        long startMillis = System.currentTimeMillis();

        List<BigInteger> collisions = new ArrayList<>(0);

        for (BigInteger current = searchSpace.getFromInclusive(); current.compareTo(searchSpace.getToExclusive())<0; current=current.add(BigInteger.ONE)){
            BCECPublicKey publicKey = AddressUtils.publicKey(current);

            AddressHashes addressHashes = AddressUtils.getAddressHashes(publicKey);

            if (addresses.contains(addressHashes.getCompressed())){
                LOG.info("Found matching private key ({}) for compressed address", current.toString(16));
                collisions.add(current);
            }

            if (addresses.contains(addressHashes.getUncompressed())){
                LOG.info("Found matching private key ({}) for uncompressed address", current.toString(16));
                collisions.add(current);
            }
        }

        long keys = searchSpace.getToExclusive().subtract(searchSpace.getFromInclusive()).longValue();
        long seconds = (System.currentTimeMillis() - startMillis) / 1000;
        seconds++; //round up, avoids division by zero
        long rate = keys / seconds;

        LOG.info("{} collisions for {} at {} keys/sec", collisions.size(), searchSpace, rate);

        return new ColliderResult(searchSpace, collisions);
    }
}
