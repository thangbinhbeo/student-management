package com.example.pe;

import android.content.Intent;
import android.os.Bundle;

import com.example.pe.adapters.MajorAdapter;
import com.example.pe.adapters.StudentAdapter;
import com.example.pe.api.APIClient;
import com.example.pe.api.MajorService;
import com.example.pe.api.Repository;
import com.example.pe.api.StudentService;
import com.example.pe.models.Major;
import com.example.pe.models.Student;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pe.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private StudentService studentService;
    private MajorService majorService;
    private ListView lvStudent, lvMajor;
    private StudentAdapter studentAdapter;
    private List<Student> studentList = new ArrayList<>();
    private Map<Integer, String> majorMap = new HashMap<>();;
    private Button buttonAddStudent, buttonAddMajor, buttonLoginGoogle, buttonLogOut, buttonMap;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentService = Repository.getStudentService();
        majorService = Repository.getMajorService();
        lvStudent = findViewById(R.id.studentList);
        lvMajor = findViewById(R.id.majorList);
        buttonAddStudent = findViewById(R.id.btnAddStuden);
        buttonAddMajor = findViewById(R.id.buttonAddMajor);
        buttonLoginGoogle = findViewById(R.id.buttonLoginGoogle);
        buttonLogOut = findViewById(R.id.buttonLogOut);
        buttonMap = findViewById(R.id.buttonMap);

        buttonMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        });

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StudentsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MajorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        lvStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Student selectedStudent = (Student) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(MainActivity.this, StudentProfile.class);
                intent.putExtra("studentId", selectedStudent.getID());
                startActivity(intent);
                finish();
            }
        });

        lvMajor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Major selectedMajor = (Major) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(MainActivity.this, MajorProfile.class);
                intent.putExtra("majorID", selectedMajor.getMajorID());
                startActivity(intent);
                finish();
            }
        });

        getAllMajors();
        fetchMajors();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            updateUI(account);
        }

        buttonLoginGoogle.setOnClickListener(v -> signIn());
        buttonLogOut.setOnClickListener(v -> signOut());
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> updateUI(null));
    }

    private void fetchMajors(){
        Call<Major[]> call = majorService.getAllMajors();
        call.enqueue(new Callback<Major[]>() {
            @Override
            public void onResponse(Call<Major[]> call, Response<Major[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Major[] majors = response.body();
                    MajorAdapter adapter = new MajorAdapter(MainActivity.this, List.of(majors));
                    lvMajor.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to retrieve majors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Major[]> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllMajors() {
        Call<Major[]> call = majorService.getAllMajors();

        call.enqueue(new Callback<Major[]>() {
            @Override
            public void onResponse(Call<Major[]> call, Response<Major[]> response) {
                if (!response.isSuccessful()) {
                    Log.d("API Error", "Error code: " + response.code());
                    Toast.makeText(MainActivity.this, "Failed to load majors, code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Major[] majors = response.body();
                if (majors != null) {
                    for (Major major : majors) {
                        majorMap.put(major.getMajorID(), major.getMajorName());
                    }
                }

                getAllStudents();
            }

            @Override
            public void onFailure(Call<Major[]> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load majors: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAllStudents() {
        Call<Student[]> call = studentService.getAllStudents();

        call.enqueue(new Callback<Student[]>() {
            @Override
            public void onResponse(Call<Student[]> call, Response<Student[]> response) {
                if (!response.isSuccessful()) {
                    Log.d("API Error", "Error code: " + response.code());
                    Toast.makeText(MainActivity.this, "Failed to load students, code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Student[] students = response.body();
                if (students == null) {
                    Log.d("API Error", "Empty response");
                    return;
                }

                Log.d("Student Data", Arrays.toString(students));
                studentList.addAll(Arrays.asList(students));
                studentAdapter = new StudentAdapter(MainActivity.this, studentList, majorMap);
                lvStudent.setAdapter(studentAdapter);
            }

            @Override
            public void onFailure(Call<Student[]> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load students: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("Google Sign-In", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Đăng nhập thành công
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();

            ((TextView) findViewById(R.id.status)).setText("Hello, " + personName + " (" + personEmail + ")");
            findViewById(R.id.buttonLoginGoogle).setVisibility(View.GONE);
            findViewById(R.id.buttonLogOut).setVisibility(View.VISIBLE);
        } else {
            // Đăng xuất
            ((TextView) findViewById(R.id.status)).setText("Signed out");
            findViewById(R.id.buttonLoginGoogle).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonLogOut).setVisibility(View.GONE);
        }
    }
}