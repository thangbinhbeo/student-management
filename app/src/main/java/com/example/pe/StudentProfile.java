package com.example.pe;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.api.MajorService;
import com.example.pe.api.Repository;
import com.example.pe.api.StudentService;
import com.example.pe.models.Major;
import com.example.pe.models.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentProfile extends AppCompatActivity {

    private EditText edName, edEmail, edDob, edAddress;
    private Spinner spinnerGender, spinnerMajor;
    private Button buttonUpdateData, btnBack, btnDelete, buttonViewMap;
    private boolean isEditable = false;
    private StudentService studentService;
    private MajorService majorService;
    private int studentId;
    private Student student;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile);

        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edDob = findViewById(R.id.edDob);
        edAddress = findViewById(R.id.edAddress);
        spinnerMajor = findViewById(R.id.spinnerMajor);
        buttonUpdateData = findViewById(R.id.buttonUpdateData);
        btnBack = findViewById(R.id.buttonBack);
        btnDelete = findViewById(R.id.buttonDelete);
        buttonViewMap = findViewById(R.id.buttonViewMap);

        studentService = Repository.getStudentService();
        majorService = Repository.getMajorService();

        studentId = getIntent().getIntExtra("studentId", -1);
        if (studentId != -1) {
            loadStudentData(studentId);
            loadMajors();
        }

        buttonViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentProfile.this, MapActivity.class);
                intent.putExtra("Address", student.getAddress());
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStudent();
            }
        });

        buttonUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditable();
            }
        });

    }

    private void loadMajors() {
        Call<Major[]> call = majorService.getAllMajors();
        call.enqueue(new Callback<Major[]>() {
            @Override
            public void onResponse(Call<Major[]> call, Response<Major[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Major[] majors = response.body();
                    ArrayAdapter<Major> adapter = new ArrayAdapter<>(StudentProfile.this,
                            android.R.layout.simple_spinner_item, majors);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMajor.setAdapter(adapter);

                    setCurrentMajor(student.getMajorID(), majors);
                } else {
                    Toast.makeText(StudentProfile.this, "Failed to load majors!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major[]> call, Throwable t) {
                Toast.makeText(StudentProfile.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCurrentMajor(int majorID, Major[] majors) {
        for (int i = 0; i < majors.length; i++) {
            if (majors[i].getMajorID() == majorID) {
                spinnerMajor.setSelection(i);
                return;
            }
        }

        Toast.makeText(this, "Current major not found!", Toast.LENGTH_SHORT).show();
    }

    private void loadStudentData(int studentId) {
        Call<Student> call = studentService.getStudent(studentId);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful() && response.body() != null) {
                    student = response.body();
                    if (student != null) {
                        int majorID = student.getMajorID();
                    } else {
                        // Handle the case where the student is null
                        Log.e("StudentProfile", "Student object is null");

                    }
                    String address = student.getAddress();
                    setupMapButton(address);
                    fillStudentData(student);

                    loadMajors();


                } else {
                    Toast.makeText(StudentProfile.this, "Failed to load student data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Toast.makeText(StudentProfile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMapButton(String address) {
        buttonViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentProfile.this, MapActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    private void fillStudentData(Student student) {
        if (student == null) {
            Log.e("StudentProfile", "Student object is null");
            return; // Avoid null pointer exceptions
        }
        edName.setText(student.getName());
        edEmail.setText(student.getEmail());
        edDob.setText(formatDate(student.getDate()));
        edAddress.setText(student.getAddress());

        spinnerGender.setSelection(getGenderPosition(student.getGender()));
        if (student.getAddress() != null) {
            setupMapButton(student.getAddress());
        } else {
            Toast.makeText(this, "Address not available!", Toast.LENGTH_SHORT).show();
        }

        setFieldsEditable(false);
    }

    private void toggleEditable() {
        isEditable = !isEditable;
        setFieldsEditable(isEditable);
        if (isEditable) {
            buttonUpdateData.setText("SAVE");
        } else {
            buttonUpdateData.setText("UPDATE");

            updateStudentData();
        }
    }

    private void setFieldsEditable(boolean editable) {
        edName.setEnabled(editable);
        edEmail.setEnabled(editable);
        edDob.setEnabled(false);
        edAddress.setEnabled(editable);
        spinnerGender.setEnabled(editable);
        spinnerMajor.setEnabled(editable);
    }

    private void updateStudentData() {
        String name = edName.getText().toString();
        String email = edEmail.getText().toString();
        String address = edAddress.getText().toString();
        long dob = convertDateToMillis(edDob.getText().toString());
        String gender = spinnerGender.getSelectedItem().toString();
        Major selectedMajor = (Major) spinnerMajor.getSelectedItem();
        int majorID = selectedMajor != null ? selectedMajor.getMajorID() : -1;

        Student updatedStudent = new Student(studentId, name, dob, gender, email, address, majorID);
        Call<Student> call = studentService.updateStudent(studentId ,updatedStudent);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StudentProfile.this, "Student updated successfully!", Toast.LENGTH_SHORT).show();

                    setFieldsEditable(false);
                    buttonUpdateData.setText("UPDATE");
                } else {
                    Toast.makeText(StudentProfile.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Toast.makeText(StudentProfile.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long convertDateToMillis(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = format.parse(dateString);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        edDob.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day);

        datePickerDialog.show();
    }

    private String formatDate(long dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(dob);
        return sdf.format(date);
    }

    private int getGenderPosition(String gender) {
        String[] genders = getResources().getStringArray(R.array.gender_array);
        for (int i = 0; i < genders.length; i++) {
            if (genders[i].equalsIgnoreCase(gender)) {
                return i;
            }
        }
        return 0;
    }

    private void deleteStudent(){
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Call<Void> call = studentService.deleteStudent(studentId);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(StudentProfile.this, "Student deleted successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(StudentProfile.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(StudentProfile.this, "Deletion failed!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(StudentProfile.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
