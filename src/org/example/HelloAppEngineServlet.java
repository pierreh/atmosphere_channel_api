package org.example;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

public class HelloAppEngineServlet extends HttpServlet {
    
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
        req.getRequestDispatcher("index.jsp").include(req, resp);
	}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        for (Broadcaster b: BroadcasterFactory.getDefault().lookupAll()) {
            b.broadcast("Hello Atmosphere!");
        }
    }
    
    
}