package com.krab.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.MyViewHolder> {

	private Context context;
	private ArrayList<Entry> taskList;
	private Activity activity;

	public TaskItemAdapter(Context context, ArrayList<Entry> taskList) {
		this.context = context;
		this.taskList = taskList;
		this.activity = (Activity)context;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.task_entry, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		holder.taskID.setText(String.valueOf(position + 1));
		holder.title.setText(taskList.get(position).title);
		holder.description.setText(taskList.get(position).description);
		holder.executionTime.setText(taskList.get(position).getExecutionTime());
		String notificationTxt = taskList.get(position).notification == 0 ? "Powiadomienia: wyłączone" : "Powiadomienia: włączone";
		String statusTxt = taskList.get(position).status == 0 ? "Status: nieukończone" : "Status: ukończone";
		holder.notifications.setText(notificationTxt);
		holder.status.setText(statusTxt);
		holder.attachment.setVisibility(taskList.get(position).file.equals("")?View.INVISIBLE:View.VISIBLE);
		holder.taskEntry.setOnClickListener(v -> {
			Intent intent = new Intent(context, UpdateActivity.class);
			intent.putExtra("id", taskList.get(position).id);
			intent.putExtra("title", taskList.get(position).title);
			intent.putExtra("description", taskList.get(position).description);
			intent.putExtra("notifications", taskList.get(position).notification);
			intent.putExtra("category", taskList.get(position).category);
			intent.putExtra("status", taskList.get(position).status);
			intent.putExtra("executionTime", taskList.get(position).getExecutionTimeInMillis());
			intent.putExtra("file" , taskList.get(position).file);
			activity.startActivityForResult(intent, 1);
		});
	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {

		TextView taskID, title, description, executionTime, notifications, status, attachment;
		LinearLayout taskEntry;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			taskID = itemView.findViewById(R.id.taskID);
			title = itemView.findViewById(R.id.title);
			description = itemView.findViewById(R.id.description);
			executionTime = itemView.findViewById(R.id.creationTime);
			notifications = itemView.findViewById(R.id.notificationsStatus);
			status = itemView.findViewById(R.id.taskStatus);
			taskEntry = itemView.findViewById(R.id.taskEntry);
			attachment = itemView.findViewById(R.id.attachment);
		}
	}
}
