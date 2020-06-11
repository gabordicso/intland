const rootNodeId = 1;
const delBtnEnabledClass = "btn_a";
const delBtnDisabledClass = "btn_a_disabled";

const contentContainerId = "content_container";
const noContentContainerId = "noContent_container";
const buttonbarId = "buttonbar";
const addChildButtonId = "addChildButton";
const editButtonId = "editButton";
const deleteButtonId = "deleteButton";
const nameElementId = "nameDisplay";
const contentElementId = "contentDisplay";

var ContentController = function() {
	this.contentContainer = $("#" + contentContainerId);
	this.noContentContainer = $("#" + noContentContainerId);
	this.buttonbar = $("#" + buttonbarId);
	this.addChildButton = $("#" + addChildButtonId);
	this.editButton = $("#" + editButtonId);
	this.deleteButton = $("#" + deleteButtonId);
	this.nameElement = $("#" + nameElementId);
	this.contentElement = $("#" + contentElementId);
}

ContentController.prototype = {
	init: function(uiController) {
		this.uiController = uiController;
		this.addChildButton.click(this.addChildButtonClick.bind(this));
		this.editButton.click(this.editButtonClick.bind(this));
		this.deleteButton.click(this.deleteButtonClick.bind(this));
	},
	
	uninit: function() {
		this.addChildButton.off("click");
		this.editButton.off("click");
		this.deleteButton.off("click");
	},
	
	addChildButtonClick: function() {
		this.uiController.showInfo("addChildButtonClick");
		
	},

	editButtonClick: function() {
		this.uiController.showInfo("editButtonClick");
		
	},

	deleteButtonClick: function() {
		this.uiController.showInfo("deleteButtonClick");
		
		var deleteConfirm = this.deleteConfirm.bind(this);
		$("#deleteCurrentNodeConfirm").dialog({
			resizable: false,
			height: "auto",
			width: 400,
			modal: true,
			draggable: false,
			buttons: {
				"Delete node": function() {
					deleteConfirm();
					$(this).dialog("close");
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
	},
	
	deleteConfirm: function() {
		this.uiController.deleteNode(this.node.id);
	},

	setNode: function(node) {
		this.node = node;
		if (node == null) {
			this.buttonbar.hide();
			this.contentContainer.hide();
			this.noContentContainer.show();
		} else {
			this.noContentContainer.hide();
			this.buttonbar.show();
			this.contentContainer.show();
			this.nameElement.html(node.name);
			this.contentElement.val(node.content);
			var isRootNode = (node.id == rootNodeId);
			if (isRootNode) {
				this.disableDeleteButton();
			} else {
				this.enableDeleteButton();
			}
		}
	},
	
	enableDeleteButton: function() {
		this.deleteButton.addClass(delBtnEnabledClass);
		this.deleteButton.removeClass(delBtnDisabledClass);
		this.deleteButton.click(this.deleteButtonClick.bind(this));
	},
	
	disableDeleteButton: function() {
		this.deleteButton.addClass(delBtnDisabledClass);
		this.deleteButton.removeClass(delBtnEnabledClass);
		this.deleteButton.off("click");
	}
}

