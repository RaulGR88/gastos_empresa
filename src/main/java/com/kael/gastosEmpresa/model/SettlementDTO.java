package com.kael.gastosEmpresa.model;

public class SettlementDTO {
    private String from;
    private String to;
    private double amount;
    
	public SettlementDTO() {
		super();
	}

	public SettlementDTO(String from, String to, double amount) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
