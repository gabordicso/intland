const noTreeContainerId = "noTree_container";

var TreeController = function() { }

TreeController.prototype = {
	init: function(uiController) {
		this.uiController = uiController;
		
		this.lastFilter = null;

		this.filterTextbox = $("#filterTextbox");
		this.filterButton = $("#filterButton");
		this.clearFilterButton = $("#clearFilterButton");

		this.treeDiv = $('#jstree_div');
		this.lastTreeElementInstance = null;
		this.lastLoadedTree = null;
		this.nextTreeElementId = 0;
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
	},

	onFilterButtonClick: function() {
		this.processFilterInput();
	},
	
	onClearFilterButtonClick: function() {
		this.filterTextbox.val("");
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
		// this.lastFilter = filter;
		if (filter == "") {
			this.uiController.loadTree();
		} else {
			this.uiController.loadFilteredTree(filter);
		}
	},
	
	setTree: function(tree, selectedNodeId) {
		this.lastLoadedTree = tree;
		var isEmptyTree = tree.nodes.length <= 0;
		if (isEmptyTree) {
			this.treeDiv.hide();
			this.noResultsDiv.show();
		} else {
			this.noResultsDiv.hide();
			this.treeDiv.show();
			var data = this.getTreeDataFromTree(tree, selectedNodeId, null);
			if (this.lastTreeElementInstance) {
				this.lastTreeElementInstance.remove();
			}
			this.lastTreeElementInstance = this.getNewTreePlaceholder().jstree({
				'core': {
					'data': data,
					'multiple': false,
					"check_callback": true
				},
				"plugins": [
					"dnd", "search", "state", "types", "wholerow"
				]
			}).on('changed.jstree', function (e, data) {
			    var ids = [];
			    for(var i = 0; i < data.selected.length; i++) {
			    	ids.push(data.instance.get_node(data.selected[i]).id);
			    }
			    var selectedId = null;
			    if (ids.length > 0) {
			    	selectedId = ids[0];
			    }
			    this.selectNode(selectedId);
			  }.bind(this))
			  .on('dnd_stop.vakata.jstree', function(e, data) {alert(0);});
		}

		this.lastFilter = null;
	},
	
	setFilteredTree: function(filteredTree, selectedNodeId) {
		
		var tree = filteredTree.tree;
		this.lastLoadedTree = tree;
		var isEmptyTree = tree.nodes.length <= 0;
		if (isEmptyTree) {
			this.treeDiv.hide();
			this.noResultsDiv.show();
		} else {
			this.noResultsDiv.hide();
			this.treeDiv.show();
			var data = this.getTreeDataFromTree(tree, selectedNodeId, filteredTree.matchingNodeIds);
			if (this.lastTreeElementInstance) {
				this.lastTreeElementInstance.remove();
			}
			this.lastTreeElementInstance = this.getNewTreePlaceholder().jstree({
				'core': {
					'data': data,
					'multiple': false,
					"check_callback": true
				},
				"plugins": [
					"dnd", "search", "state", "types", "wholerow"
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
			  }.bind(this))
			  ;
		}

		this.lastFilter = filteredTree.filter;
	},
	
	moveNode: function(nodeId, newParentId) {
		var updatedNode = this.lastLoadedTree.nodes[nodeId];
		this.uiController.updateNodeParent(updatedNode, newParentId);
	},
	
	selectNode: function(nodeId) {
		var selectedNode = null;
		if (nodeId !== undefined && this.lastLoadedTree != null && this.lastLoadedTree.nodes[nodeId] != null) {
			selectedNode = this.lastLoadedTree.nodes[nodeId];
		}
		this.uiController.selectNode(selectedNode);
	},
	
	getNewTreePlaceholder: function() {
		var id = "treePlaceholder" + this.nextTreeElementId++;
		$('#jstree_div').append('<div id="' + id + '"></div>');
		return $('#' + id);
	},
	
	refresh: function(selectedNodeId) {
		// selectedNodeId is null if refresh triggered by delete
		// TODO if tree is filtered and selectedNodeId is not visible, select root; if root also not visible, do nothing
		this.processFilter(this.lastFilter);
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
				var selected = (currentNode.id == parseInt(selectedNodeId));
				var disabled = !(enabledNodeIds == null || enabledNodeIds.includes(currentNode.id));
				var nodeTreeData = {
					id: currentNode.id,
					text: currentNode.name,
					state: {
						opened: false,
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
		/*
		[
			{
				'text' : 'Root node',
				'state' : {
					'opened' : true,
					'selected' : true
				},
				'children' :
				[
					{
						'text' : 'Child 1'
					},
					'Child 2'
				]
			}
		]
		*/
	}
}

