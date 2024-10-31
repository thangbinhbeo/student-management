package com.example.pe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.api.MajorService;
import com.example.pe.api.Repository;
import com.example.pe.models.Major;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MajorActivity extends AppCompatActivity {
    private EditText edName;
    private Button buttonSaveData;
    private Button buttonFullList;

    private MajorService majorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_major);

        edName = findViewById(R.id.edName);
        buttonSaveData = findViewById(R.id.buttonSaveData);
        buttonFullList = findViewById(R.id.buttonFullList);
        majorService = Repository.getMajorService();

        buttonFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MajorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSaveData.setOnClickListener(v -> addMajor());
    }

    private void addMajor() {
        String name = edName.getText().toString();

        Major major = new Major(0, name);
        Call<Major> call = majorService.createMajor(major);
        call.enqueue(new Callback<Major>() {
            @Override
            public void onResponse(Call<Major> call, Response<Major> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MajorActivity.this, "Add major successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(MajorActivity.this, "Add major failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major> call, Throwable t) {
                Toast.makeText(MajorActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        edName.setText("");
    }
}
