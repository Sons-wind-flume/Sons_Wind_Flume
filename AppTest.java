package org.jfree.starter;

import java.awt.GraphicsEnvironment;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 */
public class AppTest {
    
    @Test
    public void testSomething() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Requires a graphical display");
        App app = new App("JFreeApp Starter App");
        assertNotNull(app);
        app.dispose();
    }
}
