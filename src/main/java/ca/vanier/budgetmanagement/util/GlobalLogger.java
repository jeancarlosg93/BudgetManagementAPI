package ca.vanier.budgetmanagement.util;

import org.slf4j.Logger;


//this class is used to get the logger
//every class should use this class to get the logger
//this class is used to implement logging

public class GlobalLogger {
    private GlobalLogger() {
        // Prevent instantiation
    }

    public static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

    //methods to log info, debug, warn, and error messages
    public static void info(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }

    public static void info(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }

    public static void debug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }

    public static void warn(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).warn(message, args);
    }

    public static void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).error(message, throwable);
    }

    public static void error(Class<?> clazz, String message) {
        getLogger(clazz).error(message);
    }
}