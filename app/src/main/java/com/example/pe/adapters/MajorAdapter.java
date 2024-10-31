package com.example.pe.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pe.R;
import com.example.pe.models.Major;

import java.util.List;

public class MajorAdapter extends ArrayAdapter<Major> {
    private Context context;
    private List<Major> majors;

    public MajorAdapter(Context context, List<Major> majors) {
        super(context, 0, majors);
        this.context = context;
        this.majors = majors;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Major major = majors.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.major_item, parent, false);
        }

        if (convertView == null) {
            Log.e("MajorAdapter", "convertView is null");
        }

        TextView textView = convertView.findViewById(R.id.textName);
        TextView textViewID = convertView.findViewById(R.id.textID);

        if (textView == null || textViewID == null) {
            Log.e("MajorAdapter", "TextViews are null");
        }

        textViewID.setText("ID: " + major.getMajorID());
        textView.setText(major.getMajorName());
        return convertView;
    }
}
