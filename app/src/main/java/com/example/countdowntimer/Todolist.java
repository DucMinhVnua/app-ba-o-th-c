package com.example.countdowntimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adapter.AdapterToDoList;
import com.example.adapter.ObjItemToDoList;
import com.example.database.ListDataBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Todolist extends AppCompatActivity {

    Toolbar toolbar;
    ListDataBase listDataBase;
    RecyclerView recyclerView;
    ArrayList<ObjItemToDoList> arrayList;
    AdapterToDoList adapterToDoList;
    Button btnAdd;
    EditText edtAdd;

    String sDate, valueEdtAdd, valueEdtEdit, sWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);
        setStatusBarTransparent();

        init ();

        // Tạo database và tạo bảng
        DataBaseListToDo();

        // ToolBar
        ToolBar ();

        // ToolBar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Todolist.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Add ArrayList
        addArrayList();

        // Adapter
        adapterList();

        // Xử lý button add
        BtnAdd();
    }

    public void init () {
        toolbar = (Toolbar) findViewById(R.id.tbToDoList);
        recyclerView = (RecyclerView) findViewById(R.id.rcv);
        btnAdd = (Button) findViewById(R.id.btn_add);
        edtAdd = (EditText) findViewById(R.id.edt_add);
    }

    public void addArrayList() {
        arrayList = new ArrayList<>();

        // Select table
        SelectTable();
    }

    public void adapterList() {
        adapterToDoList = new AdapterToDoList(this,arrayList);
        recyclerView.setAdapter(adapterToDoList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void ToolBar () {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void DataBaseListToDo() {
        // Khởi tạo database
        listDataBase = new ListDataBase(this, "toDoListDB.sqlite", null, 1);

        // Tạo Bảng
        listDataBase.QueryData("CREATE TABLE IF NOT EXISTS TableToDoList (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Work VARCHAR(200)," +
                " Date DATETIME)");
    }

    public void InsertData() {
        listDataBase.QueryData("INSERT INTO TableToDoList VALUES (null, '"+valueEdtAdd+"', '"+sDate+"')");
        // cập nhật sau khi insert
        arrayList.clear();
        SelectTable ();
        adapterToDoList.notifyDataSetChanged();
    }

    public void BtnAdd () {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                Date date = Calendar.getInstance().getTime();
                sDate = format.format(date);

                if (!TextUtils.isEmpty(edtAdd.getText().toString().trim())) {
                    valueEdtAdd = edtAdd.getText().toString().trim();

                    // Insert Data
                    InsertData();
                    edtAdd.setText("");
                    Toast.makeText(Todolist.this, "successful ADD", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Todolist.this, "Vui lòng nhập công việc", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void SelectTable () {
        Cursor cursor = listDataBase.GetData("SELECT * FROM TableToDoList");
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            sWork = cursor.getString(1);
            String sDate = cursor.getString(2);

            arrayList.add(new ObjItemToDoList(sWork, sDate, id));
        }
    }

    public void DiaLogDelete (int id, int position) {
        Dialog dialog = new Dialog(Todolist.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);

        Button buttonDeleteYes = (Button) dialog.findViewById(R.id.btn_yes);
        Button buttonDeleteNo = (Button) dialog.findViewById(R.id.btn_no);

        buttonDeleteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonDeleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDataBase.QueryData("DELETE FROM TableToDoList WHERE ID = '"+ id +"'");
                dialog.dismiss();

                // cập nhật sau khi xóa
                arrayList.remove(position);
                recyclerView.removeViewAt(position);
                adapterToDoList.notifyItemRemoved(position);
                adapterToDoList.notifyItemRangeChanged(position, arrayList.size());

                Toast.makeText(Todolist.this, "successful Delete", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void DiaLogEdit (int id) {
        Dialog dialog = new Dialog(Todolist.this);
        dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.setCancelable(false);

        Button buttonDeleteYes = (Button) dialog.findViewById(R.id.btn_yes);
        Button buttonDeleteNo = (Button) dialog.findViewById(R.id.btn_no);
        EditText edtEdit = (EditText) dialog.findViewById(R.id.edt_edit);

        Cursor cursor = listDataBase.GetData("SELECT Work FROM TableToDoList Where Id='"+id+"'");
        while (cursor.moveToNext()){
            sWork = cursor.getString(0);
        }
        edtEdit.setText(sWork);

        buttonDeleteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonDeleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtEdit.getText().toString().trim())) {
                    valueEdtEdit = edtEdit.getText().toString().trim();

                    listDataBase.QueryData("UPDATE TableToDoList SET Work = '"+valueEdtEdit+"' WHERE ID = '"+ id +"'");
                    dialog.dismiss();

                    // cập nhật sau khi edit
                    arrayList.clear();
                    SelectTable();
                    adapterToDoList.notifyDataSetChanged();
                    Toast.makeText(Todolist.this, "successful fix", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Todolist.this, "Vui lòng nhập chỉnh sửa", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    // Thanh trạng thái trong suốt
    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            View decorView = window.getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}