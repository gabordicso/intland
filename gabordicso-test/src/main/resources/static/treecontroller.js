const noTreeContainerId = "noTree_container";

var TreeController = function() { }

TreeController.prototype = {
	init: function(uiController) {
		this.uiController = uiController;
		
		this.lastFilter = null;

		this.filterTextbox = $("#filterTextbox");
		this.filterButton = $("#filterButton");
		this.clearFilterButton = $("#clearFilterButton");

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
	
	setTree: function(tree) {
		var data = this.getTreeDataFromTree(tree);
		this.lastFilter = null;
	},
	
	setFilteredTree: function(filteredTree) {
		this.lastFilter = filteredTree.filter;
	},
	
	refresh: function() {
		this.processFilter(this.lastFilter); // may cause inconsistencies if user updates input field but does not click Filter, and refresh is triggered; should use cached last filter
		// TODO refresh depending on last action (filter or full load)
	},
	
	getTreeDataFromTree: function(tree) {
		var data = Array();
		var nodes = [];
		if (Array.isArray(tree)) {
			tree.forEach(function(node) {
				nodes[node.id] = node;
			});
		}
		var currentNodeId = rootNodeId;
		var currentNode = nodes[currentNodeId];
		
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

