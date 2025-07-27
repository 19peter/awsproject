package org.peters.projectaws.Helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.dtos.Response.S3.GetDataResponseDto;

public class AppMethods {
    private static final Logger logger = LogManager.getLogger(AppMethods.class);
    public static GetDataResponseDto runMethodOne(String data) {

            logger.info("<AppMethods>: Running method one");
            logger.info("<AppMethods>: Processing method one data: " + data);
 
            logger.info("<AppMethods>: Method one completed");
            return new GetDataResponseDto();
     
    }

    public static GetDataResponseDto runMethodTwo(String data) {
  
            logger.info("<AppMethods>: Running method two");
            logger.info("<AppMethods>: Processing method two data: " + data);
      
            logger.info("<AppMethods>: Method two completed");
            return new GetDataResponseDto();
    }
  
}
