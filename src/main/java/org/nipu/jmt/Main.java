package org.nipu.jmt;

import io.javalin.ApiBuilder;
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
        MessageBundle messageBundle = new MessageBundle();
        Accounts accounts = new RestAccounts(
                new SyncAccounts(
                        new ValidAccounts(
                                new InMemoryAccounts()
                        )
                )
        );
        Javalin application = Javalin.start(8080);

        //Mapping Section
        application.get("/", context -> context.result(messageBundle.get("INDEX_MESSAGE")));
        //TODO: move inside accounts/RestAccounts.java
        application.routes(
                () -> ApiBuilder.path("accounts",
                        () -> {
                            ApiBuilder.get(context -> context.json(accounts.findAll()));//TODO: RestAccounts::findAll
                            ApiBuilder.get(":id", context -> {
                                final Account account = accounts.find(Long.valueOf(context.param("id")));
                                context.json(account);
                            });
                            ApiBuilder.post(context ->
                                    {
                                        final JsonAsAccount jsonAsAccount = context.bodyAsClass(JsonAsAccount.class);
                                        AtomicAccount atomicAccount = new AtomicAccount(
                                                jsonAsAccount.getCustomerName()
                                        );
                                        final Account account = accounts.add(atomicAccount);
                                        context.json(account);
                                        context.status(201);
                                    }
                            );
                        }
                )
        );

        //Error Handling Section
        application.error(404, context -> context.result(messageBundle.get("404_ERROR_MESSAGE")));
        application.exception(Exception.class, (e, context) ->
                context.result(messageBundle.get("INTERNAL_ERROR_MESSAGE", e.getLocalizedMessage())
                )
        );
    }

}
