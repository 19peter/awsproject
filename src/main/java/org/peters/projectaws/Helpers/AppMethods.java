package org.peters.projectaws.Helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.dtos.Response.Response;

public class AppMethods {
    private static final Logger logger = LogManager.getLogger(AppMethods.class);
    public static Response runMethodOne(String data) {

            logger.info("<AppMethods>: Running method one");
            logger.info("<AppMethods>: Processing method one data: " + data);
 
            logger.info("<AppMethods>: Method one completed");
            return new Response("200");
     
    }

    public static Response runMethodTwo(String data) {
  
            logger.info("<AppMethods>: Running method two");
            logger.info("<AppMethods>: Processing method two data: " + data);
      
            logger.info("<AppMethods>: Method two completed");
            return new Response("200");
    }
  
}
