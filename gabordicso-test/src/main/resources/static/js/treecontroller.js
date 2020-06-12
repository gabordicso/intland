const noTreeContainerId = "noTree_container";
const filterTextboxId = "filterTextbox";
const filterButtonId = "filterButton";
const clearFilterButtonId = "clearFilterButton";
const jstreeDivId = "jstree_div";

var TreeController = function() { }

TreeController.prototype = {
	init: function(uiController) {
		this.uiController = uiController;
		
		this.lastFilter = null;

		this.filterTextbox = $("#" + filterTextboxId);
		this.filterButton = $("#" + filterButtonId);
		this.clearFilterButton = $("#" + clearFilterButtonId);

		this.treeDiv = $('#' + jstreeDivId);
		this.lastTreeElementInstance = null;
		this.lastLoadedTree = null;
		this.nextTreeElementId = 0;
		this.nodeIdToSelectAfterRefresh = null;
		this.noResultsDiv = $('#' + noTreeContainerId)
		if (this.filterButtonClickHandler == null) {
			this.filterButtonClickHandler = this.filterButton.click(this.onFilterButtonClick.bind(this));
		}
		if (this.clearFilterButtonClickHandler == null) {
			this.clearFilterButtonClickHandler = this.clearFilterButton.click(this.onClearFilterButtonClick.bind(this));
		}
	},
	
	uninit: function() {
		this.filterButton.off("click");
		this.clearFilterButton.off("click");
		this.uninitTreeElement();
	},

	uninitTreeElement: function() {
		if (this.lastTreeElementInstance) {
			this.lastTreeElementInstance.remove();
		}
	},
	


	onFilterButtonClick: function() {
		this.processFilterInput();
	},
	
	onClearFilterButtonClick: function() {
		this.filterTextbox.val("");
		this.nodeIdToSelectAfterRefresh = rootNodeId;
		this.processFilterInput();
	},
	


	processFilterInput: function() {
		var filter = this.filterTextbox.val();
		this.processFilter(filter);
	},
	
	processFilter: function(filter) {
		if (filter == null) {
			filter = "";
		}
		filter = filter.trim();
		if (filter == "") {
			this.uiController.loadTree();
		} else {
			this.uiController.loadFilteredTree(filter);
		}
	},
	


	setTree: function(tree, selectedNodeId, enabledNodeIds) {
		if (selectedNodeId == null) {
			selectedNodeId = this.nodeIdToSelectAfterRefresh;
		}
		if (selectedNodeId == null || tree.nodes[selectedNodeId] == null) {
			selectedNodeId = rootNodeId;
		}
		this.lastLoadedTree = tree;
		var isEmptyTree = (tree.nodes[rootNodeId] == null);
		if (isEmptyTree) {
			this.treeDiv.hide();
			this.noResultsDiv.show();
			this.uiController.selectNode(null);
		} else {
			this.noResultsDiv.hide();
			this.treeDiv.show();
			var data = this.getTreeDataFromTree(tree, selectedNodeId, enabledNodeIds);
			if (enabledNodeIds == null || enabledNodeIds.includes(selectedNodeId)) {
				this.uiController.selectNode(tree.nodes[selectedNodeId]);
			} else {
				this.uiController.selectNode(null);
			}
			this.uninitTreeElement();
			this.lastTreeElementInstance = this.getNewTreePlaceholder().jstree({
				'core': {
					'data': data,
					'multiple': false,
					"check_callback": true
				},
				"plugins": [
					"dnd", "wholerow"
				]
			})
			.on('move_node.jstree', function(e, data) {
				this.moveNode(data.node.id, data.node.parent);
			}.bind(this))
			.on('changed.jstree', function (e, data) {
			    var ids = [];
			    for(var i = 0; i < data.selected.length; i++) {
			    	ids.push(data.instance.get_node(data.selected[i]).id);
			    }
			    var selectedId = null;
			    if (ids.length > 0) {
			    	selectedId = ids[0];
			    }
			    this.selectNode(selectedId);
			  }.bind(this));
		}

		this.lastFilter = null;
	},
	
	setFilteredTree: function(filteredTree) {
		var tree = filteredTree.tree;
		this.setTree(tree, null, filteredTree.matchingNodeIds);
		this.lastFilter = filteredTree.filter;
	},
	
	refresh: function(selectedNodeId) {
		this.nodeIdToSelectAfterRefresh = selectedNodeId;
		this.processFilter(this.lastFilter);
	},
	
	getNewTreePlaceholder: function() {
		var id = "treePlaceholder" + this.nextTreeElementId++;
		this.treeDiv.append('<div id="' + id + '"></div>');
		return $('#' + id);
	},
	


	selectNode: function(nodeId) {
		var selectedNode = null;
		if (nodeId !== undefined && this.lastLoadedTree != null && this.lastLoadedTree.nodes[nodeId] != null) {
			selectedNode = this.lastLoadedTree.nodes[nodeId];
		}
		if (selectedNode != null) {
			this.nodeIdToSelectAfterRefresh = selectedNode.id;
		} else {
			this.nodeIdToSelectAfterRefresh = null;
		}
		this.uiController.selectNode(selectedNode);
	},
	
	moveNode: function(nodeId, newParentId) {
		if (isNaN(newParentId)) {
			// jstree allows moving a node up to the root level, we don't allow this
			this.refresh(null);
			return;
		}
		var updatedNode = this.lastLoadedTree.nodes[nodeId];
		this.uiController.updateNodeParent(updatedNode, newParentId);
	},
	


	getTreeDataFromTree: function(tree, selectedNodeId, enabledNodeIds) {
		var nodes = tree.nodes;
		if (nodes[rootNodeId]) {		
			return this.getTreeDataOfNodes([ rootNodeId ], nodes, selectedNodeId, enabledNodeIds);
		}
		return { };
	},
	
	getTreeDataOfNodes: function(nodeIdArray, nodes, selectedNodeId, enabledNodeIds) {
		var data = Array();
		nodeIdArray.forEach(function(nodeId) {
			var currentNode = nodes[nodeId];
			if (currentNode != null) {
				var disabled = !(enabledNodeIds == null || enabledNodeIds.includes(currentNode.id));
				var selected = (currentNode.id == parseInt(selectedNodeId)) && !disabled;
				var nodeTreeData = {
					id: currentNode.id,
					text: currentNode.name,
					state: {
						opened: true,
						selected: selected,
						disabled: disabled
					}
				};
				var childIds = currentNode.children;
				if (Array.isArray(childIds) && childIds.length > 0) {
					nodeTreeData.children = this.getTreeDataOfNodes(childIds, nodes, selectedNodeId, enabledNodeIds);
				}
				data.push(nodeTreeData);
			}
		}.bind(this));
		return data;
	}
}

