package org.peters.projectaws.Helpers;

import org.apache.logging.log4j.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggingUtils {
    private static final Map<String, String> requestContext = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    public static void startRequestTrace(String reqId) {
        requestId.set(reqId);
        requestContext.put(reqId, Thread.currentThread().getName());
    }

    public static void endRequestTrace(String reqId) {
        requestContext.remove(reqId);
        requestId.remove();
    }

    public static void logWithContext(Logger logger, String format, Object... args) {
        String ctx = requestId.get() != null ? 
            String.format("[Req:%s][Thread:%s] ", requestId.get(), Thread.currentThread().getName()) : 
            String.format("[Thread:%s] ", Thread.currentThread().getName());
        logger.info(ctx + format, args);
    }

    public static void logErrorWithContext(Logger logger, String message, Throwable t) {
        String ctx = requestId.get() != null ? 
            String.format("[Req:%s][Thread:%s] ", requestId.get(), Thread.currentThread().getName()) : 
            String.format("[Thread:%s] ", Thread.currentThread().getName());
        logger.error(ctx + message, t);
    }

    public static String getCurrentRequestId() {
        return requestId.get();
    }
}