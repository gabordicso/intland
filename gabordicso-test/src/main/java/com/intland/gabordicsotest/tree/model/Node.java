package com.intland.gabordicsotest.tree.model;

import java.util.Set;

public class Node {
	private Long id;
	private Long parentId;
	private String name;
	private String content;
	private Set<Long> children;

	public Node() { }

	public Node(Long id, Long parentId, String name, String content, Set<Long> children) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.content = content;
		this.children = children;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public Set<Long> getChildren() {
		return children;
	}
	public void setChildren(Set<Long> children) {
		this.children = children;
	}
	
	public void addChild(Long childId) {
		this.children.add(childId);
	}
	
	public void removeChild(Long childId) {
		this.children.remove(childId);
	}
}
