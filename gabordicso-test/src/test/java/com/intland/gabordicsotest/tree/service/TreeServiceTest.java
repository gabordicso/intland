package com.intland.gabordicsotest.tree.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
	 * x - try to delete root node, fail
	 * x - delete node by id, verify that the tree does not contain it afterwards, try to get node by id, should return null
	 * x - create the same node twice, two instances created
	 * x - update an existing node (not using the same node instance in the test!), no new node created
	 * x - update an existing node twice, no new node created
	 * x - update a non-existent node, fail
	 * x - delete a node with no children, test that only that node got deleted
	 * x - delete a node with children on at least 2 levels, test that no other nodes got deleted
	 * x - update parent of an existing node to another node that is not a child
	 * x - try to update parent of an existing node to itself, fail
	 * x - try to update parent of an existing node to one of its children, fail
	 * x - try to update parent of an existing node to a non-existent node, fail
	 * - search: only root matches
	 * - search: the whole tree matches
	 * - search: a node under root matches, it has children and siblings - only the root node and the matching node are returned, matching node ids contains the id of the matching node only
	 * - search: no node under root matches, but one grandchild of root matches - root node, matching node's parent, and the matching node are returned, matching node ids contains the id of the matching node only
	 * - search: root's child and root's grand-grandchild match - path to grand-grandchild returned, matching nodes marked as matching
	 * - search: root has 3 children, one matches, the child of one of the other children also matches - return: root, matching child, matching grandchild and its parent, third child not returned at all
	 * - search: no matches
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
		updatedRoot.setChildren(ConcurrentHashMap.newKeySet());
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
		Node[] newNodes = prepare_CreateNodes(2, TreeService.rootNodeId, TreeService.rootNodeId);

		// act
		Node addedNode1 = service.createNode(newNodes[0]);
		Long addedNode1Id = addedNode1.getId();
		Node addedNode2 = service.createNode(newNodes[1]);
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
		Node[] newNodes = prepare_CreateNodes(2, TreeService.rootNodeId);

		// act
		Node addedNode1 = service.createNode(newNodes[0]);
		Long addedNode1Id = addedNode1.getId();
		int addedNode1ChildrenSize = addedNode1.getChildren().size();

		newNodes[1].setParentId(addedNode1Id);
		Node addedNode2 = service.createNode(newNodes[1]);
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
	public void deleteRootNode_Throw() throws IOException, ValidationException {
		// arrange
		
		// act

		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.deleteNodeById(TreeService.rootNodeId);
		});
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

	@Test
	public void updateNode() throws ValidationException, IOException {
		// arrange
		String name = "New node name";
		String content = "New node content";
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName(name);
		newNode.setContent(content);
		
		Node addedNode = service.createNode(newNode);
		Long addedNodeId = addedNode.getId();
		
		// act
		Node updateInputNode = new Node();
		updateInputNode.setId(addedNodeId);
		updateInputNode.setParentId(TreeService.rootNodeId);
		updateInputNode.setName(name + " updated");
		updateInputNode.setContent(content + " updated");
		service.updateNode(updateInputNode);

		Tree tree = service.getTree();

		// assert
		Assertions.assertEquals(2, tree.getNodes().size());
	}

	@Test
	public void updateNodeTwice() throws ValidationException, IOException {
		// arrange
		String name = "New node name";
		String content = "New node content";
		Node newNode = new Node();
		newNode.setParentId(TreeService.rootNodeId);
		newNode.setName(name);
		newNode.setContent(content);
		
		Node addedNode = service.createNode(newNode);
		Long addedNodeId = addedNode.getId();
		
		// act
		Node updateInputNode = new Node();
		updateInputNode.setId(addedNodeId);
		updateInputNode.setParentId(TreeService.rootNodeId);
		updateInputNode.setName(name + " updated");
		updateInputNode.setContent(content + " updated");
		service.updateNode(updateInputNode);
		updateInputNode.setName(name + " updated again");
		updateInputNode.setContent(content + " updated again");
		service.updateNode(updateInputNode);

		Tree tree = service.getTree();

		// assert
		Assertions.assertEquals(2, tree.getNodes().size());
	}

	@Test
	public void updateNonExistentNode() throws ValidationException, IOException {
		// arrange
		String name = "New node name";
		String content = "New node content";
		Node updateInputNode = new Node();
		updateInputNode.setId(nonExistentNodeId);
		updateInputNode.setParentId(TreeService.rootNodeId);
		updateInputNode.setName(name);
		updateInputNode.setContent(content);

		// act

		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.updateNode(updateInputNode);
		});
	}
	
	@Test
	public void deleteDeletedNodeOnly() throws ValidationException, IOException {
		// arrange
		Node[] newNodes = prepare_CreateNodes(2, TreeService.rootNodeId, TreeService.rootNodeId);

		// act
		Node addedNode1 = service.createNode(newNodes[0]);
		Long addedNode1Id = addedNode1.getId();
		Node addedNode2 = service.createNode(newNodes[1]);
		Long addedNode2Id = addedNode2.getId();
		service.deleteNodeById(addedNode1Id);
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		Set<Long> rootChildren = root.getChildren();
		
		// assert
		Assertions.assertEquals(2, tree.getNodes().size());
		Assertions.assertFalse(rootChildren.contains(addedNode1Id));
		Assertions.assertTrue(rootChildren.contains(addedNode2Id));
	}

	@Test
	public void deleteDeletedNodeAndChildrenOnly() throws ValidationException, IOException {
		// arrange
		Node[] newNodes = prepare_CreateNodes(5, TreeService.rootNodeId);
		Node newNode1 = newNodes[0];
		Node newNode2 = newNodes[1];
		Node newNode3 = newNodes[2];
		Node newNode4 = newNodes[3];
		Node newNode5 = newNodes[4];

		Long addedNode1Id = service.createNode(newNode1).getId();
		
		newNode2.setParentId(addedNode1Id);
		Long addedNode2Id = service.createNode(newNode2).getId();
		
		newNode3.setParentId(addedNode2Id);
		Long addedNode3Id = service.createNode(newNode3).getId();
		
		newNode4.setParentId(addedNode3Id);
		Long addedNode4Id = service.createNode(newNode4).getId();
		
		Long addedNode5Id = service.createNode(newNode5).getId();

		// act
		service.deleteNodeById(addedNode3Id);
		
		Tree tree = service.getTree();
		Node root = tree.getNodes().get(TreeService.rootNodeId);
		Set<Long> rootChildren = root.getChildren();
		Set<Long> nodeIds = tree.getNodes().keySet();
		
		// assert
		Assertions.assertEquals(4, nodeIds.size());
		Assertions.assertTrue(rootChildren.contains(addedNode1Id));
		Assertions.assertTrue(rootChildren.contains(addedNode5Id));
		Assertions.assertTrue(nodeIds.contains(addedNode1Id));
		Assertions.assertTrue(nodeIds.contains(addedNode2Id));
		Assertions.assertTrue(nodeIds.contains(addedNode5Id));
		Assertions.assertFalse(nodeIds.contains(addedNode3Id));
		Assertions.assertFalse(nodeIds.contains(addedNode4Id));
	}

	@Test
	public void updateParentToValidNewParent() throws ValidationException, IOException {
		// arrange
		Node[] newNodes = prepare_CreateNodes(5, TreeService.rootNodeId);
		Node newNode1 = newNodes[0];
		Node newNode2 = newNodes[1];
		Node newNode3 = newNodes[2];
		Node newNode4 = newNodes[3];
		Node newNode5 = newNodes[4];

		Long addedNode1Id = service.createNode(newNode1).getId();
		
		newNode2.setParentId(addedNode1Id);
		Long addedNode2Id = service.createNode(newNode2).getId();
		
		newNode3.setParentId(addedNode2Id);
		Node addedNode3 = service.createNode(newNode3);
		Long addedNode3Id = addedNode3.getId();
		
		newNode4.setParentId(addedNode3Id);
		service.createNode(newNode4).getId();
		
		Long addedNode5Id = service.createNode(newNode5).getId();

		// act
		Node updateNode3Input = copyNodeExcludingChildren(addedNode3);
		updateNode3Input.setParentId(addedNode5Id);
		Tree tree = service.getTree();
		Map<Long, Node> nodes = tree.getNodes();
		boolean node2ChildrenContainsNode3BeforeUpdate = nodes.get(addedNode2Id).getChildren().contains(addedNode3Id);
		service.updateNode(updateNode3Input);
		tree = service.getTree();
		nodes = tree.getNodes();
		
		// assert
		Assertions.assertTrue(nodes.get(addedNode5Id).getChildren().contains(addedNode3Id));
		Assertions.assertTrue(node2ChildrenContainsNode3BeforeUpdate);
		Assertions.assertFalse(nodes.get(addedNode2Id).getChildren().contains(addedNode3Id));
	}
	
	@Test
	public void updateParentToSelf_Throw() throws ValidationException, IOException {
		// arrange
		Node[] newNodes = prepare_CreateNodes(1, TreeService.rootNodeId);
		Node newNode1 = newNodes[0];

		Node addedNode1 = service.createNode(newNode1);
		Long addedNode1Id = addedNode1.getId();
		Node updateNode1Input = copyNodeExcludingChildren(addedNode1);
		updateNode1Input.setParentId(addedNode1Id);

		// act
		
		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.updateNode(updateNode1Input);
		});
	}
	
	@Test
	public void updateParentToNonExistent_Throw() throws ValidationException, IOException {
		// arrange
		Node[] newNodes = prepare_CreateNodes(1, TreeService.rootNodeId);
		Node newNode1 = newNodes[0];

		Node addedNode1 = service.createNode(newNode1);
		Node updateNode1Input = copyNodeExcludingChildren(addedNode1);
		updateNode1Input.setParentId(nonExistentNodeId);

		// act
		
		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.updateNode(updateNode1Input);
		});
	}
	
	@Test
	public void updateParentToChild_Throw() throws ValidationException, IOException {
		// arrange
		Node[] newNodes = prepare_CreateNodes(2);
		Node newNode1 = newNodes[0];
		Node newNode2 = newNodes[1];

		Node addedNode1 = service.createNode(newNode1);
		Long addedNode1Id = addedNode1.getId();
		newNode2.setParentId(addedNode1Id);
		Long addedNode2Id = service.createNode(newNode2).getId();

		Node updateNode1Input = copyNodeExcludingChildren(addedNode1);
		updateNode1Input.setParentId(addedNode2Id);

		// act
		
		// assert
		Assertions.assertThrows(ValidationException.class, () -> {
			service.updateNode(updateNode1Input);
		});
	}
	
	private Node copyNodeExcludingChildren(Node original) {
		Node copy = new Node();
		
		copy.setId(original.getId());
		copy.setParentId(original.getParentId());
		copy.setName(original.getName());
		copy.setContent(original.getContent());

		return copy;
	}
	private Node[] prepare_CreateNodes(int count, Long... parentIds) {
		Node[] nodes = new Node[count];
		for (int i = 0; i < count; i++) {
			Node newNode = new Node();
			Long parentId = TreeService.rootNodeId;
			if (i < parentIds.length) {
				parentId = parentIds[i];
			}
			newNode.setParentId(parentId);
			newNode.setName(String.format("New node %d name", i));
			newNode.setContent(String.format("New node %d content", i));
			nodes[i] = newNode;
		}

		return nodes;
	}
	
}
