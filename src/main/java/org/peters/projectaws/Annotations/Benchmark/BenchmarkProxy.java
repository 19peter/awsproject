package org.peters.projectaws.Annotations.Benchmark;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unchecked")
public class BenchmarkProxy {
    private static final Logger logger = LogManager.getLogger(BenchmarkProxy.class);

    public static <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class[] { interfaceClass },
            (proxy, method, args) -> {
                // Get the actual method from the target class that implements the interface
                Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                if (targetMethod.isAnnotationPresent(Benchmark.class)) {
                    long startTime = System.currentTimeMillis();
                    Object result = method.invoke(target, args);
                    long endTime = System.currentTimeMillis();
                    logger.info( "<" + targetMethod.getDeclaringClass().getSimpleName() + ">: " + " Method(" + method.getName() + ") took " + (endTime - startTime) + "ms");
                    return result;
                } else {
                    return method.invoke(target, args);
                }
            }
        );
    }
}
