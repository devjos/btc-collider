package org.schleger.btc_collider;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.Security;

public class AddressUtilsTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testFromUncompressed() throws Exception{
        String[] uncompressedAddresses = new String[]{
                "1EHNa6Q4Jz2uvNExL497mE43ikXhwF6kZm", //key is 1
                "1LagHJk2FyCV2VzrNHVqg3gYG4TSYwDV4m", //key is 2
                "1NZUP3JAc9JkmbvmoTv7nVgZGtyJjirKV1", //key is 3
        };
        String[] compressedAddresses = new String[]{
                "1BgGZ9tcN4rm9KBzDn7KprQz87SZ26SAMH", //key is 1
                "1cMh228HTCiwS8ZsaakH8A8wze1JR5ZsP", //key is 2
                "1CUNEBjYrCn2y1SdiUMohaKUi4wpP326Lb", //key is 3
        };

        for (int i = 0; i<uncompressedAddresses.length; i++){
            BCECPublicKey publicKey = AddressUtils.publicKey(BigInteger.valueOf(i+1));
            AddressHashes addressHashes = AddressUtils.getAddressHashes(publicKey);

            //compressed
            byte [] expectedCompressed = AddressUtils.getAddressHash(compressedAddresses[i]);
            Assertions.assertArrayEquals(expectedCompressed, addressHashes.getCompressed());

            //uncompressed
            byte [] expectedUncompressed = AddressUtils.getAddressHash(uncompressedAddresses[i]);
            Assertions.assertArrayEquals(expectedUncompressed, addressHashes.getUncompressed());
        }
    }

    @Test
    public void testBech32() throws Exception{
        //from https://bitcointalk.org/index.php?topic=4992632.0
        //priv key 1
        String bech32Address = "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4";

        byte[] addressHash = AddressUtils.getAddressHash(bech32Address);
        byte [] expected = Hex.decodeHex("751e76e8199196d454941c45d1b3a323f1433bd6");
        Assertions.assertArrayEquals(expected, addressHash);
    }

    @Test
    public void wifToPrivateKey(){
        String[] wifArr = new String[]{
                "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB3kEsreAnchuDf", //key is 1
                "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB3kEsreAvUcVfH", //key is 2
                "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB3kEsreB1FQ8BZ", //key is 3
        };
        for (int i = 0; i<wifArr.length; i++){
            BigInteger privateKey = AddressUtils.wifToPrivateKey(wifArr[i]);
            Assertions.assertEquals(i+1, privateKey.intValue());
        }

        BigInteger pk = AddressUtils.wifToPrivateKey("5JxrY7MyGiD413FDM4vHVqxqhv5um8yLtxXx5mVk2R662y6Qd8L");
        String expected = "9726b7fa33993313e772f547f2f0288e645e012c71cca86a791532b5c8457e46";
        Assertions.assertEquals(expected, pk.toString(16));
    }

    @Test
    public void testYIs33Bytes(){
        BigInteger pk = new BigInteger("9726b7fa33993313e772f547f2f0288e645e012c71cca86a791532b5c8457e46", 16);
        BCECPublicKey publicKey = AddressUtils.publicKey(pk);
        AddressHashes addressHashFromPrivate = AddressUtils.getAddressHashes(publicKey);
        byte[] addressHashFromAddress = AddressUtils.getAddressHash("19qjnwhBH7Vtfx5qeJSf6TpA9RquZCHdp6");

        Assertions.assertArrayEquals(addressHashFromAddress, addressHashFromPrivate.getUncompressed());
    }

    @Test
    public void testYIs31Bytes(){
        BigInteger pk = new BigInteger("d11937325a66f461a139e39e9a221a8e04dbeefc5fbadff77be850aa7362f284", 16);

        BCECPublicKey publicKey = AddressUtils.publicKey(pk);
        AddressUtils.getAddressHashes(publicKey);
    }


}
