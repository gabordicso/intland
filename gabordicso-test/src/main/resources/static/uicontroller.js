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

		this.contentController.init(this);
		this.contentController.setNode({ name: "Test name", content: "Test content", id: 2, parentId: 1 });
		// this.contentController.setNode({ name: "Test name", content: "Test content", id: 1, parentId: 1 });
		// this.contentController.setNode(null);
		
		this.treePane = $("#" + treePaneDivId);
		this.contentPane = $("#" + contentPaneDivId);
		this.treeContainer = $("#" + treeContainerDivId);
		this.contentContainer = $("#" + contentContainerDivId);
		
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
		this.contentController.uninit();
	},

	deleteNode: function(id) {
		this.onDeleteStart();
		this.restClient.deleteNode(id, this.deleteNodeDone.bind(this), this.deleteNodeFail.bind(this), this.deleteNodeAlways.bind(this));
	},
	
	onDeleteStart: function() {
		$("body").LoadingOverlay("show", this.getLoadingOverlayOptions());
	},
	
	deleteNodeDone: function() {
		this.showInfo("Node deleted");
		this.treeController.refresh();
		this.contentController.setNode(null);
	},
	
	deleteNodeFail: function() {
		this.showError("Node could not be deleted");
	},
	
	deleteNodeAlways: function() {
		this.onDeleteEnd();
	},
	
	onDeleteEnd: function() {
		$("body").LoadingOverlay("hide", this.getLoadingOverlayOptions());
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
	
	onFilterButtonClick: function() {
		var filter = this.filterTextbox.val();
		if (filter == null) {
			filter = "";
		}
		filter = filter.trim();
		if (filter == "") {
			this.loadTree();
		} else {
			this.loadFilteredTree(filter);
		}
	},
	
	loadTree: function() {
		this.onTreeLoadStart();
		this.restClient.loadTree(this.loadTreeDone.bind(this), this.loadTreeFail.bind(this), this.loadTreeAlways.bind(this));
	},
	
	loadTreeDone: function(tree) {
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
	
	onClearFilterButtonClick: function() {
		this.filterTextbox.val("");
		this.onFilterButtonClick();
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