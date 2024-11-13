package com.example.tripwallet.models;

public class Expense {
    private long id;
    private long tripId;
    private double amount;
    private String category;
    private String date;
    private String description;

    public Expense(long id, long tripId, double amount, String category, 
                  String date, String description) {
        this.id = id;
        this.tripId = tripId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getTripId() { return tripId; }
    public void setTripId(long tripId) { this.tripId = tripId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 