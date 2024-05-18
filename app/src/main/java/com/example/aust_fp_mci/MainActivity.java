package com.example.aust_fp_mci;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private StudentDatabaseHelper dbHelper;
    private TextInputEditText editTextStudentName;
    private ListView listViewStudents;
    private Button btnAddStudent;
    private ArrayAdapter<String> adapter;
    private List<String> studentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        dbHelper = new StudentDatabaseHelper(this);
        editTextStudentName = findViewById(R.id.editTextStudentName);
        listViewStudents = findViewById(R.id.listViewStudents);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        studentList = new ArrayList<>();
        // simple_list_item_1 is generic layout used in the list.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        listViewStudents.setAdapter(adapter);

        btnAddStudent.setOnClickListener(v -> {
            String studentName = editTextStudentName.getText().toString();
            if (!studentName.isEmpty()) {
                addStudentToDatabase(studentName);
                editTextStudentName.setText("");
                loadStudentsFromDatabase();
            }
        });

        loadStudentsFromDatabase();
    }

    private void addStudentToDatabase(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String insertQuery = "INSERT INTO " + StudentDatabaseHelper.TABLE_STUDENTS + " (" +
                StudentDatabaseHelper.COLUMN_NAME + ") VALUES ('" + name + "')";
        db.execSQL(insertQuery);
    }

    private void loadStudentsFromDatabase() {
        // Without it the list duplicate on entry update, so we ensure to clear then repopulate it.
        studentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDatabaseHelper.TABLE_STUDENTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_NAME));
                studentList.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close(); // Release cursor from memory.
        adapter.notifyDataSetChanged(); // Notify the adapter that data set has changed.
    }
}