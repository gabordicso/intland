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
		this.contentController.setNode({ name: "Test name", content: "Test content", id: 2, parentId: 1 });
		// this.contentController.setNode({ name: "Test name", content: "Test content", id: 1, parentId: 1 });
		// this.contentController.setNode(null);
		
		this.treePane = $("#" + treePaneDivId);
		this.contentPane = $("#" + contentPaneDivId);
		this.treeContainer = $("#" + treeContainerDivId);
		this.contentContainer = $("#" + contentContainerDivId);
		
	},
	
	uninit: function() {
		this.treeController.uninit();
		this.contentController.uninit();
	},

	onNodeManipulationStart: function() {
		$("body").LoadingOverlay("show", this.getLoadingOverlayOptions());
	},
	
	onNodeManipulationEnd: function() {
		$("body").LoadingOverlay("hide", this.getLoadingOverlayOptions());
	},
	
	createNode: function(parentNode, name, content) {
		this.onNodeManipulationStart();
		var node = {
			id: null,
			parentId: parentNode.id,
			name: name,
			content: content
		};
		this.restClient.createNode(node, this.createNodeDone.bind(this), this.createNodeFail.bind(this), this.createNodeAlways.bind(this));
	},
	
	createNodeDone: function(result) {
		this.showInfo("Node created");
		this.treeController.refresh(); // TODO if newly created node does not match an active filter, it won't be shown in the tree and should not be selectable
		this.contentController.setNode(result); // TODO check if result can be used directly
	},
	
	createNodeFail: function(xhr, status, errorThrown) {
		this.showError("Node could not be created");
	},
	
	createNodeAlways: function(xhr, status) {
		this.onNodeManipulationEnd();
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
		this.treeController.refresh();
		this.contentController.setNode(result); // TODO check if result can be used directly
	},
	
	updateNodeFail: function(xhr, status, errorThrown) {
		this.showError("Node could not be updated");
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
		this.treeController.refresh();
		this.contentController.setNode(null);
	},
	
	deleteNodeFail: function(xhr, status, errorThrown) {
		this.showError("Node could not be deleted");
	},
	
	deleteNodeAlways: function(xhr, status) {
		this.onNodeManipulationEnd();
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
	
	loadTree: function() {
		this.onTreeLoadStart();
		this.restClient.loadTree(this.loadTreeDone.bind(this), this.loadTreeFail.bind(this), this.loadTreeAlways.bind(this));
	},
	
	loadTreeDone: function(tree) {
		this.treeController.setTree(tree);
		this.showInfo("Tree loaded");
	},
	
	loadTreeFail: function(xhr, status, errorThrown) {
		this.showError("Could not load tree, please try again");
	},
	
	loadTreeAlways: function(xhr, status) {
		this.onTreeLoadEnd();
	},
	
	loadFilteredTree: function(filter) {
		this.onTreeLoadStart();
		this.restClient.loadFilteredTree(filter, this.loadFilteredTreeDone.bind(this), this.loadFilteredTreeFail.bind(this), this.loadTreeAlways.bind(this));
	},
	
	loadFilteredTreeDone: function(filteredTree) {
		this.showInfo("Filtered tree loaded");
	},
	
	loadFilteredTreeFail: function(xhr, status, errorThrown) {
		this.showError("Could not load filtered tree, please try again");
	},
	
	onTreeLoadStart: function() {
		this.treeContainer.hide();
		this.treePane.LoadingOverlay("show", this.getLoadingOverlayOptions());
//		setTimeout(this.onTreeLoadEnd.bind(this), 2000); // TODO remove
	},

	onTreeLoadEnd: function() {
		this.treePane.LoadingOverlay("hide", this.getLoadingOverlayOptions());
		this.treeContainer.show();
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
		return { fade: [100, 100], zIndex: 60009021 };
	}
}