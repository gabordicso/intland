package com.intland.gabordicsotest.tree.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.model.Tree;
import com.intland.gabordicsotest.tree.service.FilteredTree;
import com.intland.gabordicsotest.tree.service.TreeService;

@RestController
public class TreeController {
	TreeService service;
	
	public TreeController(TreeService service) {
		this.service = service;
	}
	
	// @GetMapping("/tree")
//	@RequestMapping(value = "/tree", method = RequestMethod.GET,
//            consumes = MediaType.ALL_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//	public String getTree(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
//		model.addAttribute("name", name);
//		return "greeting";
//	}
//	
//	@RequestMapping(value = "/tree2", method = RequestMethod.GET,
//            consumes = MediaType.ALL_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//	public Tree getTree2(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
//		Map<Long, Node> nodes = new HashMap<>();
//		nodes.put(1L, new Node(1L, null, "name", "content"));
//		Tree tree = new Tree(nodes);
//		return tree;
//	}

/*	@RequestMapping(
			value = "/init",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tree init() {
		service.testSave();
		return service.getTree();
	}*/

	@RequestMapping(
			value = "/tree",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tree getTree() throws Exception {
		try {
			return service.getTree();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error reading tree");
		}
	}

	@RequestMapping(
			value = "/filteredTree/{filter}",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public FilteredTree getFilteredTree(@PathVariable(name = "filter", required = true) String filter) {
		return service.getFilteredTree(filter);
	}

	@RequestMapping(
			value = "/node/{id}",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node getNode(@PathVariable(name = "id", required = true) Long id) {
		return service.getNodeById(id);
	}

	@RequestMapping(
			value = "/node",
			method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tree postNode(@RequestParam Node node) {
		return service.createNode(node);
	}

	@RequestMapping(
			value = "/node",
			method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tree putNode(@RequestParam Node node) {
		return service.updateNode(node);
	}

	@RequestMapping(
			value = "/node/{id}",
			method = RequestMethod.DELETE,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node deleteNode(@PathVariable(name = "id", required = true) Long id) {
		return service.deleteNodeById(id);
	}

	/*
	 * TODO
	 * - create/update tree node - POST: create, PUT: update
	 *   name and content mandatory, parentId optional
	 * - delete node by id (also delete the subtree under the node to be deleted)
	 * - list tree
	 * - reorganize (move node under a new parent node)
	 * - get node by id
	 * - find content by search string, return all matching nodes and the path up to the root node, flagging nodes by whether they match or not
	 * - store structure in a file
	 * for testing, create a TreeSaver class that can be injected; should be a mock instance for testing and a prod instance for prod
	 * for search, first loop through all nodes and see if they match, collect them in a search result map flagging them as matching nodes, then trace each node back to the root and if a particular node is not yet in the search result map, add it and flag it as non-matching 
	 * */
}
