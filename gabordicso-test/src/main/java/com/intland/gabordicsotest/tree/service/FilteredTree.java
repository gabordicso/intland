package com.intland.gabordicsotest.tree.service;

import java.util.Set;

import com.intland.gabordicsotest.tree.model.Tree;

public class FilteredTree {

	private Tree filteredTree;
	private String filter;
	private Set<Long> matchingNodeIds;

	public FilteredTree(Tree filteredTree, String filter, Set<Long> matchingNodeIds) {
		this.filteredTree = filteredTree;
		this.filter = filter;
		this.matchingNodeIds = matchingNodeIds;
	}

	public Tree getFilteredTree() {
		return filteredTree;
	}
	public void setFilteredTree(Tree filteredTree) {
		this.filteredTree = filteredTree;
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
