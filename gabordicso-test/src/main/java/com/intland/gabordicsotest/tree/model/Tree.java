package com.intland.gabordicsotest.tree.model;

import java.util.Map;

public class Tree {
	private Map<Long, Node> nodes;
	
	public Tree() { }

	
	public Map<Long, Node> getNodes() {
		return nodes;
	}
	public void setNodes(Map<Long, Node> nodes) {
		this.nodes = nodes;
	}
}
