package com.intland.gabordicsotest.tree.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.model.Tree;
import com.intland.gabordicsotest.tree.service.validation.ValidationException;

public class TreeServiceTest {

	// TODO refactor test, move duplicated, reusable code parts to single, hierarchic functions

	private static final Long nonExistentNodeId = -1L;
	private TreeRepo repo;
	private TreeService service;
	
	@BeforeEach
	public void createService() throws IOException {
		repo = new TestTreeRepo();
		service = new TreeService(repo);
		service.resetTree();
	}
	
	@AfterEach
	public void cleanupService() {
		service = null;
		repo = null;
	}
	
	/*
	 * Test cases:
	 * x - initial tree
	 * x - update root
	 * x - try to set parentId for root
	 * x - add node under root
	 * x - add 2 nodes under root
	 * x - add 2 nodes, one under root, one under the other one
	 * x - get node by id, valid id
	 * x - get node by id, invalid id
	 * x - delete node by id, verify that the tree does not contain it afterwards, try to get node by id, should return null
	 * x - create the same node twice, two instances created
	 * - update an existing node (not using the same node instance in the test!), no new node created
	 * - update an existing node twice, no new node created
	 * - update a non-existent node, fail
	 * - delete a node with no children, test that only that node got deleted
	 * - delete a node with children on at least 2 levels, test that no other nodes got deleted
	 * - try to delete root node, fail
	 * - update parent of an existing node to another node that is not a child
	 * - try to update parent of an existing node to itself, fail
	 * - try to update parent of an existing node to one of its children, fail
	 * - try to update parent of an existing node to a non-existent node, fail
	 * - search: only root matches
	 * - search: the whole tree matches
	 * - search: a node under root matches, it has children and siblings - only the root node and the matching node are returned, matching node ids contains the id of the matching node only
	 * - search: no node under root matches, but one grandchild of root matches - root node, matching node's parent, and the matching node are returned, matching node ids contains the id of the matching node only
	 * - search: root's child and root's grand-grandchild match - path to grand-grandchild returned, matching nodes marked as matching
	 * - search: root has 3 children, one matches, the child of one of the other children also matches - return: root, matching child, matching grandchild and its parent, third child not returned at all
	 * */
	
	@Test
	public void initialTree() throws IOException {
		// arrange
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		
		// act (empty)
		
		// assert
		Assertions.assertNotNull(tree);
		Assertions.assertNotNull(root);
		Assertions.assertEquals(1, tree.getNodes().size());
	}
	
	@Test
	public void updateRoot() throws IOException, ValidationException {
		// arrange
		Tree tree = service.getTree();
		Node oldRoot = tree.getNodes().get(TreeService.rootNodeId);
		String oldName = oldRoot.getName();
		String oldContent = oldRoot.getContent();
		String newName = oldName + " modified";
		String newContent = oldContent + " modified";
		
		Node updatedRoot = new Node();
		updatedRoot.setId(TreeService.rootNodeId);
		updatedRoot.setContent(newContent);
		updatedRoot.setName(newName);
		updatedRoot.setChildren(new HashSet<>());
		updatedRoot.getChildren().add(nonExistentNodeId);

		// act
		Node newRoot = service.updateNode(updatedRoot);
		
		// assert
		Assertions.assertNotNull(newRoot);
		Assertions.assertEquals(newContent, newRoot.getContent());
		Assertions.assertEquals(newName, newRoot.getName());
		Assertions.assertEquals(0, newRoot.getChildren().size());
	}

	@Test
	public void updateRootParentId_Throw() throws IOException {
		// arrange
		Tree tree = service.getTree();
		Node oldRoot = tree.getNodes().get(TreeService.rootNodeId);
		oldRoot.setParentId(TreeService.rootNodeId);
		
		// act
		
		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.updateNode(oldRoot);
		});
	}
	
	@Test
	public void createNode() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName("New node name");
		newNode.setContent("New node content");
		
		// act
		Node addedNode = service.createNode(newNode);
		Long addedNodeId = addedNode.getId();
		Set<Long> addedNodeChildren = addedNode.getChildren();
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		Set<Long> rootChildren = root.getChildren();
		
		// assert
		Assertions.assertEquals(1, rootChildren.size());
		Assertions.assertTrue(rootChildren.contains(addedNodeId));
		Assertions.assertNotNull(addedNodeId);
		Assertions.assertNotNull(addedNodeChildren);
		Assertions.assertEquals(0, addedNodeChildren.size());
		Assertions.assertNotEquals(TreeService.rootNodeId, addedNodeId);
	}

	@Test
	public void createSameNodeTwice_TwoNewNodesAdded() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName("New node name");
		newNode.setContent("New node content");

		// act
		Node addedNode1 = service.createNode(newNode);
		Node addedNode2 = service.createNode(newNode);
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		Set<Long> rootChildren = root.getChildren();
		
		// assert
		Assertions.assertEquals(2, rootChildren.size());
		Assertions.assertNotEquals(addedNode1.getId(), addedNode2.getId());
	}

	@Test
	public void createInvalidNodeNullValues_Throw() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName(null);
		newNode.setContent(null);
		
		// act
		
		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.createNode(newNode);
		});
	}

	@Test
	public void createInvalidNodeNullParentId_Throw() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(null);
		newNode.setName("name");
		newNode.setContent("content");
		
		// act
		
		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.createNode(newNode);
		});
	}

	@Test
	public void create2NodesUnderRoot() throws IOException, ValidationException {
		// arrange
		Node newNode1 = new Node();
		newNode1.setParentId(TreeService.rootNodeId);
		newNode1.setName("New node 1 name");
		newNode1.setContent("New node 1 content");
		Node newNode2 = new Node();
		newNode2.setParentId(TreeService.rootNodeId);
		newNode2.setName("New node 2 name");
		newNode2.setContent("New node 2 content");

		// act
		Node addedNode1 = service.createNode(newNode1);
		Long addedNode1Id = addedNode1.getId();
		Node addedNode2 = service.createNode(newNode2);
		Long addedNode2Id = addedNode2.getId();
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		Set<Long> rootChildren = root.getChildren();
		
		// assert
		Assertions.assertEquals(2, rootChildren.size());
		Assertions.assertTrue(rootChildren.contains(addedNode1Id));
		Assertions.assertTrue(rootChildren.contains(addedNode2Id));
	}

	@Test
	public void create2NodesUnderEachOther() throws IOException, ValidationException {
		// arrange
		Node newNode1 = new Node();
		newNode1.setParentId(TreeService.rootNodeId);
		newNode1.setName("New node 1 name");
		newNode1.setContent("New node 1 content");
		Node newNode2 = new Node();
		newNode2.setName("New node 2 name");
		newNode2.setContent("New node 2 content");

		// act
		Node addedNode1 = service.createNode(newNode1);
		Long addedNode1Id = addedNode1.getId();
		int addedNode1ChildrenSize = addedNode1.getChildren().size();

		newNode2.setParentId(addedNode1Id);
		Node addedNode2 = service.createNode(newNode2);
		Long addedNode2Id = addedNode2.getId();
		Node addedNode1Refreshed = service.getNodeById(addedNode1Id);
		Set<Long> addedNode1RefreshedChildren = addedNode1Refreshed.getChildren();
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		Set<Long> rootChildren = root.getChildren();
		
		// assert
		Assertions.assertEquals(1, rootChildren.size());
		Assertions.assertEquals(1, addedNode1RefreshedChildren.size());
		Assertions.assertTrue(rootChildren.contains(addedNode1Id));
		Assertions.assertTrue(addedNode1RefreshedChildren.contains(addedNode2Id));

		/* The assertion below is supposed to verify the fact that the first new node instance here, returned by createNode()
		 * before the addition of the second new node, does not have any children, and that this doesn't change even after the
		 * second new node has been added under it (since this instance is supposed to be a copy of the first node before the
		 * change and, as such, is expected to be outdated after the second node has been added). However, in this JUnit test,
		 * the returned node is a reference to the same node object in the tree in the service, and therefore it reflects
		 * subsequent changes made in the service directly. That's why we cached the size of the child id set before adding the
		 * second node. This will work differently when using the service through a REST API. */

		Assertions.assertEquals(0, addedNode1ChildrenSize);
	}

	@Test
	public void getExistentNodeById() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName("New node name");
		newNode.setContent("New node content");
		
		// act
		Node addedNode = service.createNode(newNode);
		Long addedNodeId = addedNode.getId();
		Node returnedNode = service.getNodeById(addedNodeId);
		
		// assert
		Assertions.assertNotNull(returnedNode);
	}

	@Test
	public void getNonExistentNodeById() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName("New node name");
		newNode.setContent("New node content");
		
		// act
		service.createNode(newNode);
		Node returnedNode = service.getNodeById(nonExistentNodeId);
		
		// assert
		Assertions.assertNull(returnedNode);
	}

	@Test
	public void deleteNode() throws IOException, ValidationException {
		// arrange
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName("New node name");
		newNode.setContent("New node content");
		
		Node addedNode = service.createNode(newNode);
		Long addedNodeId = addedNode.getId();
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		int rootChildrenSizeBeforeDelete = root.getChildren().size();
		boolean rootChildrenContainsAddedNodeBeforeDelete = root.getChildren().contains(addedNodeId);
		
		// act
		service.deleteNodeById(addedNodeId);
		tree = service.getTree();
		root = tree.getNodes().get(TreeService.rootNodeId);
		int rootChildrenSizeAfterDelete = root.getChildren().size();
		boolean rootChildrenContainsAddedNodeAfterDelete = root.getChildren().contains(addedNodeId);
		Node nodeAfterDelete = service.getNodeById(addedNodeId);
		
		// assert
		Assertions.assertEquals(1, rootChildrenSizeBeforeDelete);
		Assertions.assertEquals(0, rootChildrenSizeAfterDelete);
		Assertions.assertTrue(rootChildrenContainsAddedNodeBeforeDelete);
		Assertions.assertFalse(rootChildrenContainsAddedNodeAfterDelete);
		Assertions.assertNull(nodeAfterDelete);
	}

}
