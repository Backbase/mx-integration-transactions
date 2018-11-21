package com.backbase.presentation.transaction.routes;

import static com.backbase.presentation.transaction.Endpoints.CATEGORIZE_AND_DESCRIBE_ID;
import static com.backbase.presentation.transaction.Endpoints.DIRECT_MX_CATEGORY_ENRICH;
import static com.backbase.presentation.transaction.Endpoints.SEDA_CATEGORIZE_AND_DESCRIBE;
import static com.backbase.presentation.transaction.routes.constants.RoutesEndpoints.DIRECT_UPDATE_TRANSACTIONS_ENRICH;
import static com.backbase.presentation.transaction.routes.constants.RoutesEndpoints.DIRECT_UPDATE_TRANSACTIONS_PERSISTENCE;

import com.backbase.buildingblocks.backend.communication.extension.ExtensibleRouteBuilder;
import org.springframework.stereotype.Component;

/**
 * CategoriseAndDescribeRoute - InOnly operation to enrich transaction category and persist.
 */
@Component
public class CategoriseAndDescribeRoute extends ExtensibleRouteBuilder {

    public CategoriseAndDescribeRoute() {
        super(CATEGORIZE_AND_DESCRIBE_ID);
    }

    @Override
    public void configure() {
        from(SEDA_CATEGORIZE_AND_DESCRIBE)
          .to(DIRECT_MX_CATEGORY_ENRICH)
          .to(DIRECT_UPDATE_TRANSACTIONS_ENRICH)
          .to(DIRECT_UPDATE_TRANSACTIONS_PERSISTENCE);
    }

}
