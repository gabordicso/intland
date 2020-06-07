package com.intland.gabordicsotest.tree.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	private List<ValidationError> errors = new ArrayList<>();
	public List<ValidationError> getErrors() {
		return errors;
	}
	public boolean isValid() {
		return errors.isEmpty();
	}
	public void addError(ValidationError error) {
		errors.add(error);
	}
}
