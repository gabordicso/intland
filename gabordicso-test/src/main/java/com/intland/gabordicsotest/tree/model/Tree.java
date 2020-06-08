package com.intland.gabordicsotest.tree.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Tree {
	private ConcurrentHashMap<Long, Node> nodes;
	
	public Tree() { }

	
	public Map<Long, Node> getNodes() {
		return nodes;
	}
	public void setNodes(ConcurrentHashMap<Long, Node> nodes) {
		this.nodes = nodes;
	}
}
