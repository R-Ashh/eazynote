package com.watchmecoding.eazynote.activity;

import android.Manifest;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.watchmecoding.eazynote.R;
import com.watchmecoding.eazynote.base.PermissionUtility;
import com.watchmecoding.eazynote.data.NoteDataSource;
import com.watchmecoding.eazynote.data.NoteItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int EDITOR_ACTIVITY_REQUEST = 1001;
    private PermissionUtility util;
    private NoteDataSource datasource;
    ArrayList<NoteItem> notesList;
    private ListView listView;
    private ArrayAdapter<NoteItem> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        final FloatingActionButton fbutton = (FloatingActionButton) findViewById(R.id.create_note);
        fbutton.setOnClickListener(this);
        requestPermission();
        initNotes();
        fetch();
        noteEdit();
    }

    private void noteEdit() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteItem note = notesList.get(position);
                openNote(note);
            }
        });

    }

    private void fetch() {
        listView = (ListView) findViewById(R.id.list);
        adapter =
                new ArrayAdapter<>(this, R.layout.list_item_layout, notesList);
        listView.setAdapter(adapter);
    }

    private void refreshDisplay() {
        if (adapter != null && notesList != null) {
            notesList.clear();
            notesList.addAll(datasource.findAll());
            adapter.notifyDataSetChanged();
        }
    }

    private void initNotes() {
        datasource = new NoteDataSource(this);
        notesList = datasource.findAll();
    }


    private void requestPermission() {
        util = new PermissionUtility();

        util.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, this, new PermissionUtility.CallBack() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onNotGranted() {
            }
        });
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        util.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_note:
                addNote();
                break;
            default:
                break;
        }
    }

    private void addNote() {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    private void openNote(NoteItem item) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.setAction(new Gson().toJson(item));
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            refreshDisplay();
        }
    }
}
