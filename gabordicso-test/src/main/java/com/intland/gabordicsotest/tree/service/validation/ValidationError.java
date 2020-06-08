package com.intland.gabordicsotest.tree.service.validation;

public enum ValidationError {
	ID_NULL("Property 'id' must be set"),
	ID_NOT_NULL("Property 'id' must not be set"),
	ID_INVALID("Property 'id' is invalid: no such node"),
	ID_ROOT("Root node id not allowed"),
	PARENTID_NULL("Property 'parentId' must be set"),
	PARENTID_NOT_NULL("Property 'parentId' must not be set"),
	PARENTID_INVALID("Property 'parentId' points to a node that does not exist"),
	PARENTID_SELF("Property 'parentId' points to the node being updated"),
	PARENTID_CHILD("Property 'parentId' points to a child node"),
	NAME_EMPTY("Name must be set"),
	CONTENT_EMPTY("Content must be set")
	;

	private String error;
	public String getError() {
		return error;
	}
	private ValidationError(String error) {
		this.error = error;
	}
}
