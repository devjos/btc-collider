package org.schleger.btc_collider.addresses;

import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.set.hash.THashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.schleger.btc_collider.AddressUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public class FileAddressesProvider implements AddressesProvider {

    private static final Logger LOG = LogManager.getLogger();
    private final Path file;

    public FileAddressesProvider(Path file){
        this.file = file;
    }


    public TCustomHashSet<byte[]> read() throws IOException {
        TCustomHashSet<byte[]> addressHashes = new TCustomHashSet<>(new AddressHashingStrategy(), 30_000_000);
        //THashSet<byte[]> addressHashes = new THashSet<>(30_000_000);

        InputStream fileStream = new FileInputStream(file.toFile());
        InputStream gzipStream = new GZIPInputStream(fileStream);
        Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
        BufferedReader buffered = new BufferedReader(decoder);

        int num_p2pk = 0;
        int num_p2sh = 0;
        int num_p2wpk = 0;
        int num_p2wsh = 0;
        int num_misc = 0;

        String line = null;
        while ((line = buffered.readLine())!=null) {
            String address = line.trim();
            LOG.trace("Read address: {}",address);
            if (address.startsWith("d-")
                    || address.startsWith("m-")
                    || address.startsWith("s-")){
                num_misc++;
                continue;
                // this is nulldata, multisig, and misc
                // see https://github.com/Blockchair/Blockchair.Support/issues/273
            } else if (address.startsWith("1")){
                num_p2pk++;

                byte[] addressHash = AddressUtils.getAddressHash(address);
                addressHashes.add(addressHash);
            } else if (address.startsWith("3")){
                //P2SH
                num_p2sh++;
                continue;
            } else if (address.startsWith("bc1")){
                try{
                    byte[] addressHash = AddressUtils.getAddressHash(address);
                    addressHashes.add(addressHash);

                    num_p2wpk++;
                } catch (UnsupportedOperationException e){
                    num_p2wsh++;
                }

            }
        }
        LOG.info("Address statistics");
        LOG.info("P2PK: " + num_p2pk);
        LOG.info("P2SH:  " + num_p2sh);
        LOG.info("P2WPK: " + num_p2wpk);
        LOG.info("P2WSH:  " + num_p2wsh);
        LOG.info("misc:   " + num_misc);

        return addressHashes;
    }

    @Override
    public TCustomHashSet<byte[]> provideAddresses() throws IOException {
        return read();
    }
}
