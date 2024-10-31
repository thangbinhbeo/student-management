package com.example.pe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pe.R;
import com.example.pe.models.Student;

import java.util.List;
import java.util.Map;

public class StudentAdapter extends ArrayAdapter<Student> {
    private Context context;
    private List<Student> students;
    private Map<Integer, String> majorMap;

    public StudentAdapter(Context context, List<Student> students, Map<Integer, String> majorMap) {
        super(context, 0, students);
        this.context = context;
        this.students = students;
        this.majorMap = majorMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        }

        Student student = students.get(position);

        TextView textID = convertView.findViewById(R.id.textID);
        TextView textName = convertView.findViewById(R.id.textName);
        TextView textMajor = convertView.findViewById(R.id.textMajor);
        ImageView imageGender = convertView.findViewById(R.id.imageGender);

        textID.setText(student.getID() + "");
        textName.setText(student.getName());

        String majorName = majorMap.get(student.getMajorID());
        textMajor.setText(majorName != null ? majorName : "N/A");

        if ("male".equalsIgnoreCase(student.getGender())) {
            imageGender.setImageResource(R.drawable.male);
        } else if ("female".equalsIgnoreCase(student.getGender())) {
            imageGender.setImageResource(R.drawable.female);
        }

        return convertView;
    }
}
