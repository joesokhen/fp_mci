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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
        adapter = new ArrayAdapter<>(this, R.layout.student_list_item, R.id.textViewDate, studentList);
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
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String insertQuery = "INSERT INTO " + StudentDatabaseHelper.TABLE_STUDENTS + " (" +
                StudentDatabaseHelper.COLUMN_NAME + ", " + StudentDatabaseHelper.COLUMN_DATE + ") VALUES ('" + name + "', '" + currentDate + "')";
        db.execSQL(insertQuery);
    }

    private void loadStudentsFromDatabase() {
        // Without it the list duplicate on entry update, so we ensure to clear then repopulate it.
        studentList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + StudentDatabaseHelper.TABLE_STUDENTS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StudentDatabaseHelper.COLUMN_NAME);
            int dateColumnIndex = cursor.getColumnIndex(StudentDatabaseHelper.COLUMN_DATE);
            if (nameColumnIndex != -1 && dateColumnIndex != -1) { // Check if column indices are valid
                do {
                    String name = cursor.getString(nameColumnIndex);
                    String date = cursor.getString(dateColumnIndex);
                    studentList.add(date + " - " + name); // Combine date and name for display
                } while (cursor.moveToNext());
            }
        }
        cursor.close(); // Release cursor from memory.
        adapter.notifyDataSetChanged(); // Notify the adapter that data set has changed.
    }
}