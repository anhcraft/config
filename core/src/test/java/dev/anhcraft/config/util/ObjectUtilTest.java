package dev.anhcraft.config.util;

import dev.anhcraft.config.context.ElementScope;
import dev.anhcraft.config.context.PathType;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectUtilTest {
    @Test
    public void testNewInstance() {
        assertDoesNotThrow(() -> ObjectUtil.newInstance(ArrayList.class));
        assertDoesNotThrow(() -> ObjectUtil.newInstance(ElementScope.class));
        assertDoesNotThrow(() -> ObjectUtil.newInstance(PathType.class));
        assertDoesNotThrow(() -> ObjectUtil.newInstance(Integer.class));
        assertDoesNotThrow(() -> ObjectUtil.newInstance(Object.class));
        assertThrows(InstantiationException.class, () -> ObjectUtil.newInstance(List.class));
        assertThrows(InstantiationException.class, () -> ObjectUtil.newInstance(AbstractList.class));
        assertThrows(InstantiationException.class, () -> ObjectUtil.newInstance(int.class));
    }
}
