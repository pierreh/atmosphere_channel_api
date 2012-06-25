package org.example;

import java.io.IOException;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author p.havelaar
 */
public class MyAtmosphereHandler extends AbstractReflectorAtmosphereHandler {
    
    private final Logger logger = LoggerFactory.getLogger("MyAtmosphereHandler");

    public void onRequest(AtmosphereResource ar) throws IOException {
        logger.info("handling atmosphere request");
        if (ar.getRequest().getMethod().equals("GET")) {
            ar.addEventListener(listener);
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
    
    private AtmosphereResourceEventListener listener = new AtmosphereResourceEventListener() {

        public void onSuspend(AtmosphereResourceEvent are) {
            logger.info("resource suspend " + are.getResource().uuid());
        }

        public void onResume(AtmosphereResourceEvent are) {
            logger.info("resource resume " + are.getResource().uuid());
        }

        public void onDisconnect(AtmosphereResourceEvent are) {
            logger.info("resource disconnect " + are.getResource().uuid());
        }

        public void onBroadcast(AtmosphereResourceEvent are) {
            logger.info("resource broadcast " + are.getResource().uuid());
        }

        public void onThrowable(AtmosphereResourceEvent are) {
            logger.error("resource error " + are.getResource().uuid() + "\n" + are.throwable());
        }
    };
    
}
