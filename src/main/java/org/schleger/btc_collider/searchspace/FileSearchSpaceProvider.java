package org.schleger.btc_collider.searchspace;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileSearchSpaceProvider implements SearchSpaceProvider {

    private final BigInteger interval = BigInteger.valueOf(500_000);
    private final Path path;
    private TreeSet<SearchSpace> done = new TreeSet<>();
    private final ArrayList<SearchSpace> pending = new ArrayList<>();

    public FileSearchSpaceProvider(Path path) throws IOException {
        this.path = path;
        init();
    }

    private void init() throws IOException{
        if (Files.exists(path)){
            List<String> lines = Files.readAllLines(path);
            for (String line : lines){
                SearchSpace searchSpace = SearchSpace.fromLine(line);
                done.add(searchSpace);
            }
        }
    }

    @Override
    public SearchSpace nextSearchSpace() throws IOException {
        SearchSpace s;

        if (!pending.isEmpty()){
            //continue from last search space
            SearchSpace lastSearchSpace = pending.get(pending.size() - 1);
            BigInteger fromInclusive = lastSearchSpace.getToExclusive();
            s = new SearchSpace(fromInclusive, fromInclusive.add(interval));
        } else if (!done.isEmpty()){
            SearchSpace first = done.first();
            BigInteger fromInclusive = first.getToExclusive();
            s = new SearchSpace(fromInclusive, fromInclusive.add(interval));
        } else {
            SearchSpace r = SearchSpace.random();
            s = new SearchSpace(r.getFromInclusive(), r.getFromInclusive().add(interval));
        }

        pending.add(s);
        return s;
    }

    private void write(SearchSpace s) throws IOException {
        Files.write(path, s.toString().getBytes());
    }

    @Override
    public void done(SearchSpace searchSpace) throws IOException {
        pending.remove(searchSpace);

        add(searchSpace);

        try(BufferedWriter w = Files.newBufferedWriter(path)){
            for (SearchSpace s : done){
                w.write(s.toString());
                w.newLine();
            }
        }
    }

    private void add(SearchSpace searchSpace) {
        boolean mergeLower = false;
        boolean mergeHigher = false;

        SearchSpace lower = done.lower(searchSpace);
        if (lower!=null && lower.mergeable(searchSpace)){
            done.remove(lower);
            mergeLower = true;
        }

        SearchSpace higher = done.higher(searchSpace);
        if (higher!=null && higher.mergeable(searchSpace)){
            done.remove(higher);
            mergeHigher = true;
        }

        if (mergeLower && mergeHigher){
            SearchSpace merged = new SearchSpace(lower.getFromInclusive(), higher.getToExclusive());
            done.add(merged);
        } else if (mergeLower){
            SearchSpace merged = lower.merge(searchSpace);
            done.add(merged);
        } else if (mergeHigher){
            SearchSpace merged = higher.merge(searchSpace);
            done.add(merged);
        } else {
            done.add(searchSpace);
        }
    }
}
