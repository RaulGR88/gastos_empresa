package com.kael.gastosEmpresa.model;

public class GroupBalanceDTO {
    private String name;
    private String lastName;
    private double balance;

    public GroupBalanceDTO(String name, String lastName, double balance) {
        this.name = name;
        this.lastName = lastName;
        this.balance = balance;
    }

    // Getters
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public double getBalance() { return balance; }
}
