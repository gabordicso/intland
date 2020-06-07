package com.intland.gabordicsotest.tree.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.model.Tree;
import com.intland.gabordicsotest.tree.service.validation.ValidationError;
import com.intland.gabordicsotest.tree.service.validation.ValidationException;
import com.intland.gabordicsotest.tree.service.validation.ValidationResult;

@Service
public class TreeService {
	private static final Long rootNodeId = 1L;
	
	private TreeRepo repo;
	private Tree cachedTree = null;

	public TreeService(TreeRepo repo) {
		this.repo = repo;
	}


	
	public Tree getTree() throws IOException {
		synchronized(TreeService.class) {
			return getCachedTree();
			// in theory, it is possible that the cached tree gets modified by another thread while
			// being processed by this thread in the method calling this one; this can be fixed by
			// creating a copy of the object here and returning it instead of the original object
			// (this applies to all get operations)
		}
	}

	public FilteredTree getFilteredTree(String filter) {
		synchronized(TreeService.class) {
			// TODO
			return null;
		}
	}

	public Node getNodeById(Long id) throws IOException {
		synchronized(TreeService.class) {
			return getCachedTree().getNodes().get(id);
		}
	}

	public Node createNode(Node node) throws ValidationException, IOException {
		synchronized(TreeService.class) {
			ValidationResult result = NodeValidator.validateNewNode(node);
			if (!result.isValid()) {
				throw new ValidationException(result);
			}
			NodeValidator.sanitizeNode(node);
			Long parentId = node.getParentId();
			Node parentNode = getNodeById(parentId);
			if (parentNode == null) {
				result.addError(ValidationError.PARENTID_INVALID);
				throw new ValidationException(result);
			}
			Long id = getNewNodeId();
			node.setId(id);
			getCachedTree().getNodes().put(id, node);
			parentNode.getChildren().add(id);
			saveCachedTree();
			return node;
		}
	}



	public Node updateNode(Node node) throws ValidationException, IOException {
		// TODO refactor method
		synchronized(TreeService.class) {
			// node id may not be changed (also doesn't make sense since we are using the id to find the node to be updated)
			// it is possible to change the root node's name and content, but not its parentId
			final boolean isRootNode = (rootNodeId.equals(node.getId())); // TODO test case: updating root node with non-null parentId
			ValidationResult result = NodeValidator.validateUpdatedNode(node, isRootNode);
			if (!result.isValid()) {
				throw new ValidationException(result);
			}
			NodeValidator.sanitizeNode(node);
			Long nodeId = node.getId();
			Node currentNode = getCachedTree().getNodes().get(nodeId);
			if (currentNode == null) {
				result.addError(ValidationError.ID_INVALID);
				throw new ValidationException(result);
			}
			Long parentId = node.getParentId();
			boolean parentChanged = !isRootNode && !currentNode.getParentId().equals(parentId);
			if (parentChanged) {
				if (getNodeById(parentId) == null) {
					result.addError(ValidationError.PARENTID_INVALID);
					throw new ValidationException(result);
				}
				Set<Long> childIds = getChildIdsRecursively(nodeId);
				if (childIds.contains(parentId)) {
					result.addError(ValidationError.PARENTID_CHILD);
					throw new ValidationException(result);
				}
				// TODO handle parentId change
				// if parentId changed, validate that the new parent is not a child node, then remove node id from children of old parent and add it to children of new parent
			}

			return node;
		}
	}

	public void deleteNodeById(Long id) throws IOException, ValidationException {
		synchronized(TreeService.class) {
			if (id == null) {
				ValidationResult result = new ValidationResult();
				result.addError(ValidationError.ID_NULL);
				throw new ValidationException(result);
			}
			if (rootNodeId.equals(id)) {
				ValidationResult result = new ValidationResult();
				result.addError(ValidationError.ID_ROOT);
				throw new ValidationException(result);
			}
			Node node = getNodeById(id);
			if (node == null) {
				ValidationResult result = new ValidationResult();
				result.addError(ValidationError.ID_INVALID);
				throw new ValidationException(result);
			}
			Long parentId = node.getParentId();
			Node parentNode = getNodeById(parentId);
			parentNode.getChildren().remove(id);
			getCachedTree().getNodes().remove(id);
			Set<Long> childIds = getChildIdsRecursively(id);
			for (Long nodeId : childIds) {
				getCachedTree().getNodes().remove(nodeId);
			}
			saveCachedTree();
		}
	}


	
	private Set<Long> getChildIdsRecursively(Long nodeId) {
		return getChildIdsRecursively(nodeId, new HashSet<>()); // TODO ensure no infinite loop can occur
	}



	private Set<Long> getChildIdsRecursively(Long nodeId, Set<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}



	private Tree getCachedTree() throws IOException {
		if (cachedTree == null) {
			synchronized(TreeService.class) {
				if (cachedTree == null) {
					cachedTree = repo.loadTree();
					if (cachedTree == null) {
						cachedTree = getEmptyTree();
						saveCachedTree();
					}
				}
			}
		}
		return cachedTree;
	}

	private void saveCachedTree() throws IOException {
		synchronized(TreeService.class) {
			repo.saveTree(cachedTree);
		}
	}

	private Node getRootNode() {
		return new Node(rootNodeId, null, "Root node", "Root node content", new TreeSet<>());
	}

	private Tree getEmptyTree() {
		Map<Long, Node> nodes = new HashMap<>();
		nodes.put(rootNodeId, getRootNode());
		Tree tree = new Tree();
		tree.setNodes(nodes);
		return tree;
	}

	private Long getNewNodeId() throws IOException {
		Long maxId = null;
		for (Long currentId : getCachedTree().getNodes().keySet()) {
			if (maxId == null || currentId.compareTo(maxId) > 0) {
				maxId = currentId;
			}
		}
		return maxId + 1;
	}

}
