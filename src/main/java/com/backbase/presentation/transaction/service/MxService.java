package com.backbase.presentation.transaction.service;

import static com.backbase.presentation.transaction.Endpoints.DIRECT_MX_CATEGORY_ENRICH;
import static com.backbase.presentation.transaction.util.RequestUtil.copyRequestList;

import com.backbase.buildingblocks.backend.internalrequest.InternalRequest;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.presentation.transaction.model.MxTransaction;
import com.backbase.presentation.transaction.model.MxTransactions;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsPatchRequestBody;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsPostRequestBody;
import com.backbase.presentation.transaction.routes.CategoriseAndDescribeRoute;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.Consume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class MxService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriseAndDescribeRoute.class);

    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private final String categorizeEndpoint;
    private final String MX_ERROR = "Unable to categorize transaction StatusCode={}, Message={}";

    public MxService(
      @Value("${backbase.mx.content-type}") String contentType,
      @Value("${backbase.mx.apiKey}") String apiKey,
      @Value("${backbase.mx.clientId}") String clientId,
      @Value("${backbase.mx.url}") String mxUrl) {
        this.categorizeEndpoint = mxUrl + "/" + clientId + "/categorize_and_describe";
        this.restTemplate = new RestTemplate();

        headers = new HttpHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Accept", contentType);
        headers.set("MD-API-Key", apiKey);
    }

    @Consume(uri = DIRECT_MX_CATEGORY_ENRICH)
    public InternalRequest<List<TransactionsPatchRequestBody>> enrich(InternalRequest<List<TransactionsPostRequestBody>> request) {
        MxTransactions mxTransactions = new MxTransactions();
        mxTransactions.setTransaction(request.getData().stream().map(item ->
          transform(item)).collect(Collectors.toList()));

        HttpEntity<MxTransactions> mxRequest = new HttpEntity<>(mxTransactions, headers);
        try {
            ResponseEntity<MxTransactions> response = restTemplate
              .postForEntity(categorizeEndpoint, mxRequest, MxTransactions.class);
            return copyRequestList(request,createPatchRequest(request.getData(), response.getBody()));
        } catch (HttpStatusCodeException exception) {
            int statusCode = exception.getStatusCode().value();
            LOGGER.error(MX_ERROR, statusCode, exception.getLocalizedMessage());
        } catch (Exception ex) {
            LOGGER.error(MX_ERROR, 500, ex.getLocalizedMessage());
        }
        throw new BadRequestException();
    }

    /**
     * Create update request for transaction categorized by MX.
     * @param transactionsPostRequestBodies - Transactions to be added
     * @param mxResponse - MX enriched transactions
     * @return - Transaction Patch Request
     */
    private List<TransactionsPatchRequestBody> createPatchRequest(List<TransactionsPostRequestBody> transactionsPostRequestBodies,
      MxTransactions mxResponse) {
        List<TransactionsPatchRequestBody> transactionsPatchRequestBodies = mxResponse.getTransaction().stream().map(
          item -> new TransactionsPatchRequestBody()
            .withId(item.getId())
            .withArrangementId(transactionsPostRequestBodies.stream().filter(
              arr -> arr.getExternalId().equals(item.getId())).findFirst().get().getArrangementId())
            .withCategory(item.getCategory())).collect(Collectors.toList());
        return transactionsPatchRequestBodies;
    }

    private MxTransaction transform(TransactionsPostRequestBody transactionsPostRequestBody) {
        MxTransaction mxTransaction = new MxTransaction();
        mxTransaction.setId(transactionsPostRequestBody.getExternalId());
        mxTransaction.setDescription(transactionsPostRequestBody.getDescription());
        mxTransaction.setAmount(transactionsPostRequestBody.getAmount());
        mxTransaction.setType(MxTransaction.Type.fromValue(transactionsPostRequestBody.getCreditDebitIndicator().name()));
        return mxTransaction;
    }

}
