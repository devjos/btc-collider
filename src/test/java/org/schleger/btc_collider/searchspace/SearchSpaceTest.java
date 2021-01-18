package org.schleger.btc_collider.searchspace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class SearchSpaceTest {

    @Test
    public void setFields(){
        BigInteger from = BigInteger.ONE;
        BigInteger to = BigInteger.TEN;
        SearchSpace s = new SearchSpace(from, to);
        Assertions.assertEquals(from, s.getFromInclusive());
        Assertions.assertEquals(to, s.getToExclusive());
    }

    @Test
    public void fromEqualsTo(){
        new SearchSpace(BigInteger.TEN, BigInteger.TEN);
    }

    @Test
    public void fromBiggerThanTo(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new SearchSpace(BigInteger.TEN, BigInteger.ONE);
        });
    }

    @Test
    public void toStringHex(){
        SearchSpace s = new SearchSpace(BigInteger.ONE, BigInteger.TEN);
        Assertions.assertEquals("1-a", s.toString());
    }

    @Test
    public void fromLine(){
        SearchSpace s = SearchSpace.fromLine("1-a");
        Assertions.assertEquals(BigInteger.ONE, s.getFromInclusive());
        Assertions.assertEquals(BigInteger.TEN, s.getToExclusive());
    }

    @Test
    public void random(){
        SearchSpace s = SearchSpace.random();
        Assertions.assertEquals(s.getFromInclusive(), s.getToExclusive());
    }

    @Test
    public void merge(){
        SearchSpace s1 = new SearchSpace(BigInteger.ONE, BigInteger.TWO);
        SearchSpace s2 = new SearchSpace(BigInteger.TWO, BigInteger.TEN);

        Assertions.assertTrue(s1.mergeable(s2));
        Assertions.assertTrue(s2.mergeable(s1));

        SearchSpace merged = s1.merge(s2);
        Assertions.assertEquals(BigInteger.ONE, merged.getFromInclusive());
        Assertions.assertEquals(BigInteger.TEN, merged.getToExclusive());

        merged = s2.merge(s1);
        Assertions.assertEquals(BigInteger.ONE, merged.getFromInclusive());
        Assertions.assertEquals(BigInteger.TEN, merged.getToExclusive());
    }
}
