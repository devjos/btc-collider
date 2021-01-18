package org.schleger.btc_collider;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Bech32;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

public class AddressUtils {

    private static final Logger LOG = LogManager.getLogger();
    private static final ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
    private static KeyFactory keyFactory;

    static {
        try {
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            LOG.error("Could not initialize key factory", e);
        }
    }

    public static byte[] getAddressHash(String addressString){
        if (addressString.startsWith("3")){
            throw new UnsupportedOperationException("P2SH not implemented yet");
        }
        else if (addressString.startsWith("1")){
            byte[] addressBytes = Base58.decode(addressString);
            if ( addressBytes.length != 25){
                throw new IllegalArgumentException("addressString is not 25 bytes long after Base58 decoding");
            }
            byte[] addressHash = new byte[20];
            System.arraycopy(addressBytes, 1, addressHash, 0, 20);
            return addressHash;
        } else if (addressString.startsWith("bc1")){
            //segwit
            Bech32.Bech32Data bech32 = Bech32.decode(addressString);
            if (bech32.data.length != 33){
                //P2WSH
                throw new UnsupportedOperationException("Not implemented yet!");
            }
            //P2WPKH

            byte[] hash = new byte[20];
            int bytePos = 0;

            for (int i=1; i<bech32.data.length; i++){
                byte d = bech32.data[i];

                for (int srcBit=4; srcBit>=0; srcBit--){
                    int targetBit = 7 - (bytePos % 8);
                    byte bitSet = (byte)(((d >> srcBit)&1) << targetBit);

                    hash[bytePos/8] = (byte) (hash[bytePos/8] | bitSet );
                    bytePos++;
                }
            }

            return hash;
        }
        else{
            throw new IllegalArgumentException("illegal address: " + addressString);
        }

    }

    public static BigInteger wifToPrivateKey(String wif){
        byte[] wifBytes = Base58.decode(wif);
        if (wifBytes.length != 37 ){
            throw new IllegalArgumentException("Unknown wif size: " + wifBytes.length);
        }
        byte[] privateKey = new byte[32];
        System.arraycopy(wifBytes, 1, privateKey, 0, 32);
        return new BigInteger(1, privateKey);
    }

    public static BCECPublicKey publicKey(BigInteger privateKey){
        try{
            ECPoint q = params.getG().multiply(privateKey);
            ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(q, params);
            return (BCECPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            LOG.error("Invalid key", e);
            throw new IllegalStateException(e);
        }
    }

    public static AddressHashes getAddressHashes(BCECPublicKey publicKey){
        byte[] x = publicKey.getW().getAffineX().toByteArray();
        byte[] y = publicKey.getW().getAffineY().toByteArray();

        if (x.length != 32){
            x = make32(x);
        }
        if (y.length != 32){
            y = make32(y);
        }

        byte[] compressed = new byte[33];
        compressed[0] = isEven(publicKey.getW().getAffineY()) ? (byte) 0x02 : (byte) 0x03;
        System.arraycopy(x, 0, compressed, 1, 32);

        byte[] uncompressed = new byte[65];
        uncompressed[0] = 4; //first byte is 0x04 for uncompressed key
        System.arraycopy(x, 0, uncompressed, 1, 32);
        System.arraycopy(y, 0, uncompressed, 1+32, 32);

        return new AddressHashes(keyDigest(compressed), keyDigest(uncompressed));
    }

    private static byte[] make32(byte[] x) {
        byte[] newArr = new byte[32];
        if (x.length > 32){
            System.arraycopy(x, 1, newArr, 0, 32);
        } else {
            System.arraycopy(x, 0, newArr, 32-x.length, x.length);
        }
        return newArr;
    }

    private static boolean isEven(BigInteger i){
        return !i.testBit(0);
    }

    private static byte[] keyDigest(byte[] key){
        byte[] h1 = DigestUtils.sha256(key);
        RIPEMD160Digest ripemd160 = new RIPEMD160Digest();
        ripemd160.update(h1, 0, h1.length);
        byte[] addressHash = new byte[ripemd160.getDigestSize()];
        ripemd160.doFinal(addressHash, 0);
        return addressHash;
    }
}
