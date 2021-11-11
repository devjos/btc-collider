package org.schleger.btc_collider;

import gnu.trove.set.hash.TCustomHashSet;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.schleger.btc_collider.addresses.AddressesProvider;
import org.schleger.btc_collider.addresses.FileAddressesProvider;
import org.schleger.btc_collider.collider.ColliderCallable;
import org.schleger.btc_collider.collider.ColliderResult;
import org.schleger.btc_collider.collision.CollisionListener;
import org.schleger.btc_collider.collision.CountingCollisionListener;
import org.schleger.btc_collider.collision.FileCollisionListener;
import org.schleger.btc_collider.searchspace.FileSearchSpaceProvider;
import org.schleger.btc_collider.searchspace.RandomSearchSpaceProvider;
import org.schleger.btc_collider.searchspace.SearchSpace;
import org.schleger.btc_collider.searchspace.SearchSpaceProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Collider {

    private static final Logger LOG = LogManager.getLogger();
    private static final Path ADDRESSES_PATH = Path.of("addresses", "latest.txt.gz");
    private static final Path SEARCH_SPACE_PATH = Path.of("searchspace", "space.txt");

    private final int numThreads;
    private final int runtimeMinutes;
    private final boolean searchRandom;

    public Collider(int numThreads, int runtimeMinutes, boolean searchRandom){
        this.numThreads = numThreads;
        this.runtimeMinutes = runtimeMinutes;
        this.searchRandom = searchRandom;
    }

    public void run() throws IOException{
        LOG.info("Read " + ADDRESSES_PATH);
        AddressesProvider f = new FileAddressesProvider(ADDRESSES_PATH);
        TCustomHashSet<byte[]> addresses = f.provideAddresses();
        LOG.info("Read DONE");

        CountingCollisionListener countingCollisionListener = new CountingCollisionListener();
        List<CollisionListener> collisionListeners = List.of(countingCollisionListener, new FileCollisionListener("collisions"));

        LOG.info("Start collider on {} threads", numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        SearchSpaceProvider p = searchRandom ? new RandomSearchSpaceProvider() : new FileSearchSpaceProvider(SEARCH_SPACE_PATH);
        ArrayList<Future<ColliderResult>> tasks = new ArrayList<>(numThreads);

        long startSec = System.currentTimeMillis() / 1000;
        long currentSec = startSec;

        boolean keepRunning = true;

        do{
            keepRunning = keepRunning && currentSec - startSec <= runtimeMinutes * 60;
            if (keepRunning){
                while (tasks.size() < numThreads * 2 ){
                    //add new
                    SearchSpace searchSpace = p.nextSearchSpace();
                    ColliderCallable c = new ColliderCallable(addresses, searchSpace);
                    Future<ColliderResult> task = executorService.submit(c);
                    tasks.add(task);
                    LOG.debug("New task added: " + searchSpace);
                }
            } else {
                LOG.info("Shutdown soon. Remaining tasks: {}", tasks.size());
            }

            try {
                Thread.sleep(20_000);
            } catch (InterruptedException e) {
                //silently ignore
                LOG.info("Got interrupted, preparing to shutdown", e);
                keepRunning = false;
            }

            Iterator<Future<ColliderResult>> iterator = tasks.iterator();
            while (iterator.hasNext()){
                Future<ColliderResult> task = iterator.next();
                if (!task.isDone()) continue;

                iterator.remove();

                ColliderResult result = null;
                try {
                    result = task.get();
                } catch (InterruptedException e) {
                    LOG.error(e);
                } catch (ExecutionException e) {
                    LOG.error(e);
                }
                if (result != null){
                    result.getCollisions().forEach( key -> {
                        String keyStr = key.toString(16);
                        LOG.info("Found key: " + keyStr);
                        collisionListeners.forEach( c -> c.collisionEvent(key));
                    });
                    p.done(result.getSearchSpace());
                }

            }

            currentSec = System.currentTimeMillis() / 1000;
        }while(!tasks.isEmpty());

        LOG.info("Shutdown. Found {} collisions.", countingCollisionListener.getCollisionCount());
        executorService.shutdown();

        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());


        Options options = new Options();

        Option time = new Option("t", "time", true, "runtime in minutes");
        time.setRequired(true);
        options.addOption(time);

        Option threadCount = new Option("n", "threads", true, "thread count");
        threadCount.setRequired(true);
        options.addOption(threadCount);

        Option random = new Option("r", "random", false, "search randomly");
        random.setRequired(false);
        options.addOption(random);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("", e);
            formatter.printHelp("Collider", options);

            System.exit(1);
        }

        int runtimeMinutes = Integer.parseInt(cmd.getOptionValue("t"));
        int numThreads = Integer.parseInt(cmd.getOptionValue("n"));
        boolean searchRandom = cmd.hasOption("r");

        Collider c = new Collider(numThreads, runtimeMinutes, searchRandom);
        c.run();
    }


}
