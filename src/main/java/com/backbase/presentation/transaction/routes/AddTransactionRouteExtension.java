package com.backbase.presentation.transaction.routes;

import static com.backbase.presentation.transaction.Endpoints.SEDA_CATEGORIZE_AND_DESCRIBE;
import static com.backbase.presentation.transaction.routes.constants.RoutesEndpoints.DIRECT_CREATE_TRANSACTIONS_PERSISTENCE;

import org.apache.camel.ExchangePattern;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class AddTransactionRouteExtension extends CreateTransactionsRoute {

    @Override
    public void configure() {
        interceptSendToEndpoint(DIRECT_CREATE_TRANSACTIONS_PERSISTENCE)
          .to(ExchangePattern.InOnly, SEDA_CATEGORIZE_AND_DESCRIBE);
        super.configure();
    }

}
