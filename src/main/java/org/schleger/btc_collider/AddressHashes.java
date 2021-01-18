package org.schleger.btc_collider;

public class AddressHashes {

    private byte[] compressed;
    private byte[] uncompressed;

    public AddressHashes(byte[] compressed, byte[] uncompressed){
        this.compressed = compressed;
        this.uncompressed = uncompressed;
    }

    public byte[] getCompressed() {
        return compressed;
    }

    public byte[] getUncompressed() {
        return uncompressed;
    }
}
