<%@ page contentType="text/html; charset = UTF-8"%>
<html>
	<head>
		<link rel="icon" href="favico.png" type="image/png" sizes="16x16"/>
		<title>Gabor Dicso test task for Intland</title>

		<script src="uicontroller.js"></script>
		<script src="restclient.js"></script>

		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

		<script src="loadingoverlay.min.js"></script><%/* source: https://gasparesganga.com/labs/jquery-loading-overlay/ */%>
		<script src="topper.js"></script><%/* source: https://www.jqueryscript.net/other/top-notification-bar-topper.html */%>
		<script src="jstree.min.js"></script><%/* source: https://www.jstree.com/api/#/ */%>

		<link rel="stylesheet" href="gabordicsotest.css" />

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
				<div><h1>Content Tree</h1></div>
				<div id="filterbar" class="filterbar">
					<input type="text" id="filterTextbox" class="filterTextbox" />
					<input type="submit" id="filterButton" value="Filter tree" class="btn_primary" />
					<a id="clearFilterButton" class="btn_a" href="javascript: void();">clear filter</a>
				</div>
				<div id="tree_container" class="tree_container">
				
					<div id="jstree_demo_div"></div>
					<input id="testbtn" type="submit" onclick="init()" value="Init" />
				</div>
			</div>
			<div id="content_pane" class="content_pane">
				<div><h1>Content Details</h1></div>
				<div id="content_container" class="content_container">
					<div id="buttonbar">
						<div id="newChildButton" class="btn_primary"></div>
						<div id="editButton" class="btn"></div>
						<div id="deleteButton" class="btn_del"></div>
					</div>
					<div id="content" class="content">
						<div id="content_name" class="content_name"></div>
						<div id="content_content" class="content_content"></div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>