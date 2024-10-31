package com.example.pe;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pe.api.MajorService;
import com.example.pe.api.Repository;
import com.example.pe.api.StudentService;
import com.example.pe.models.Major;
import com.example.pe.models.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentsActivity extends AppCompatActivity {

    private Spinner sGender, sMajor;
    private EditText edDate, edName, edEmail, edAddress;
    private Button buttonSaveData;
    private Button buttonFullList;
    private StudentService studentService;
    private MajorService majorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);

        sGender = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sGender.setAdapter(adapter);

        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edDate = findViewById(R.id.edDob);
        edAddress = findViewById(R.id.edAddress);
        sMajor = findViewById(R.id.spinnerMajor);

        edDate.setOnClickListener(v -> showDatePickerDialog());

        buttonSaveData = findViewById(R.id.buttonSaveData);
        buttonFullList = findViewById(R.id.buttonFullList);

        buttonFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonSaveData.setOnClickListener(v -> addStudent());

        studentService = Repository.getStudentService();
        majorService = Repository.getMajorService();
        loadMajors();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        edDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day);

        datePickerDialog.show();
    }

    private void loadMajors() {
        Call<Major[]> call = majorService.getAllMajors();
        call.enqueue(new Callback<Major[]>() {
            @Override
            public void onResponse(Call<Major[]> call, Response<Major[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Major[] majors = response.body();
                    ArrayAdapter<Major> adapter = new ArrayAdapter<>(StudentsActivity.this,
                            android.R.layout.simple_spinner_item, majors);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sMajor.setAdapter(adapter);
                } else {
                    Toast.makeText(StudentsActivity.this, "Failed to load majors!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major[]> call, Throwable t) {
                Toast.makeText(StudentsActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addStudent() {
        String name = edName.getText().toString();
        String email = edEmail.getText().toString();
        long dob = convertDateToMillis(edDate.getText().toString());
        String gender = sGender.getSelectedItem().toString();
        String address = edAddress.getText().toString();

        Major selectedMajor = (Major) sMajor.getSelectedItem();
        int majorID = selectedMajor != null ? selectedMajor.getMajorID() : -1;

        Student student = new Student(0, name, dob, gender, email, address, majorID);

        Call<Student> call = studentService.createStudent(student);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StudentsActivity.this, "Add student successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(StudentsActivity.this, "Add student failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Toast.makeText(StudentsActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void clearFields() {
        edName.setText("");
        edEmail.setText("");
        edDate.setText("");
        edAddress.setText("");
        sMajor.setSelection(0);
        sGender.setSelection(0);
    }
}
