package com.sapicons.deepak.tbd.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 20-08-2018.
 */

public class CollectItem implements Serializable {
    String accountNumber, timestamp, amountCollected, profitAmount, accountType;
    public CollectItem(){}

    public CollectItem(String accountNumber, String timestamp, String amountCollected, String profitAmount, String accountType){
        this.accountNumber = accountNumber;
        this.timestamp = timestamp;
        this.amountCollected = amountCollected;
        this.profitAmount = profitAmount;
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAmountCollected() {
        return amountCollected;
    }

    public void setAmountCollected(String amountCollected) {
        this.amountCollected = amountCollected;
    }

    public String getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(String profitAmount) {
        this.profitAmount = profitAmount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
