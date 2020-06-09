package com.intland.gabordicsotest.tree.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.intland.gabordicsotest.tree.model.Node;
import com.intland.gabordicsotest.tree.model.Tree;
import com.intland.gabordicsotest.tree.service.validation.ValidationError;
import com.intland.gabordicsotest.tree.service.validation.ValidationException;
import com.intland.gabordicsotest.tree.service.validation.ValidationResult;

@Service
public class TreeService {
	public static final Long rootNodeId = 1L;
	
	private final TreeRepo repo;
	private volatile Tree cachedTree = null;

	public TreeService(TreeRepo repo) {
		this.repo = repo;
	}


	
	/**
	 * 
	 * @throws IOException
	 */
	public void resetTree() throws IOException {
		synchronized(TreeService.class) {
			cachedTree = getEmptyTree();
			saveCachedTree();
		}		
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Tree getTree() throws IOException {
		synchronized(TreeService.class) {
			return getCachedTree();
			// in theory, it is possible that the cached tree gets modified by another thread while
			// being processed by this thread in the method calling this one; this can be fixed by
			// creating a copy of the object here and returning it instead of the original object
			// (this applies to all get operations)
		}
	}

	/**
	 * 
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public FilteredTree getFilteredTree(String filter) throws IOException {
		synchronized(TreeService.class) {
			Set<Long> matchingNodeIds = new TreeSet<Long>();
			Set<Long> nodeIdsOfMatchingSubTree = new TreeSet<Long>();
			Set<Long> currentNodePath = new TreeSet<Long>();
			Set<Long> visitedIds = new TreeSet<Long>();

			lookForMatchingNodesRecursively(filter, rootNodeId, matchingNodeIds, nodeIdsOfMatchingSubTree, currentNodePath, visitedIds);
			
			Tree subTree = new Tree();
			ConcurrentHashMap<Long, Node> nodes = new ConcurrentHashMap<>();
			for (Long nodeId : nodeIdsOfMatchingSubTree) {
				nodes.put(nodeId, getNodeById(nodeId));
			}
			subTree.setNodes(nodes);
			FilteredTree filteredTree = new FilteredTree(subTree, filter, matchingNodeIds);
			return filteredTree;
		}
	}
	
	public Node getNodeById(Long id) throws IOException {
		synchronized(TreeService.class) {
			return getCachedTree().getNodes().get(id);
		}
	}

	public Node createNode(Node inputNode) throws ValidationException, IOException {
		synchronized(TreeService.class) {
			ValidationResult result = NodeValidator.validateNewNode(inputNode);
			if (!result.isValid()) {
				throw new ValidationException(result);
			}
			NodeValidator.sanitizeNode(inputNode);
			Long parentId = inputNode.getParentId();
			Node parentNode = getNodeById(parentId);
			if (parentNode == null) {
				result.addError(ValidationError.PARENTID_INVALID);
				throw new ValidationException(result);
			}
			Node node = new Node();
			Long id = getNewNodeId();
			node.setId(id);
			node.setContent(inputNode.getContent());
			node.setName(inputNode.getName());
			node.setParentId(parentId);
			node.setChildren(ConcurrentHashMap.newKeySet()); // force concurrent set
			getCachedTree().getNodes().put(id, node);
			parentNode.getChildren().add(id);
			saveCachedTree();
			return node;
		}
	}



	public Node updateNode(Node inputNode) throws ValidationException, IOException {
		// TODO refactor method
		synchronized(TreeService.class) {
			// node id may not be changed (wouldn't make sense anyway since we are using the id to identify the node to be updated)
			// it is possible to change the root node's name and content, but not its parentId
			final boolean isRootNode = (rootNodeId.equals(inputNode.getId()));
			ValidationResult result = NodeValidator.validateUpdatedNode(inputNode, isRootNode);
			if (!result.isValid()) {
				throw new ValidationException(result);
			}
			NodeValidator.sanitizeNode(inputNode);
			Long nodeId = inputNode.getId();
			Node existingNode = getCachedTree().getNodes().get(nodeId);
			if (existingNode == null) {
				result.addError(ValidationError.ID_INVALID);
				throw new ValidationException(result);
			}

			existingNode.setName(inputNode.getName());
			existingNode.setContent(inputNode.getContent());

			Long newParentId = inputNode.getParentId();
			Long oldParentId = existingNode.getParentId();
			boolean parentChanged = !isRootNode && !oldParentId.equals(newParentId);
			if (parentChanged) {
				Node newParentNode = getNodeById(newParentId);

				if (newParentNode == null) {
					result.addError(ValidationError.PARENTID_INVALID);
					throw new ValidationException(result);
				}

				if (nodeId.equals(newParentId)) {
					result.addError(ValidationError.PARENTID_SELF);
					throw new ValidationException(result);
				}

				Set<Long> childIds = getChildIds(nodeId);
				if (childIds.contains(newParentId)) {
					result.addError(ValidationError.PARENTID_CHILD);
					throw new ValidationException(result);
				}
				
				Node oldParentNode = getNodeById(oldParentId);

				oldParentNode.getChildren().remove(nodeId);
				newParentNode.getChildren().add(nodeId);
				
				existingNode.setParentId(newParentId);
				
				saveCachedTree();
			}

			return existingNode;
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
			Set<Long> childIds = getChildIds(id);
			for (Long nodeId : childIds) {
				getCachedTree().getNodes().remove(nodeId);
			}
			getCachedTree().getNodes().remove(id);
			saveCachedTree();
		}
	}


	
	private Tree getEmptyTree() {
		ConcurrentHashMap<Long, Node> nodes = new ConcurrentHashMap<>();
		nodes.put(rootNodeId, new Node(rootNodeId, null, "Root node", "Root node content", new TreeSet<>()));
		Tree tree = new Tree();
		tree.setNodes(nodes);
		return tree;
	}

	private Tree getCachedTree() throws IOException {
		if (cachedTree == null) {
			synchronized(TreeService.class) {
				if (cachedTree == null) {
					cachedTree = repo.loadTree();
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

	private Long getNewNodeId() throws IOException {
		Long maxId = null;
		for (Long currentId : getCachedTree().getNodes().keySet()) {
			if (maxId == null || currentId.compareTo(maxId) > 0) {
				maxId = currentId;
			}
		}
		return maxId + 1;
	}

	private Set<Long> getChildIds(Long nodeId) throws IOException {
		Set<Long> ids = new HashSet<>();
		Set<Long> visitedIds = new HashSet<>();
		collectChildIdsRecursively(nodeId, ids, visitedIds);
		return ids;
	}



	private void collectChildIdsRecursively(Long nodeId, Set<Long> ids, Set<Long> visitedIds) throws IOException {
		if (visitedIds.contains(nodeId)) {
			// safety measure against accidental infinite loop
			throw new RuntimeException("Infinite loop detected");
		}
		visitedIds.add(nodeId);

		Node node = getNodeById(nodeId); // node can not be null
		Set<Long> currentChildren = node.getChildren();
		ids.addAll(currentChildren);
		for (Long childId : currentChildren) {
			collectChildIdsRecursively(childId, ids, visitedIds);
		}
	}



	private void lookForMatchingNodesRecursively(
			String filter,
			Long currentNodeId,
			Set<Long> matchingNodeIds,
			Set<Long> nodeIdsOfMatchingSubTree,
			Set<Long> currentNodePath,
			Set<Long> visitedIds) throws IOException {
		/*
		 * - start at root, then test its children, then the next level and so on; for each node visited, check if it matches the filter, if yes, add the node id to the matching node ids and add the path of the node to the nodes in path; in each recursive call, the path of the visited node must be passed as argument and added to the path set if needed
		// - find content by search string, return all matching nodes and the path up to the root node, flagging nodes by whether they match or not
		// - for search, first loop through all nodes and see if they match, collect them in a search result map flagging them as matching nodes, then trace each node back to the root and if a particular node is not yet in the search result map, add it and flag it as non-matching
		 * */
		if (visitedIds.contains(currentNodeId)) {
			// safety measure against accidental infinite loop
			throw new RuntimeException("Infinite loop detected");
		}
		visitedIds.add(currentNodeId);
		Node currentNode = getNodeById(currentNodeId);
		boolean isMatchingNode = isMatchingNode(currentNode, filter);
		if (isMatchingNode) {
			matchingNodeIds.add(currentNodeId);
			nodeIdsOfMatchingSubTree.addAll(currentNodePath);
			nodeIdsOfMatchingSubTree.add(currentNodeId);
		}
		Set<Long> currentNodeChildren = currentNode.getChildren();
		for (Long childId : currentNodeChildren) {
			Set<Long> currentChildPath = new TreeSet<Long>();
			currentChildPath.addAll(currentNodePath);
			currentChildPath.add(currentNodeId);
			lookForMatchingNodesRecursively(filter, childId, matchingNodeIds, nodeIdsOfMatchingSubTree, currentChildPath, visitedIds);
		}
	}



	private boolean isMatchingNode(Node node, String filter) {
		return (node.getName().toUpperCase().contains(filter.toUpperCase()));
	}

}
