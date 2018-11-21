package com.backbase.presentation.transaction.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MxTransactions {

    @JsonProperty("transactions")
    private List<MxTransaction> transaction = new ArrayList<>();

    public List<MxTransaction> getTransaction() {
        return transaction;
    }

    public void setTransaction(List<MxTransaction> transaction) {
        this.transaction = transaction;
    }
}
