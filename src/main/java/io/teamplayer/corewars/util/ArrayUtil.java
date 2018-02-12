package io.teamplayer.corewars.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class to manipulating arrays
 */
public final class ArrayUtil {

    private ArrayUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /** Shuffle the contents of an array */
    public static void shuffle(Object[] array) {
        Object temp;

        for (int i = 0; i < array.length; i++) {
            int random = ThreadLocalRandom.current().nextInt(array.length);

            temp = array[random];
            array[random] = array[i];
            array[i] = temp;
        }
    }
}
