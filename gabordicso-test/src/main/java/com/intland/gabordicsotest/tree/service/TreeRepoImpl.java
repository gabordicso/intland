package com.intland.gabordicsotest.tree.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intland.gabordicsotest.tree.model.Tree;

@Service
public class TreeRepoImpl implements TreeRepo {
	@Value("${gabordicsotest.treerepo.filePath}")
	private String filePath;
	
	public TreeRepoImpl() { }

	public TreeRepoImpl(String filePath) {
		this.filePath = filePath;
	}
	
	public void saveTree(Tree tree) throws IOException {
		synchronized (TreeRepo.class) {
			new ObjectMapper().writeValue(new File(filePath), tree);
		}
	}

	public Tree loadTree() throws IOException {
		synchronized (TreeRepo.class) {
			return new ObjectMapper().readValue(new File(filePath), Tree.class);
		}
	}

}
