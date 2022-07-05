package com.krab.todolist;

import androidx.annotation.Nullable;

public class Category implements Comparable {
	int id;
	String name;
	boolean checked = true;

	public Category(int id, String name, boolean checked) {
		this.id = id;
		this.name = name;
		this.checked = checked;
	}

	public int getStatus(){
		return	checked?1:0;
	}

	@Override
	public int compareTo(Object o) {
		return name.compareTo(((Category) o).name);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return name.equals(obj);
	}
}
