package org.schleger.btc_collider.collider;

import org.schleger.btc_collider.searchspace.SearchSpace;

import java.math.BigInteger;
import java.util.List;

public class ColliderResult {

    private SearchSpace searchSpace;
    private List<BigInteger> collisions;

    public ColliderResult(SearchSpace searchSpace, List<BigInteger> collisions){
        this.searchSpace = searchSpace;
        this.collisions = collisions;
    }

    public SearchSpace getSearchSpace(){
        return searchSpace;
    }

    public List<BigInteger> getCollisions(){
        return collisions;
    }
}
