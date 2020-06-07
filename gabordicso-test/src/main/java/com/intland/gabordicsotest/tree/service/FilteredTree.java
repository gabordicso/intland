package com.intland.gabordicsotest.tree.service;

import java.util.Set;

import com.intland.gabordicsotest.tree.model.Tree;

public class FilteredTree {

	private Tree tree;
	private String filter;
	private Set<Long> matchingNodeIds;

	public FilteredTree(Tree tree, String filter, Set<Long> matchingNodeIds) {
		this.tree = tree;
		this.filter = filter;
		this.matchingNodeIds = matchingNodeIds;
	}

	public Tree getTree() {
		return tree;
	}
	public void setTree(Tree tree) {
		this.tree = tree;
	}

	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Set<Long> getMatchingNodeIds() {
		return matchingNodeIds;
	}
	public void setMatchingNodeIds(Set<Long> matchingNodeIds) {
		this.matchingNodeIds = matchingNodeIds;
	}
}
