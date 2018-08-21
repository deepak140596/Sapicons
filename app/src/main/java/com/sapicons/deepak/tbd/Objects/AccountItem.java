package com.sapicons.deepak.tbd.Objects;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Deepak Prasad on 02-08-2018.
 */

public class AccountItem implements Serializable{
    String accountNumber, startDate,accoutType,firstName, lastName, phoneNumber, amount,interestPct;
    String actualAmt,dueAmt,accountStatus,endDate,customerPicUrl;
    String loanAmt,actualLoanAmt;
    public AccountItem(){}
    public AccountItem(String accountNumber, String startDate,String endDate, String accoutType, String firstName, String lastName,
                       String phoneNumber, String amount,String actualAmt,String dueAmt, String interestPct,String accountStatus,
                       String customerPicUrl,String loanAmt, String actualLoanAmt){
        this.accountNumber = accountNumber;
        this.startDate = startDate;
        this.accoutType = accoutType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.interestPct = interestPct;
        this.endDate = endDate;
        this.actualAmt= actualAmt;
        this.dueAmt = dueAmt;
        this.accountStatus= accountStatus;
        this.customerPicUrl=customerPicUrl;
        this.loanAmt= loanAmt;
        this.actualLoanAmt = actualLoanAmt;
    }

    public String getLoanAmt() {
        return loanAmt;
    }

    public void setLoanAmt(String loanAmt) {
        this.loanAmt = loanAmt;
    }

    public String getActualLoanAmt() {
        return actualLoanAmt;
    }

    public void setActualLoanAmt(String actualLoanAmt) {
        this.actualLoanAmt = actualLoanAmt;
    }

    public String getCustomerPicUrl() {
        return customerPicUrl;
    }

    public void setCustomerPicUrl(String customerPicUrl) {
        this.customerPicUrl = customerPicUrl;
    }

    public String getActualAmt() {
        return actualAmt;
    }

    public void setActualAmt(String actualAmt) {
        this.actualAmt = actualAmt;
    }

    public String getDueAmt() {
        return dueAmt;
    }

    public void setDueAmt(String dueAmt) {
        this.dueAmt = dueAmt;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getAccoutType() {
        return accoutType;
    }

    public void setAccoutType(String accoutType) {
        this.accoutType = accoutType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInterestPct() {
        return interestPct;
    }

    public void setInterestPct(String interestPct) {
        this.interestPct = interestPct;
    }



    public static Comparator<AccountItem> AccountNameComparator = new Comparator<AccountItem>() {
        @Override
        public int compare(AccountItem i1, AccountItem i2) {

            String accName1 = i1.getFirstName().toUpperCase();
            String accName2 = i2.getFirstName().toUpperCase();

            //ascending order
            return accName1.compareTo(accName2);
        }
    };

    public static Comparator<AccountItem> DueAmtComparator = new Comparator<AccountItem>() {
        @Override
        public int compare(AccountItem accountItem, AccountItem t1) {

            int amt1 = (int)Float.parseFloat(accountItem.getDueAmt());
            int amt2 = (int)Float.parseFloat(t1.getDueAmt());

            // descending order
            return amt2 - amt1;

        }
    };

    public static Comparator<AccountItem> DateClosedComparator = new Comparator<AccountItem>() {
        @Override
        public int compare(AccountItem accountItem, AccountItem t1) {
            long d1 = Long.parseLong(accountItem.getEndDate());
            long d2 = Long.parseLong(t1.getEndDate());

            // from current date to previous date
            return (int)(d1-d2);
        }
    };
}
