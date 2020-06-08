package com.intland.gabordicsotest.tree.model;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.util.StdConverter;

public class NodeConverter extends StdConverter<Node, Node> {

	@Override
	public Node convert(Node value) {
		if (value == null) {
			return null;
		}
		Set<Long> newValueChildren = ConcurrentHashMap.newKeySet(); // force concurrent set
		Set<Long> valueChildren = value.getChildren();
		if (valueChildren != null) {
			newValueChildren.addAll(valueChildren);
		}
		Node newValue = new Node();
		newValue.setId(value.getId());
		newValue.setContent(value.getContent());
		newValue.setName(value.getName());
		newValue.setParentId(value.getParentId());
		newValue.setChildren(newValueChildren);
		return newValue;
	}
}
