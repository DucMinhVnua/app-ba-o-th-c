package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countdowntimer.R;
import com.example.countdowntimer.Todolist;

import java.util.List;

public class AdapterToDoList extends RecyclerView.Adapter<AdapterToDoList.ListViewHolder>{

    Todolist context;
    List<ObjItemToDoList> mList;

    public AdapterToDoList(Todolist context, List<ObjItemToDoList> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todolist, parent, false);
        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterToDoList.ListViewHolder holder, int position) {
        ObjItemToDoList objItemToDoList = mList.get(position);
        if (objItemToDoList == null) {
            return;
        }

        holder.congViec.setText(objItemToDoList.getCongViec());
        holder.date.setText(objItemToDoList.getDate());

        // ------------ Button
        holder.edtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.DiaLogEdit(objItemToDoList.getId());
            }
        });

        holder.edtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.DiaLogDelete(objItemToDoList.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView congViec, date;
        ImageButton edtEdit, edtDelete;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            congViec = itemView.findViewById(R.id.tv_cv);
            date = itemView.findViewById(R.id.tv_date);
            edtEdit = itemView.findViewById(R.id.btn_edit);
            edtDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
