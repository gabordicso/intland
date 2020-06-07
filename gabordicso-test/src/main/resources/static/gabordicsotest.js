function test() {
	var url = "node";
	var data = JSON.stringify({ "name": "name", "content": "content", "parentId": "1", "id": null, "children": null });
	var success = null;
	var dataType = "json";
	var contentType = "application/json";
	$.ajax({
		  type: "POST",
		  url: url,
		  data: data,
		  success: success,
		  dataType: dataType,
		  contentType: contentType
		});
	/*$.post("node", '{name: "name", content: "content", parentId: 1}').then(function(data) {
    	window.alert("then, result: " + result)
    });*/
}

$(document).ready(function() {
});