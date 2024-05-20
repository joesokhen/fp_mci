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
import android.widget.SimpleCursorAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.ContentValues;

public class MainActivity extends AppCompatActivity {

    private StudentDatabaseHelper dbHelper;
    private TextInputEditText editTextStudentName;
    private ListView listViewStudents;
    private Button btnAddStudent;


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
        // Content Values is an easy & straightforward alternative to INSERT QUERY.
        ContentValues values = new ContentValues();
        values.put(StudentDatabaseHelper.COLUMN_NAME, name);
        values.put(StudentDatabaseHelper.COLUMN_DATE, getCurrentDateTime());
        values.put(StudentDatabaseHelper.COLUMN_STATUS, 1); // Set status to 1 indicating attendance
        db.insert(StudentDatabaseHelper.TABLE_STUDENTS, null, values);
    }

    private void loadStudentsFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + StudentDatabaseHelper.TABLE_STUDENTS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.student_list_item,
                cursor,
                new String[] { StudentDatabaseHelper.COLUMN_DATE, StudentDatabaseHelper.COLUMN_NAME, StudentDatabaseHelper.COLUMN_STATUS },
                new int[] { R.id.textViewDate, R.id.textViewName, R.id.statusIndicator },
                0
        );

        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (view.getId() == R.id.statusIndicator) {
                int status = cursor1.getInt(columnIndex);
                view.setBackgroundResource(status == 1 ? R.drawable.circle_green : R.drawable.circle_red);
                return true;
            }
            return false;
        });

        listViewStudents.setAdapter(adapter); // SimpleCursorAdapter notify changes.
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}