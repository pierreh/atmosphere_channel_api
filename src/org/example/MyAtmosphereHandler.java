package org.example;

import java.io.IOException;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author p.havelaar
 */
@AtmosphereHandlerService(path="/atmosphere")
public class MyAtmosphereHandler extends AbstractReflectorAtmosphereHandler {
    
    private final Logger logger = LoggerFactory.getLogger("MyAtmosphereHandler");

    public void onRequest(AtmosphereResource ar) throws IOException {
        logger.info("handling atmosphere request");
        if (ar.getRequest().getMethod() == "GET") {
            ar.suspend(-1, false);
        }
    }

    public void onStateChange(AtmosphereResourceEvent are) throws IOException {
        logger.info("received atmosphere event");
        super.onStateChange(are);
    }

    public void destroy() {
        logger.info("destroyed");
    }
    
}
