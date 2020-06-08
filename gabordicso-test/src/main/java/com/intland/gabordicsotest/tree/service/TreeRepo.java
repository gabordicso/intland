package com.intland.gabordicsotest.tree.service;

import java.io.IOException;

import com.intland.gabordicsotest.tree.model.Tree;

public interface TreeRepo {
	public void saveTree(Tree tree) throws IOException;
	public Tree loadTree() throws IOException;
}
