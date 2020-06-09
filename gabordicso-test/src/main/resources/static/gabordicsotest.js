function test() {
	var url = "node";
	var name = $("#name").val();
	var content = $("#content").val();
	var parentId = $("#parentId").val();
	var data = JSON.stringify({ "name": name, "content": content, "parentId": parentId, "id": null, "children": null });
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
}

$(document).ready(function() {
});