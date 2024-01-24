package org.abc.authentication.model;

import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Represents an individual user who can create account, login, and place the orders. It encapsulates all the user related information.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class User {

    @NotNull(message = "User name must not be null")
    @Size(min = 3, message = "Name minimum length should be 3")
    private String name;
    @NotNull(message = "Email id must not be null")
    @Email(message = "Email id should be proper")
    private String emailId;
    @NotNull(message = "Mobile number must not be null")
    private String mobileNumber;
    @NotNull(message = "Password must not be null")
    @Size(min = 8, message = "Password minimun length should be 8")
    private String password;
    private int id;
    private List<String> addresses;

    public void setId(final int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void addAddress(String address) {
        addresses = (null == addresses) ? new ArrayList<>() : addresses;

        addresses.add(address);
    }

    public List<String> getAddresses() {
        return addresses;
    }

    @Override
    public boolean equals(final Object object) {
        return !Objects.isNull(object) && getClass() == object.getClass() && this.hashCode() == object.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailId, mobileNumber);
    }
}