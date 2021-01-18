package org.schleger.btc_collider.searchspace;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class SearchSpace implements Comparable<SearchSpace>{

    private final BigInteger fromInclusive;
    private final BigInteger toExclusive;

    public SearchSpace(BigInteger fromInclusive, BigInteger toExclusive){
        if (fromInclusive.signum() <= 0 || toExclusive.signum() <= 0){
            throw new IllegalArgumentException("fromInclusive and toExclusive must be > 0");
        }
        if (fromInclusive.compareTo(toExclusive) > 0){
            throw new IllegalArgumentException("fromInclusive is bigger than toExclusive");
        }
        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
    }

    public static SearchSpace fromLine(String line) {
        String[] splitted = line.split("-");
        if(splitted.length!=2){
            throw new IllegalArgumentException("Unsupported line");
        }
        BigInteger startInclusive = new BigInteger(splitted[0], 16);
        BigInteger endExclusive = new BigInteger(splitted[1], 16);
        return new SearchSpace(startInclusive, endExclusive);
    }

    public static SearchSpace random() {
        SecureRandom r = new SecureRandom();
        byte[] bytes = new byte[32];
        r.nextBytes(bytes);
        BigInteger num = new BigInteger(1, bytes);
        return new SearchSpace(num, num);
    }

    public BigInteger getFromInclusive(){
        return fromInclusive;
    }

    public BigInteger getToExclusive(){
        return toExclusive;
    }

    public boolean mergeable(SearchSpace other){
        int compared = this.compareTo(other);
        if (compared < 0){
            return this.toExclusive.compareTo(other.fromInclusive) >= 0;
        } else if (compared == 0){
            return true;
        } else {
            return this.fromInclusive.compareTo(other.toExclusive) <= 0;
        }
    }

    public SearchSpace merge(SearchSpace other) {
        int compared = this.compareTo(other);
        if (compared < 0){
            if (this.toExclusive.compareTo(other.fromInclusive) >= 0){
                return new SearchSpace(this.fromInclusive, other.toExclusive);
            }
        } else if (compared == 0){
            return new SearchSpace(this.fromInclusive, this.toExclusive.max(other.toExclusive));
        } else {
            if (this.fromInclusive.compareTo(other.toExclusive) <= 0){
                return new SearchSpace(other.fromInclusive, this.toExclusive);
            }
        }
        throw new IllegalArgumentException("Cannot merge");
    }

    @Override
    public String toString(){
        return fromInclusive.toString(16) + "-" + toExclusive.toString(16);
    }


    @Override
    public int compareTo(SearchSpace o) {
        return fromInclusive.compareTo(o.getFromInclusive());
    }
}
