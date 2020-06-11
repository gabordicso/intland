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
		
		this.treePane = $("#" + treePaneDivId);
		this.contentPane = $("#" + contentPaneDivId);
		this.treeContainer = $("#" + treeContainerDivId);
		this.contentContainer = $("#" + contentContainerDivId);
		
		this.filterTextbox = $("#filterTextbox");
		this.filterButton = $("#filterButton");
		this.clearFilterButton = $("#clearFilterButton");

		this.filterButton.click(this.onFilterButtonClick.bind(this));
		this.clearFilterButton.click(this.onClearFilterButtonClick.bind(this));
		
		$("#testbtn").click(this.onTreeLoadStart.bind(this)); // TODO remove
	},

	showInfo: function(info) {
		Topper({
			title: 'Info',
			text: info,
			style: 'info',
			type: 'top',
			autoclose: true,
			autocloseAfter: 5000
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
		return { fade: [100, 100] };
	}
}