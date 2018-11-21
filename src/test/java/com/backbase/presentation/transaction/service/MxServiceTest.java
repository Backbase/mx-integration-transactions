package com.backbase.presentation.transaction.service;


import com.backbase.buildingblocks.backend.internalrequest.DefaultInternalRequestContext;
import com.backbase.buildingblocks.backend.internalrequest.InternalRequest;
import com.backbase.presentation.transaction.Application;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsPatchRequestBody;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsPostRequestBody;
import com.backbase.rest.spec.common.types.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {Application.class})
public class MxServiceTest {

    /**
     * In-Order to execute the test - following perperties required:
     *
     * backbase.mx.content-type=application/vnd.mx.deduction.v1+json
     * backbase.mx.apiKey=...
     * backbase.mx.url=https://int-deduction.moneydesktop.com
     * backbase.mx.clientId=...
     */
    @Autowired
    private MxService mxService;

    @Test
    public void testCategorize() throws JsonProcessingException {
        InternalRequest<List<TransactionsPostRequestBody>> listInternalRequest = new InternalRequest<>();
        listInternalRequest.setInternalRequestContext(new DefaultInternalRequestContext());

        List<com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsPostRequestBody> requestData = new ArrayList<>();
        requestData.add(transactionsPostRequestBodyWithTransactionAmount(1)
          .withSequenceNumber("12345678901234567890"));
        requestData.add(transactionsPostRequestBodyWithTransactionAmount(2)
          .withDescription("Connextion"));

        listInternalRequest.setData(requestData);
        InternalRequest<List<TransactionsPatchRequestBody>> response = mxService.enrich(listInternalRequest);
        Assert.assertEquals(2,response.getData().size());
        Assert.assertEquals("ALCOHOL_BARS",response.getData().get(0).getCategory());
        Assert.assertEquals("UNCATEGORIZED",response.getData().get(1).getCategory());
    }

    public static TransactionsPostRequestBody transactionsPostRequestBodyWithTransactionAmount(int index) {
        return new TransactionsPostRequestBody()
          .withArrangementId("arrangementId_" + index)
          .withExternalId("externalId_" + index)
          .withExternalArrangementId("externalArrangementId_" + index)
          .withReference("reference_" + index)
          .withDescription("BEER BAR 65000000764SALT LAKE C" + index)
          .withTypeGroup("Payment")
          .withType("Faster payment (UK)")
          .withCategory("category_" + index)
          .withBookingDate(new Date())
          .withValueDate(new Date())
          .withTransactionAmountCurrency(new Currency().withAmount(BigDecimal.ONE).withCurrencyCode("EUR"))
          .withCreditDebitIndicator(
            com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionItem.CreditDebitIndicator.DBIT)
          .withInstructedAmount(BigDecimal.ONE)
          .withInstructedCurrency("EUR")
          .withCurrencyExchangeRate(BigDecimal.ONE)
          .withCounterPartyName("GPN_" + index)
          .withCounterPartyAccountNumber("" + index)
          .withCounterPartyBIC("BIC_")
          .withCounterPartyCountry("C" + index)
          .withCounterPartyBankName("cpBankName_" + index)
          .withCreditorId("creditorId_" + index)
          .withMandateReference("mandateReference_" + index)
          .withBillingStatus("PENDING")
          .withCheckSerialNumber(new BigDecimal(index));
    }
}
