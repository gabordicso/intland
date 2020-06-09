package com.intland.gabordicsotest.tree.service;

import java.io.IOException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.intland.gabordicsotest.tree.model.Tree;

@Service
@Primary
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
