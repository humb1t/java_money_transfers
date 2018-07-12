package org.nipu.jmt;

import io.javalin.Javalin;

/**
 * Main class for application start.
 *
 * @author Nikita_Puzankov
 */
public class Main {
    private static final String INDEX_MESSAGE = "{api_endpoints:[]}";

    public static void main(String[] args) {
        Javalin application = Javalin.start(8080);
        application.get("/", context -> context.result(INDEX_MESSAGE));
    }

}
