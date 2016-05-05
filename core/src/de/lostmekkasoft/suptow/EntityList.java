/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lostmekkasoft.suptow;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author fine
 */
public class EntityList implements Iterable<Entity> {
	
	private LinkedList<Entity> list = new LinkedList<Entity>();
	private LinkedList<Entity> toRemove = new LinkedList<Entity>();
	
	public boolean add(Entity e) {
		if (list.contains(e)) return false;
		return list.add(e);
	}
	
	public void markToRemove(Entity e) {
		toRemove.add(e);
	}
	
	public void applyRemoval() {
		list.removeAll(toRemove);
		toRemove.clear();
	}

	public int size() {
		return list.size();
	}

	public void clear() {
		list.clear();
	}

	@Override
	public Iterator<Entity> iterator() {
		return list.iterator();
	}
	
}
