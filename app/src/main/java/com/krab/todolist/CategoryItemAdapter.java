package com.krab.todolist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.MyViewHolder> {

	Context context;
	ArrayList<Category> categoryList;

	public CategoryItemAdapter(Context context, ArrayList<Category> categoryList) {
		this.context = context;
		this.categoryList = categoryList;
	}

	@NonNull
	@Override
	public CategoryItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.category_entry, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CategoryItemAdapter.MyViewHolder holder, int position) {
		CategoriesDB db = new CategoriesDB(context);
		holder.categoryName.setText(categoryList.get(position).name);
		holder.active.setChecked(categoryList.get(position).checked);

		holder.active.setOnCheckedChangeListener((buttonView, isChecked) -> {
			categoryList.get(position).checked = isChecked;
			db.updateData(categoryList.get(position));
		});

		holder.deleteBTN.setOnClickListener(v -> {
			db.deleteElement(categoryList.get(position).name);
			((SettingsActivity) context).reload();
		});
	}

	@Override
	public int getItemCount() {
		return categoryList.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {

		TextView categoryName;
		ImageButton deleteBTN;
		CheckBox active;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			categoryName = itemView.findViewById(R.id.categoryName);
			deleteBTN = itemView.findViewById(R.id.deleteCategory);
			active = itemView.findViewById(R.id.active);
		}
	}
}
