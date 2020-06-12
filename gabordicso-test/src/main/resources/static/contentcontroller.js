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
const idElementId = "idElement";

var ContentController = function() {
	this.contentContainer = $("#" + contentContainerId);
	this.noContentContainer = $("#" + noContentContainerId);
	this.buttonbar = $("#" + buttonbarId);
	this.addChildButton = $("#" + addChildButtonId);
	this.editButton = $("#" + editButtonId);
	this.deleteButton = $("#" + deleteButtonId);
	this.nameElement = $("#" + nameElementId);
	this.contentElement = $("#" + contentElementId);
	this.idElement = $("#" + idElementId);
	this.nodeDialogNameField = $("#nodeName");
	this.nodeDialogContentField = $("#nodeContent");
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
		this.showNodeDialog(true);
	},

	editButtonClick: function() {
		this.showNodeDialog(false);
	},
	
	showNodeDialog: function(isCreate) {
		
		var name = "";
		var content = "";
		var title = "Create node";
		var callback = this.createNode.bind(this);
		
		if (!isCreate) {
			name = this.node.name;
			content = this.node.content;
			title = "Update node";
			callback = this.updateNode.bind(this);
		}

		var validate = this.validateNodeDialog.bind(this);
		var beforeClose = this.closeNodeDialog.bind(this);
		
		this.nodeDialogNameField.val(name);
		this.nodeDialogContentField.val(content);
		
		$("#nodeDialog").dialog({
			resizable: false,
			height: "auto",
			width: 400,
			modal: true,
			draggable: false,
			title: title,
			buttons: {
				"Save": function() {
					if (validate()) {
						callback();
						beforeClose();
						$(this).dialog("close");
					}
				},
				Cancel: function() {
					beforeClose();
					$(this).dialog("close");
				}
			}
		});
	},
	
	closeNodeDialog: function() {
		this.markDialogFieldAsValid(this.nodeDialogNameField, true);
		this.markDialogFieldAsValid(this.nodeDialogContentField, true);
		$("#validationMessage").hide();
	},

	validateNodeDialog: function() {
		var name = this.nodeDialogNameField.val();
		var content = this.nodeDialogContentField.val();
		var valid = true;
		if (name == null || name.trim() == "") {
			this.markDialogFieldAsValid(this.nodeDialogNameField, false);
			valid = false;
		} else {
			this.markDialogFieldAsValid(this.nodeDialogNameField, true);
		}
		if (content == null || content.trim() == "") {
			this.markDialogFieldAsValid(this.nodeDialogContentField, false);
			valid = false;
		} else {
			this.markDialogFieldAsValid(this.nodeDialogContentField, true);
		}
		if (!valid) {
			$("#validationMessage").show();
		} else {
			$("#validationMessage").hide();
		}
		return valid;
	},
	
	markDialogFieldAsValid: function(field, isValid) {
		field.addClass(isValid ? "nodeData_valid" : "nodeData_invalid");
		field.removeClass(isValid ? "nodeData_invalid" : "nodeData_valid");
	},
	
	createNode: function() {
		var name = this.nodeDialogNameField.val();
		var content = this.nodeDialogContentField.val();
		this.uiController.createNode(this.node, name, content);
	},
	
	updateNode: function() {
		var name = this.nodeDialogNameField.val();
		var content = this.nodeDialogContentField.val();
		this.uiController.updateNode(this.node, name, content);
	},
	
	deleteButtonClick: function() {
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
			this.idElement.html("(id: " + node.id + ")");
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

