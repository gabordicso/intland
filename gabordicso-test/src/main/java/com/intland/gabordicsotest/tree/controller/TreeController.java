package com.intland.gabordicsotest.tree.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.model.Tree;
import com.intland.gabordicsotest.tree.service.FilteredTree;
import com.intland.gabordicsotest.tree.service.TreeService;
import com.intland.gabordicsotest.tree.service.validation.ValidationException;

@RestController
@RequestMapping("/")
public class TreeController {
	TreeService service;
	
	public TreeController(TreeService service) {
		this.service = service;
	}
	
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
	public FilteredTree getFilteredTree(@PathVariable(name = "filter", required = true) String filter) throws IOException {
		return service.getFilteredTree(filter);
	}

	@RequestMapping(
			value = "/node/{id}",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node getNode(@PathVariable(name = "id", required = true) Long id) throws IOException {
		return service.getNodeById(id);
	}

	@RequestMapping(
			value = "/node",
			method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node postNode(@RequestBody Node node) throws ValidationException, IOException {
		return service.createNode(node);
	}

	@RequestMapping(
			value = "/node",
			method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node putNode(@RequestParam Node node) throws ValidationException, IOException {
		return service.updateNode(node);
	}

	@RequestMapping(
			value = "/node/{id}",
			method = RequestMethod.DELETE,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteNode(@PathVariable(name = "id", required = true) Long id) throws IOException, ValidationException {
		service.deleteNodeById(id);
	}
}
