package com.intland.gabordicsotest.tree;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import com.intland.gabordicsotest.tree.controller.TreeController;
import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.service.TreeService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GaborDicsoTestApplicationTests {

	@LocalServerPort
	private int port;
	
	@Autowired
	private TreeController controller;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	void integrationTest() {
		// check context load
		assertThat(controller).isNotNull();

		// initialize tree service by calling delete (it will create an empty tree)
		String baseUrl = "http://localhost:" + port + "/";
		String treeUrl = baseUrl + "tree";
		String nodeUrl = baseUrl + "node";
		restTemplate.delete(treeUrl);

		// new node will be added below, tree should not contain it yet, only the root node
		String newNodeName = "newNodeName";
		String newNodeContent = "newNodeContent";
		String tree = this.restTemplate.getForObject(treeUrl, String.class);
		assertThat(tree).contains("Root node");
		assertThat(tree).doesNotContain(newNodeName);

		// add new node
		Node node = new Node(null, TreeService.rootNodeId, newNodeName, newNodeContent, null);
		restTemplate.postForEntity(nodeUrl, node, Node.class);
		
		// tree should now contain the new node
		tree = this.restTemplate.getForObject(treeUrl, String.class);
		assertThat(tree).contains(newNodeName);
	}
}
