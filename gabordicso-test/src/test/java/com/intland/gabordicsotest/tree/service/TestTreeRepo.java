package com.intland.gabordicsotest.tree.service;

import java.io.IOException;

import com.intland.gabordicsotest.tree.model.Tree;

public class TestTreeRepo implements TreeRepo {

	private Tree cachedTree;
	@Override
	public void saveTree(Tree tree) throws IOException {
		cachedTree = tree;
	}

	@Override
	public Tree loadTree() throws IOException {
		return cachedTree;
	}

}
