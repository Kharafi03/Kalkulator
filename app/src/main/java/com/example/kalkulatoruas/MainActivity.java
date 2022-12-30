package com.example.kalkulatoruas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<ItemList> mExampleList;
    private RecyclerView mRecyclerView;
    private SharedPreferenceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    TextView texthasil;
    RadioGroup operasiGroup;
    RadioButton tambahRadio, kurangRadio, kaliRadio, bagiRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();
        buildRecyclerView();
        setInsertButton();


        FloatingActionButton buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        operasiGroup = findViewById(R.id.radioGroup);
        tambahRadio = findViewById(R.id.tombolTambah);
        kurangRadio = findViewById(R.id.tombolKurang);
        kaliRadio = findViewById(R.id.tombolKali);
        bagiRadio = findViewById(R.id.tombolBagi);

        texthasil = findViewById(R.id.textViewHasil);

    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mExampleList);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<ItemList>>() {}.getType();
        mExampleList = gson.fromJson(json, type);

        if (mExampleList == null) {
            mExampleList = new ArrayList<>();
        }
    }

    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SharedPreferenceAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setDialog(new SharedPreferenceAdapter.Dialog() {

            @Override
            public void onLongClick(int pos) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Hapus History")
                        .setMessage("Hapus history perhitungan ini?")
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                mExampleList.remove(pos);

                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "History perhitungan dihapus", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            }
        });
    }

    private void setInsertButton() {
        Button buttonInsert = findViewById(R.id.buttonHitung);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText angka1 = findViewById(R.id.editAngka1);
                EditText angka2 = findViewById(R.id.editAngka2);
                insertItem(angka1.getText().toString(), angka2.getText().toString());
                saveData();
            }
        });
    }

    private void insertItem(String angka1, String angka2) {
       if(angka1.length() != 0 && angka2.length() != 0){
           if(tambahRadio.isChecked() || kurangRadio.isChecked() || kaliRadio.isChecked() || bagiRadio.isChecked()){
                int hasil = 0;
                if (tambahRadio.isChecked()) {
                    hasil = Integer.parseInt(angka1) + Integer.parseInt(angka2);
                } else if (kurangRadio.isChecked()) {
                    hasil = Integer.parseInt(angka1) - Integer.parseInt(angka2);
                } else if (kaliRadio.isChecked()) {
                    hasil = Integer.parseInt(angka1) * Integer.parseInt(angka2);
                } else if (bagiRadio.isChecked()) {
                    hasil = Integer.parseInt(angka1) / Integer.parseInt(angka2);
                }

                // memunculkan operasi yang dipilih dari operasiGroup ke dalam hasil
                String operasi = "";
                if (tambahRadio.isChecked()) {
                    operasi = "+";
                } else if (kurangRadio.isChecked()) {
                    operasi = "-";
                } else if (kaliRadio.isChecked()) {
                    operasi = "*";
                } else if (bagiRadio.isChecked()) {
                    operasi = "/";
                }

               texthasil.setText(String.valueOf(hasil));

                mExampleList.add(new ItemList(angka1, operasi, angka2, String.valueOf(hasil)));
                mAdapter.notifyItemInserted(mExampleList.size());
           }
           else{
               Toast.makeText(getApplicationContext(), "Masukkan operasi", Toast.LENGTH_SHORT).show();
           }
       }
       else{
           Toast.makeText(getApplicationContext(), "Masukkan semua angka", Toast.LENGTH_SHORT).show();
       }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteData() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Hapus History")
                .setMessage("Hapus semua history perhitungan?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.clear();
                        editor.apply();
                        mExampleList.clear();

                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "History perhitungan dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}