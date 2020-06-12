package com.intland.gabordicsotest.tree.service;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.service.validation.ValidationError;
import com.intland.gabordicsotest.tree.service.validation.ValidationResult;

public class NodeValidator {
	private NodeValidator() { }

	public static ValidationResult validateNewNode(Node node) {
		ValidationResult result = new ValidationResult();
		verifyIdNull(node, result);
		verifyParentIdNotNull(node, result);
		verifyRequiredFieldsFilled(node, result);
		return result;
	}

	public static ValidationResult validateUpdatedNode(Node node, boolean isRootNode) {
		ValidationResult result = new ValidationResult();
		verifyIdNotNull(node, result);
		if (isRootNode) {
			verifyParentIdNull(node, result);
		} else {
			verifyParentIdNotNull(node, result);
		}
		verifyRequiredFieldsFilled(node, result);
		return result;
	}

	private static void verifyRequiredFieldsFilled(Node node, ValidationResult result) {
		if (node.getName() == null) {
			result.addError(ValidationError.NAME_EMPTY);
		}
		if (node.getContent() == null) {
			result.addError(ValidationError.CONTENT_EMPTY);
		}
	}
	
	private static void verifyIdNull(Node node, ValidationResult result) {
		if (node.getId() != null) {
			result.addError(ValidationError.ID_NOT_NULL);
		}
	}

	private static void verifyIdNotNull(Node node, ValidationResult result) {
		if (node.getId() == null) {
			result.addError(ValidationError.ID_NULL);
		}
	}

	private static void verifyParentIdNull(Node node, ValidationResult result) {
		if (node.getParentId() != null) {
			result.addError(ValidationError.PARENTID_NOT_NULL);
		}
	}

	private static void verifyParentIdNotNull(Node node, ValidationResult result) {
		if (node.getParentId() == null) {
			result.addError(ValidationError.PARENTID_NULL);
		}
	}

	public static void sanitizeNode(Node node) {
		PolicyFactory policy = Sanitizers.STYLES;
		String name = node.getName();
		String content = node.getContent();
		String safeName = policy.sanitize(name);
		String safeContent = policy.sanitize(content);
		node.setName(safeName);
		node.setContent(safeContent);
	}

}
