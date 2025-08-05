package org.peters.projectaws.Helpers;


import java.util.concurrent.ThreadLocalRandom;

import org.peters.projectaws.dtos.Response.Response;

public class Helpers {

    public static int delayDuration = 3000;

    public static void simulateDelay() throws InterruptedException {
        int sleepTime = ThreadLocalRandom.current().nextInt(3000, 5001);
        System.out.println("Sleep Time: " + sleepTime);
        Thread.sleep(sleepTime);
    }

    public static Response populateResponse(String code, String data, boolean isPresent) {
        Response response = new Response(code);
        response.setData(data);
        response.setIsPresent(isPresent);
        return response;

    }
}
