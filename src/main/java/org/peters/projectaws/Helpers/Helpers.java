package org.peters.projectaws.Helpers;

import org.peters.projectaws.dtos.Response.S3.GetDataResponseDto;

import java.util.concurrent.ThreadLocalRandom;

public class Helpers {

    public static int delayDuration = 3000;

    public static void simulateDelay() throws InterruptedException {
        int sleepTime = ThreadLocalRandom.current().nextInt(3000, 5001);
        System.out.println("Sleep Time: " + sleepTime);
        Thread.sleep(sleepTime);
    }

    public static GetDataResponseDto populateResponse(String code, String data, boolean isPresent) {
        GetDataResponseDto response = new GetDataResponseDto();
        response.setData(data);
        response.setCode(code);
        response.setIsPresent(isPresent);
        return response;

    }
}
