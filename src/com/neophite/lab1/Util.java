package com.neophite.lab1;

import java.util.Random;
import java.util.Set;

public class Util {
    public static <E> E getRandomSetElement(Set<E> set) {
        return set.stream().skip(new Random().nextInt(set.size())).findFirst().orElse(null);
    }
}
