package org.schleger.btc_collider.searchspace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileSearchSpaceProviderTest {

    @Disabled
    @Test
    public void createNewFile() throws Exception{
        Path path = Path.of("searchspace","space.test.txt");
        Assertions.assertFalse(Files.exists(path));

        FileSearchSpaceProvider p = new FileSearchSpaceProvider(path);
        SearchSpace searchSpace = p.nextSearchSpace();
        Assertions.assertNotNull(searchSpace);
        Assertions.assertTrue(Files.exists(path));

        Files.delete(path);
    }

    @Test
    public void readExisting() throws IOException {
        Path path = Path.of("searchspace","space.existing.txt");
        Assertions.assertTrue(Files.exists(path));

        FileSearchSpaceProvider p = new FileSearchSpaceProvider(path);
        SearchSpace s = p.nextSearchSpace();
        Assertions.assertEquals(10, s.getFromInclusive().intValue());
        Assertions.assertEquals(500010, s.getToExclusive().intValue());
    }

    @Test
    public void canUpdate() throws IOException {
        Path path = Path.of("searchspace","space.update.txt");
        Files.write(path, "4-b".getBytes(StandardCharsets.UTF_8));

        FileSearchSpaceProvider p = new FileSearchSpaceProvider(path);
        SearchSpace s = p.nextSearchSpace();
        Assertions.assertEquals(11, s.getFromInclusive().intValue());

        p.done(s);

        List<String> lines = Files.readAllLines(path);
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("4-7a12b", lines.get(0));
    }

    @Test
    public void canCorrectlyMerge() throws IOException {
        Path path = Path.of("searchspace","space.update.txt");
        Files.write(path, "4-b".getBytes(StandardCharsets.UTF_8));

        FileSearchSpaceProvider p = new FileSearchSpaceProvider(path);
        SearchSpace s1 = p.nextSearchSpace();
        Assertions.assertEquals("b", s1.getFromInclusive().toString(16));
        Assertions.assertEquals("7a12b", s1.getToExclusive().toString(16));

        SearchSpace s2 = p.nextSearchSpace();
        Assertions.assertEquals("7a12b", s2.getFromInclusive().toString(16));
        Assertions.assertEquals("f424b", s2.getToExclusive().toString(16));

        p.done(s2);
        List<String> lines = Files.readAllLines(path);
        Assertions.assertEquals(2, lines.size());
        Assertions.assertEquals("4-b", lines.get(0));
        Assertions.assertEquals("7a12b-f424b", lines.get(1));

        p.done(s1);
        lines = Files.readAllLines(path);
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("4-f424b", lines.get(0));
    }
}
