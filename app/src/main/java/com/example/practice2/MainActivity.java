package com.example.practice2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Floating Action Button
        FloatingActionButton fabAddExpense = findViewById(R.id.fab_add_expense);

        // Set an onClick listener to navigate to AddExpenseActivity
        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //Navigate to the Add Expense screen
                 Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                 startActivity(intent);
            }
        });
    }
}
