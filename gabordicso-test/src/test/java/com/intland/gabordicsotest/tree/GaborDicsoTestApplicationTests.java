package com.intland.gabordicsotest.tree;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.intland.gabordicsotest.tree.service.TreeService;

@SpringBootTest
class GaborDicsoTestApplicationTests {

	@Autowired
	private TreeService treeService;
	@Test
	void contextLoads() {
		// System.out.println(treeService.getTree().getNodes().get(1L).getContent());
	}

}
