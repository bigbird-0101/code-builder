package io.github.bigbird0101.code.fxui.common;

import io.github.bigbird0101.code.core.template.Template;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-25 22:23:22
 */
class ClassUtilTest {

    @Test
    void getAllClassByInterface() {
        ArrayList<Class<?>> allClassByInterface = ClassUtil.getAllClassByInterface(Template.class);
        assertFalse(allClassByInterface.isEmpty());
    }
}