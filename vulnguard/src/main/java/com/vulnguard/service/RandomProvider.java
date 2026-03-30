package com.vulnguard.service;

/**
 * Abstraction for Random to enable testing without mocking java.util.Random.
 * This interface allows us to inject mock implementations during testing,
 * avoiding issues with mocking final/internal JDK classes on Java 25+.
 */
public interface RandomProvider {
    /**
     * Returns a random int between 0 (inclusive) and bound (exclusive).
     *
     * @param bound the upper bound (exclusive)
     * @return a random int in the range [0, bound)
     */
    int nextInt(int bound);
}
