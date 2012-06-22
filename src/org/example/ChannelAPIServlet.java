package org.example;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.atmosphere.cpr.AtmosphereServlet;

/**
 *
 * @author p.havelaar
 */
public class ChannelAPIServlet extends AtmosphereServlet {

    @Override
    public void init(ServletConfig sc) throws ServletException {
        framework().setAsyncSupport(new ChannelApiAsyncSupport(framework().getAtmosphereConfig()));
        super.init(sc);
        
        framework().addAtmosphereHandler("/atmosphere"
                , new MyAtmosphereHandler());
    }
    
}
