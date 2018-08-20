package com.sapicons.deepak.tbd.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 25-07-2018.
 */

public class CustomerItem implements Serializable{

    String customerId,firstName, lastName, phone, addressLine1, addressLine2, townCity, pincode, photoUrl;

    public CustomerItem(){

    }
    public CustomerItem(String customerId,String firstName, String lastName, String phone, String addressLine1,
                        String addressLine2, String townCity, String pincode, String photoUrl){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2= addressLine2;
        this.townCity = townCity;
        this.pincode = pincode;
        this.photoUrl = photoUrl;
        this.customerId = customerId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getTownCity() {
        return townCity;
    }

    public void setTownCity(String townCity) {
        this.townCity = townCity;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
