const baseUrl = "http://localhost:8080/";

const url_tree = baseUrl + "tree/";
const url_filteredTree = baseUrl + "filtered-tree/";
const url_node = baseUrl + "node/";

const method_GET = "GET";
const method_POST = "POST";
const method_PUT = "PUT";
const method_DELETE = "DELETE";

var RESTClient = function() { }

RESTClient.prototype = {
	doCall: function(url, method, data, done, fail, always) {
		$.ajax({
			url: url,
			type: method,
			data: data,
			contentType: "application/json"
		})
		.done(done)
		.fail(fail)
		.always(always);
	},

	loadTree: function(done, fail, always) {
		this.doCall(url_tree, method_GET, null, done, fail, always);
	},

	loadFilteredTree: function(filter, done, fail, always) {
		var filterUrlEncoded = encodeURIComponent(filter);
		var url = url_filteredTree + filterUrlEncoded;
		this.doCall(url, method_GET, null, done, fail, always);
	},
	
	getNode: function(id, done, fail, always) {
		var url = url_node + parseInt(id);
		this.doCall(url, method_GET, null, done, fail, always);
	},
	
	createNode: function(node, done, fail, always) {
		var url = url_node;
		var data = JSON.stringify({ "name": node.name, "content": node.content, "parentId": node.parentId, "id": null, "children": null });
		this.doCall(url, method_POST, data, done, fail, always);
	},
	
	updateNode: function(node, done, fail, always) {
		var url = url_node;
		var data = JSON.stringify({ "name": node.name, "content": node.content, "parentId": node.parentId, "id": node.id, "children": null });
		this.doCall(url, method_PUT, data, done, fail, always);
	},
	
	deleteNode: function(id, done, fail, always) {
		var url = url_node + parseInt(id);
		this.doCall(url, method_DELETE, null, done, fail, always);
	},

	initTree: function(done, fail, always) {
		this.doCall(url_tree, method_DELETE, null, done, fail, always);
	}
}
