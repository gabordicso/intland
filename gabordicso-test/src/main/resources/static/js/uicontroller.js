const treePaneDivId = "tree_pane";
const contentPaneDivId = "content_pane";
const treeContainerDivId = "tree_container";
const contentContainerDivId = "content_container";

var UIController = function() { }

UIController.prototype = {
	start: function() {
		console.log("UIController.start()");
		hideElement("scriptLoading");
		showElement("app_container");
		this.init();
	},

	init: function() {
		console.log("UIController.init()");

		this.restClient = new RESTClient();
		this.treeController = new TreeController(this);
		this.contentController = new ContentController();

		this.treeController.init(this);
		this.contentController.init(this);
		
		this.treePane = $("#" + treePaneDivId);
		this.contentPane = $("#" + contentPaneDivId);
		this.treeContainer = $("#" + treeContainerDivId);
		this.contentContainer = $("#" + contentContainerDivId);
		
		this.loadTree();
	},
	
	uninit: function() {
		this.treeController.uninit();
		this.contentController.uninit();
	},



	selectNode: function(selectedNode) {
		this.contentController.setNode(selectedNode);
	},
	


	createNode: function(parentNode, name, content) {
		this.onNodeManipulationStart();
		var parentId = null;
		if (parentNode != null) {
			parentId = parentNode.id;
		}
		var node = {
			id: null,
			parentId: parentId,
			name: name,
			content: content
		};
		this.restClient.createNode(node, this.createNodeDone.bind(this), this.createNodeFail.bind(this), this.createNodeAlways.bind(this));
	},
	
	createNodeDone: function(result) {
		this.showInfo("Node created");
		this.treeController.refresh(result.id); // TODO if newly created node does not match an active filter, it won't be shown in the tree and should not be selectable
		this.contentController.setNode(result);
	},
	
	createNodeFail: function(xhr, status, errorThrown) {
		this.showError("Node could not be created");
		this.treeController.refresh(null);
	},
	
	createNodeAlways: function(xhr, status) {
		this.onNodeManipulationEnd();
	},
	


	updateNodeParent: function(updatedNode, newParentId) {
		this.onNodeManipulationStart();
		var node = {
			id: updatedNode.id,
			parentId: newParentId,
			name: updatedNode.name,
			content: updatedNode.content
		};
		this.restClient.updateNode(node, this.updateNodeDone.bind(this), this.updateNodeFail.bind(this), this.updateNodeAlways.bind(this));
	},

	updateNode: function(updatedNode, name, content) {
		this.onNodeManipulationStart();
		var node = {
			id: updatedNode.id,
			parentId: updatedNode.parentId,
			name: name,
			content: content
		};
		this.restClient.updateNode(node, this.updateNodeDone.bind(this), this.updateNodeFail.bind(this), this.updateNodeAlways.bind(this));
	},
	
	updateNodeDone: function(result) {
		this.showInfo("Node updated");
		this.treeController.refresh(result.id);
		this.contentController.setNode(result); // TODO check if result can be used directly
	},
	
	updateNodeFail: function(xhr, status, errorThrown) {
		this.showError("Node could not be updated");
		this.treeController.refresh(null);
	},
	
	updateNodeAlways: function(xhr, status) {
		this.onNodeManipulationEnd();
	},
	


	deleteNode: function(id) {
		this.onNodeManipulationStart();
		this.restClient.deleteNode(id, this.deleteNodeDone.bind(this), this.deleteNodeFail.bind(this), this.deleteNodeAlways.bind(this));
	},
	
	deleteNodeDone: function(result) {
		this.showInfo("Node deleted");
		this.treeController.refresh(null);
		this.contentController.setNode(null);
	},
	
	deleteNodeFail: function(xhr, status, errorThrown) {
		this.showError("Node could not be deleted");
		this.treeController.refresh(null);
	},
	
	deleteNodeAlways: function(xhr, status) {
		this.onNodeManipulationEnd();
	},
	


	loadTree: function() {
		this.onTreeLoadStart();
		this.restClient.loadTree(this.loadTreeDone.bind(this), this.loadTreeFail.bind(this), this.loadTreeAlways.bind(this));
	},
	
	loadTreeDone: function(tree) {
		this.treeController.setTree(tree, null, null);
		this.treeContainer.show();
		this.showInfo("Tree loaded");
	},
	
	loadTreeFail: function(xhr, status, errorThrown) {
		this.showError("Could not load tree, please try again");
		this.treeController.treeReadError();
		this.treeContainer.hide();
	},
	
	loadTreeAlways: function(xhr, status) {
		this.onTreeLoadEnd();
	},
	
	loadFilteredTree: function(filter) {
		this.onTreeLoadStart();
		this.restClient.loadFilteredTree(filter, this.loadFilteredTreeDone.bind(this), this.loadFilteredTreeFail.bind(this), this.loadTreeAlways.bind(this));
	},
	
	loadFilteredTreeDone: function(filteredTree) {
		this.treeController.setFilteredTree(filteredTree);
		this.treeContainer.show();
		this.showInfo("Filtered tree loaded");
	},
	
	loadFilteredTreeFail: function(xhr, status, errorThrown) {
		this.showError("Could not load filtered tree, please try again");
	},



	initTree: function() {
		this.onTreeLoadStart();
		this.restClient.initTree(this.initTreeDone.bind(this), this.initTreeFail.bind(this), this.initTreeAlways.bind(this));
	},
	
	initTreeDone: function() {
		this.treeController.treeInitialized();
		this.treeContainer.show();
		this.showInfo("Tree initialized");
	},
	
	initTreeFail: function() {
		this.showError("Could not initialize tree, please try again");
	},
	
	initTreeAlways: function() {
		this.onTreeLoadEnd();
	},



	showInfo: function(info) {
		Topper({
			title: 'Info',
			text: info,
			style: 'info',
			type: 'top',
			autoclose: true,
			autocloseAfter: 3000
		});
	},
	
	showError: function(error) {
		Topper({
			title: 'Error',
			text: error,
			style: 'danger',
			type: 'top',
			autoclose: false
		});
	},
	
	onNodeManipulationStart: function() {
		$("body").LoadingOverlay("show", this.getLoadingOverlayOptions());
	},
	
	onNodeManipulationEnd: function() {
		$("body").LoadingOverlay("hide", this.getLoadingOverlayOptions());
	},
	
	onTreeLoadStart: function() {
		this.treeContainer.hide();
		this.treePane.LoadingOverlay("show", this.getLoadingOverlayOptions());
	},

	onTreeLoadEnd: function() {
		this.treePane.LoadingOverlay("hide", this.getLoadingOverlayOptions());
	},

	onContentLoadStart: function() {
		this.contentContainer.hide();
		this.contentPane.LoadingOverlay("show", this.getLoadingOverlayOptions());
	},

	onContentLoadEnd: function() {
		this.contentPane.LoadingOverlay("hide", this.getLoadingOverlayOptions());
		this.contentContainer.show();
	},
	
	getLoadingOverlayOptions: function() {
		return { fade: [100, 100], zIndex: 98 };
	}
}