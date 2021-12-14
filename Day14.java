package com.mim.gwam.cps.auth.advent.a14;

import io.vavr.Tuple;
import io.vavr.collection.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class Day14 {

    String firstLetter;
    Map<String, Character> rules = new HashMap<>();
    Map<String, Long> pairs = new HashMap<>();

    @BeforeEach
    public void init() throws IOException {
        Files.lines(Path.of("C:\\projects\\maark-bb\\_cps_\\gwam_cps_service_auth\\src\\test\\java\\com\\mim\\gwam\\cps\\auth\\advent\\a14\\input.txt"))
            .forEach(l -> {

                // read rule
                if (StringUtils.contains(l, "->")) {
                    String[] dir = StringUtils.split(l, " -> ");
                    rules.put(dir[0], dir[1].toCharArray()[0]);
                }

                // read template
                else if (StringUtils.isNotBlank(l)) {
                    for (int i = 0; i < l.length() - 1; i++) {
                        var pair = StringUtils.substring(l, i, i + 2);
                        increase(pairs, 1L, pair);
                    }
                    firstLetter = l.substring(0, 1);
                }
            });
    }

    @Test
    public void task2() {

        // do 40 iterations
        Stream.range(0, 40).forEach(i -> step());

        // count letters, only second letter of pair
        var result =
            pairs.entrySet().stream()
                .map(e -> Tuple.of(StringUtils.substring(e.getKey(), 1, 2), e.getValue()))
                .collect(Collectors.groupingBy(t -> t._1, Collectors.summingLong(t -> t._2)));

        // add first letter
        result.computeIfPresent(firstLetter, (k, v) -> v + 1);

        // sort letters
        var counts = result
            .entrySet().stream()
            .sorted(Entry.comparingByValue())
            .collect(Collectors.toList());

        log.info("counts: {}", counts);
        log.info("result: {}", counts.get(counts.size() - 1).getValue() - counts.get(0).getValue());
    }

    private void step() {
        Map<String, Long> pairs2 = new HashMap<>();
        pairs.forEach((pair, value) -> {
            var newPol = rules.get(pair);

            String newPair1 = new String(new char[]{pair.charAt(0), newPol});
            increase(pairs2, value, newPair1);

            String newPair2 = new String(new char[]{newPol, pair.charAt(1)});
            increase(pairs2, value, newPair2);
        });
        pairs = pairs2;
    }

    private void increase(Map<String, Long> map, Long howMuch, String key) {
        map.put(key,
            Optional.ofNullable(map.get(key))
                .map(old -> old + howMuch)
                .orElse(howMuch));
    }


}
