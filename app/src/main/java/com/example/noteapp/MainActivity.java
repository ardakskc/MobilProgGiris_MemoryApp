package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    protected ArrayList<Notes> list = new ArrayList<>();
    protected ImageButton new_note;
    protected Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recylerView);
        db = new Database(this);
        list = db.getAll();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    11);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListAdapter(getApplicationContext(),list));
        new_note = (ImageButton) findViewById(R.id.new_note);
        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Write_Note.class);
                try {
                    startActivity(i);
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error in start activity.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            list=db.getAll();
            recyclerView.setAdapter(new ListAdapter(getApplicationContext(),list));
        }
    }

}