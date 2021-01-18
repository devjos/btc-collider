package org.schleger.btc_collider.addresses;

import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.set.hash.THashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.schleger.btc_collider.AddressUtils;

import java.io.IOException;
import java.nio.file.Path;

public class FileAddressesProviderTest {


    @Test
    public void simple() throws IOException {
        FileAddressesProvider f = new FileAddressesProvider(Path.of("addresses", "test.txt.gz"));
        TCustomHashSet<byte[]> addressHashes = f.read();

        Assertions.assertEquals(1, addressHashes.size());

        byte[] h1 = AddressUtils.getAddressHash("127NVqnjf8gB9BFAW2dnQeM6wqmy1gbGtv");
        Assertions.assertTrue(addressHashes.contains(h1));

    }
}
