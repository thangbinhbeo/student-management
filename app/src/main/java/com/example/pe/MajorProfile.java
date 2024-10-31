package com.example.pe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.api.MajorService;
import com.example.pe.api.Repository;
import com.example.pe.models.Major;
import com.example.pe.models.Student;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MajorProfile extends AppCompatActivity {

    private EditText edName;
    private Button buttonUpdateData, btnBack, btnDelete;
    private boolean isEditable = false;
    private MajorService majorService;
    private int majorID;
    private Major major;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.major_profile);

        edName = findViewById(R.id.edName);
        buttonUpdateData = findViewById(R.id.buttonUpdateData);
        btnBack = findViewById(R.id.buttonFullList);
        btnDelete = findViewById(R.id.buttonDelete);

        majorService = Repository.getMajorService();
        majorID = getIntent().getIntExtra("majorID", -1);
        if (majorID != -1) {
            loadMajorData(majorID);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MajorProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMajor();
            }
        });

        buttonUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditable();
            }
        });
    }

    private void loadMajorData(int majorID) {
        Call<Major> call = majorService.getMajor(majorID);
        call.enqueue(new Callback<Major>() {
            @Override
            public void onResponse(Call<Major> call, Response<Major> response) {
                if (response.isSuccessful() && response.body() != null) {
                    major = response.body();
                    fillMajorData(major);
                } else {
                    Toast.makeText(MajorProfile.this, "Failed to load major data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major> call, Throwable t) {
                Toast.makeText(MajorProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillMajorData(Major major) {
        edName.setText(major.getMajorName());

        edName.setEnabled(false);
    }

    private void toggleEditable() {
        isEditable = !isEditable;
        setFieldsEditable(isEditable);
        if (isEditable) {
            buttonUpdateData.setText("SAVE");
        } else {
            buttonUpdateData.setText("UPDATE");

            updateMajorData();
        }
    }

    private void setFieldsEditable(boolean editable) {
        edName.setEnabled(editable);
    }

    private void updateMajorData(){
        String name = edName.getText().toString();

        Major major = new Major(majorID, name);
        Call<Major> call = majorService.updateMajor(majorID, major);
        call.enqueue(new Callback<Major>() {
            @Override
            public void onResponse(Call<Major> call, Response<Major> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MajorProfile.this, "Major updated successfully!", Toast.LENGTH_SHORT).show();

                    setFieldsEditable(false);
                    buttonUpdateData.setText("UPDATE");
                } else {
                    Toast.makeText(MajorProfile.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major> call, Throwable t) {
                Toast.makeText(MajorProfile.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMajor() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Call<Void> call = majorService.deleteMajor(majorID);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MajorProfile.this, "Major deleted successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MajorProfile.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(MajorProfile.this, "Deletion failed!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MajorProfile.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
