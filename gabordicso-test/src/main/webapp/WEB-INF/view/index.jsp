<%@ page contentType="text/html; charset = UTF-8"%>
<html>
	<head>
		<link rel="icon" href="favico.png" type="image/png" sizes="16x16"/>
		<title>Gabor Dicso test task for Intland</title>

		<script src="uicontroller.js"></script>
		<script src="treecontroller.js"></script>
		<script src="contentcontroller.js"></script>
		<script src="restclient.js"></script>

		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
		 <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
 		<script src="loadingoverlay.min.js"></script><%/* source: https://gasparesganga.com/labs/jquery-loading-overlay/ */%>
		<script src="topper.js"></script><%/* source: https://www.jqueryscript.net/other/top-notification-bar-topper.html */%>
		<script src="jstree.min.js"></script><%/* source: https://www.jstree.com/api/#/ */%>

		<link rel="stylesheet" href="gabordicsotest.css" />
		<link rel="stylesheet" href="jquery-ui.css">
		<link rel="stylesheet" href="jquerydemos.css">

		<link rel="stylesheet" href="themes/default/style.min.css" /><% /* jstree */ %>
		<link rel="stylesheet" href="topper.css" /><% /* topper */ %>

		<script>

var retries = 0;
var maxRetries = 100;

function showElement(id) {
	if (typeof($) !== "undefined") {
		$("#" + id).css({ "display": "block" });
	} else {
		document.getElementById(id).style.display = "block";
	}
}

function hideElement(id) {
	if (typeof($) !== "undefined") {
		$("#" + id).css({ "display": "none" });
	} else {
		document.getElementById(id).style.display = "none";
	}
}

function waitForScriptLoad() {
	retries++;
	if (allScriptsAreLoaded()) {
		console.log("Scripts loaded");
		onScriptsLoaded();
	} else if (retries > maxRetries) {
		console.log("Scripts not loaded and max retry count reached.");
		hideElement("scriptLoading");
		showElement("scriptLoadError");
	} else {
		console.log("Scripts not loaded yet");
		setTimeout(waitForScriptLoad, 200);
	}
}

function allScriptsAreLoaded() {
	var dependencyTypes = Array();
	
	dependencyTypes.push(typeof($));
	dependencyTypes.push(typeof(UIController));
	dependencyTypes.push(typeof(TreeController));
	dependencyTypes.push(typeof(ContentController));
	dependencyTypes.push(typeof(RESTClient));
	
	var allLoaded = true;
	dependencyTypes.forEach(function(dependencyType) {
		if (dependencyType === "undefined") {
			allLoaded = false;
		}
	});
	return allLoaded;
}

function onScriptsLoaded() {
	$(document).ready(function() {
		new UIController().start();
	});
}

waitForScriptLoad();

		</script>
	</head>
	<body>
		<div id="scriptLoading" style="display:block" class="scriptLoading">Loading...</div>
		<div id="scriptLoadError" style="display:none" class="scriptLoadError">Could not load page properly. Please refresh your browser.</div>
		<div id="app_container" style="display:none" class="app_container">
			<div id="tree_pane" class="tree_pane">
				<div id="tree_pane_inner_container" class="tree_pane_inner_container">
					<div><h1>Tree</h1></div>
					<div id="filterbar" class="filterbar">
						<input type="text" id="filterTextbox" class="filterTextbox" placeholder="Enter filter expression..." />
						<input type="submit" id="filterButton" value="Filter tree" class="btn_primary" />
						<a id="clearFilterButton" class="btn_a" href="javascript: void(0)">Clear filter</a>
					</div>
					<div id="tree_container" class="tree_container" style="display:none;">
						<div id="jstree_div"></div>
					</div>
					<div id="noTree_container" class="tree_container" style="display:block;">
						<h2 class="noContent">No results</h2>
					</div>
				</div>
			</div>
			<div id="content_pane" class="content_pane">
				<div id="content_pane_inner_container" class="content_pane_inner_container">
					<div><h1>Content</h1></div>
					<div id="buttonbar" class="buttonbar">
						<input type="submit" id="addChildButton" value="Add new child to current node" class="btn_primary" />
						<input type="submit" id="editButton" value="Edit current node" class="btn" />
						<a id="deleteButton" class="btn_a_disabled" href="javascript: void(0)">Delete current node</a>
					</div>
					<div id="noContent_container" class="content_container" style="display:block;">
						<h2 class="noContent">Please select a node in the tree</h2>
					</div>
					<div id="content_container" class="content_container" style="display:none;">
						<h2 id="nameDisplay" class="nameDisplay">Name</h2>
						<div id="idElement" class="idElement"></div>
						<textarea id="contentDisplay" class="contentDisplay" disabled=1>Content</textarea>
					</div>
				</div>
			</div>
		</div>

		<div style="display:none;" id="deleteCurrentNodeConfirm" title="Delete current node?">
			<p><span class="ui-icon ui-icon-alert" style="float:left; margin:12px 12px 20px 0;"></span>This node and its children (if any) will be permanently deleted and cannot be recovered. Are you sure?</p>
		</div>
		<div style="display:none;" id="nodeDialog" title="Node Data">
			<p>
				Name:
				<br />
				<input type="text" id="nodeName" class="nodeTextbox" placeholder="Enter node name..." />
				<br />
				Content:
				<br />
				<textarea id="nodeContent" class="content" placeholder="Enter node content..."></textarea>
				<div id="validationMessage" class="validationMessage" style="display:none;">Please fill all fields.</div>
			</p>
		</div>
	</body>
</html>