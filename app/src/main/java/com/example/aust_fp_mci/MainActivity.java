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
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.ContentValues;

public class MainActivity extends AppCompatActivity {

    private StudentDatabaseHelper dbHelper;
    private TextInputEditText editTextStudentName;
    private TextInputEditText editTextLogout;
    private ListView listViewStudents;
    private Button btnAddStudent, btnLogout;

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
        editTextLogout = findViewById(R.id.editTextLogout);
        listViewStudents = findViewById(R.id.listViewStudents);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnLogout = findViewById(R.id.btnLogout);

        btnAddStudent.setOnClickListener(v -> {
            String studentName = editTextStudentName.getText().toString();
            if (!studentName.isEmpty()) {
                addStudentToDatabase(studentName);
                editTextStudentName.setText("");
                loadStudentsFromDatabase();
            }
        });

        btnLogout.setOnClickListener(v -> {
            String studentIdText = editTextLogout.getText().toString();
            if (!studentIdText.isEmpty()) {
                int studentId = Integer.parseInt(studentIdText);
                updateStudentStatus(studentId, 0); // Log out the student by setting status to 0
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
        values.put(StudentDatabaseHelper.COLUMN_STATUS, 1); // Default to logged in (1)
        long newRowId = db.insert(StudentDatabaseHelper.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error adding student", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStudentStatus(int studentId, int newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentDatabaseHelper.COLUMN_STATUS, newStatus);
        int rowsAffected = db.update(StudentDatabaseHelper.TABLE_NAME, values, StudentDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(studentId)});

        if (rowsAffected == 0) {
            Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
            loadStudentsFromDatabase();
        }
    }

    private void loadStudentsFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + StudentDatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.student_list_item,
                cursor,
                new String[] { StudentDatabaseHelper.COLUMN_ID, StudentDatabaseHelper.COLUMN_DATE, StudentDatabaseHelper.COLUMN_NAME, StudentDatabaseHelper.COLUMN_STATUS },
                new int[] { R.id.textViewID, R.id.textViewDate, R.id.textViewName, R.id.statusIndicator },
                0
        );

        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            int viewId = view.getId();
            if (viewId == R.id.statusIndicator) {
                int status = cursor1.getInt(cursor1.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STATUS));
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