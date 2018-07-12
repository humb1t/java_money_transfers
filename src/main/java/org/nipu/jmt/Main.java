package org.nipu.jmt;

import io.javalin.Javalin;
import org.nipu.jmt.account.*;

/**
 * Main class for application start.
 *
 * @author Nikita_Puzankov
 */
public class Main {
    private static final String INDEX_MESSAGE = "{api_endpoints:[]}";

    public static void main(String[] args) {
        Accounts accounts = new RestAccounts(
                new SyncAccounts(
                        new ValidAccounts(
                                new InMemoryAccounts()
                        )
                )
        );
        Javalin application = Javalin.start(8080);

        //Mapping Section
        application.get("/", context -> context.result(INDEX_MESSAGE));
        //TODO: move inside accounts/RestAccounts.java
        application.get("/accounts", context -> context.json(accounts.findAll()));

        //Error Handling Section
        application.error(404, context -> context.result("Something went wrong"));
    }

}
