package model;

import java.util.HashMap;
import java.util.Map;

public class DisjointSet<T>
{
	private Map<T, TreeNode> map;
	
	public DisjointSet()
	{
		map = new HashMap<T, TreeNode>();
	}
	
	public T merge(T left, T right)
	{
		TreeNode leftRoot = findLoop(map.get(left));
		TreeNode rightRoot = findLoop(map.get(right));
		if(leftRoot == rightRoot) return leftRoot.value;
		if(leftRoot.rank < rightRoot.rank)
		{
			leftRoot.parent = rightRoot;
			return rightRoot.value;
		}
		else if(leftRoot.rank > rightRoot.rank)
			rightRoot.parent = leftRoot;
		else
		{
			rightRoot.parent = leftRoot;
			leftRoot.rank++;
		}
		return leftRoot.value;
	}
	
	public T find(T member)
	{
		TreeNode current = map.get(member);
		return findLoop(current).value;
	}
	
	private TreeNode findLoop(TreeNode current)
	{
		if(current.parent != current)
			current.parent = findLoop(current.parent);
		return current.parent;
	}
	
	public boolean add(T element)
	{
		if(map.containsKey(element)) return false;
		map.put(element, new TreeNode(element));
		return true;
	}
	
	private class TreeNode
	{
		public TreeNode parent;
		public T value;
		public int rank;
		
		public TreeNode(T value)
		{
			this.value = value;
			parent = this;
			rank = 0;
		}
	}
}
