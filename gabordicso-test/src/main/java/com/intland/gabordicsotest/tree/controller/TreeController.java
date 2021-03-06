package com.intland.gabordicsotest.tree.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.model.Tree;
import com.intland.gabordicsotest.tree.service.FilteredTree;
import com.intland.gabordicsotest.tree.service.TreeService;
import com.intland.gabordicsotest.tree.service.validation.ValidationException;

@RestController
@RequestMapping("/")
public class TreeController {
	private static final Logger logger = LoggerFactory.getLogger(TreeController.class);

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
		logger.info("getTree() called");
		try {
			return service.getTree();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error reading tree");
		}
	}

	@RequestMapping(
			value = "/tree",
			method = RequestMethod.DELETE,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteTree() throws Exception {
		logger.info("deleteTree() called");
		try {
			service.resetTree();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error deleting tree");
		}
	}

	@RequestMapping(
			value = "/filtered-tree/{filter}",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public FilteredTree getFilteredTree(@PathVariable(name = "filter", required = true) String filter) throws IOException {
		logger.info("getFilteredTree() called, filter: " + filter);
		try {
			filter = java.net.URLDecoder.decode(filter, StandardCharsets.UTF_8.name()).trim();
		} catch (UnsupportedEncodingException e) {
			// not going to happen - value came from JDK's own StandardCharsets
		}
		return service.getFilteredTree(filter);
	}

	@RequestMapping(
			value = "/node/{id}",
			method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node getNode(@PathVariable(name = "id", required = true) Long id) throws IOException {
		logger.info("getNode() called, id: " + id);
		return service.getNodeById(id);
	}

	@RequestMapping(
			value = "/node",
			method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node postNode(@RequestBody Node node) throws ValidationException, IOException {
		logger.info("postNode() called, node: " + node);
		return service.createNode(node);
	}

	@RequestMapping(
			value = "/node",
			method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Node putNode(@RequestBody Node node) throws ValidationException, IOException {
		// TODO should have a separate method for updating parent id only; url could be /node/{id}/parentId/{parentId}, method: PUT
		logger.info("putNode() called, node: " + node);
		return service.updateNode(node);
	}

	@RequestMapping(
			value = "/node/{id}",
			method = RequestMethod.DELETE,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteNode(@PathVariable(name = "id", required = true) Long id) throws IOException, ValidationException {
		logger.info("deleteNode() called, id: " + id);
		service.deleteNodeById(id);
	}
}
