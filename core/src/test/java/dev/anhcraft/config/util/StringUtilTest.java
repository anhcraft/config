package dev.anhcraft.config.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {
    @Test
    public void testFastSplit() {
        assertEquals(List.of("3", ""), StringUtil.fastSplit("3|", '|'));
        assertEquals(List.of("3", "", ""), StringUtil.fastSplit("3||", '|'));
        assertEquals(List.of("", "3", "", ""), StringUtil.fastSplit("|3||", '|'));
        assertEquals(List.of("f", "o", "b", "a", "r"), StringUtil.fastSplit("f o b a r", ' '));
        assertEquals(List.of("", "f", "o", "b", "a", "r"), StringUtil.fastSplit(" f o b a r", ' '));
        assertEquals(List.of("", "f", "", "o", "b", "a", "r"), StringUtil.fastSplit(" f  o b a r", ' '));
    }

    @Test
    public void testSplitCamelCase() {
        assertEquals(List.of("foo", "bar"), StringUtil.splitCamelCase("fooBar"));
        assertEquals(List.of("foo", "bar123"), StringUtil.splitCamelCase("fooBar123"));
        assertEquals(List.of("hello", "world"), StringUtil.splitCamelCase("HelloWorld"));
    }
}
