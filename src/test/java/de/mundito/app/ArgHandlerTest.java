package de.mundito.app;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerLed;
import de.mundito.args.ArgHandlerLight;
import de.mundito.args.ArgHandlerRegistry;
import de.mundito.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Not so much a unit test: more a kind of integration / regression test of the whole HotasCtrl App.
 */
public class ArgHandlerTest
{

    @Before
    public void setUp()
            throws Exception {
    }

    @After
    public void tearDown()
            throws Exception {
    }

    @Test
    public void testArgsNone() {
        Configuration configuration = new Configuration(ArgHandlerRegistry.readArgs());
        assertFalse(configuration.isValid());
    }

    @Test
    public void testArgsLightLed() {
        List<ArgHandler> argHandlers = ArgHandlerRegistry.readArgs("light", "all", "on", "led", "all", "on");
        Configuration configuration = new Configuration(argHandlers);
        assertTrue(configuration.isValid());

        assertEquals("Wrong number of argument handlers", 2, argHandlers.size());
        ArgHandler argHandler1 = argHandlers.get(0);
        ArgHandler argHandler2 = argHandlers.get(1);
        assertTrue("First argument is not 'light'", argHandler1 instanceof ArgHandlerLight);
        assertTrue("Second argument is not 'led'", argHandler2 instanceof ArgHandlerLed);
    }

}
