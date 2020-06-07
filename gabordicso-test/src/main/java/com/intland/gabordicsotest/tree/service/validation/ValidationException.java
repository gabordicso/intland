package com.intland.gabordicsotest.tree.service.validation;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 5359114146878384512L;
	
	ValidationResult validationResult = null;
	public ValidationException(ValidationResult validationResult) {
		this.validationResult = validationResult;
	}
}
