const baseUrl = "http://localhost:8080/";

const url_tree = baseUrl + "tree";
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
			// success: success,
			dataType: "json",
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
		this.doCall(url_filteredTree + filterUrlEncoded, method_GET, null, done, fail, always);
	}
}
