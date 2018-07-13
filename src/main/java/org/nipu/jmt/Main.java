package org.nipu.jmt;

import io.javalin.ApiBuilder;
import io.javalin.Javalin;
import io.javalin.event.EventType;
import org.nipu.jmt.account.*;
import org.nipu.jmt.transaction.BrokerProcess;
import org.nipu.jmt.transaction.JsonAsTransactionAmount;
import org.nipu.jmt.transaction.Transaction;
import org.nipu.jmt.transaction.TransactionsQueue;

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
        final TransactionsQueue transactionsQueue = new TransactionsQueue(100);
        BrokerProcess brokerProcess = new BrokerProcess(
                transactionsQueue,
                accounts);

        Javalin.create()
                .event(EventType.SERVER_STARTED, event -> new Thread(brokerProcess).start())
                .event(EventType.SERVER_STOPPED, event -> brokerProcess.disable())
                //Mapping Section
                //TODO: move inside accounts/RestAccounts.java
                .routes(
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
                                    ApiBuilder.put(":fromId/pay/:toId", context ->
                                            transactionsQueue.offer(
                                                    new Transaction(
                                                            Long.valueOf(context.param("fromId")),
                                                            Long.valueOf(context.param("toId")),
                                                            context.bodyAsClass(JsonAsTransactionAmount.class).getAmount()
                                                    )
                                            )
                                    );
                                }
                        )
                )
                //Additional configuration
                .disableStartupBanner()
                .enableRouteOverview("/index")
                .enableDynamicGzip()
                .enableStandardRequestLogging()
                //Error Handling Section
                .error(404, context -> context.result(messageBundle.get("404_ERROR_MESSAGE")))
                .exception(Exception.class, (e, context) ->
                        context.result(messageBundle.get("INTERNAL_ERROR_MESSAGE", e.getLocalizedMessage())
                        )
                )
                .port(8080)
                .start();
    }

}
