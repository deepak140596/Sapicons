package com.sapicons.deepak.tbd.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 19-08-2018.
 */

public class ExpenseItem implements Serializable {

    String type, amount, timestamp, description, date;
    public ExpenseItem(){}

    public ExpenseItem(String type, String amount, String timestamp, String description, String date){
        this.amount = amount;
        this.type= type;
        this. timestamp = timestamp;
         this.description = description;
         this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
