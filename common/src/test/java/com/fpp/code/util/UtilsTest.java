package com.fpp.code.util;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 15:20:49
 */
class UtilsTest {

    @org.junit.jupiter.api.Test
    void removeSuffixEmpty() {
        final String s = Utils.removeSuffixEmpty("a\n     ");
        Assertions.assertEquals("a\n",s);
    }
}