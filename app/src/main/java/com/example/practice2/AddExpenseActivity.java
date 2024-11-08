package com.example.practice2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editExpenseTitle, editTotalAmount;
    private LinearLayout participantsContainer;
    private Button btnAddParticipant, btnSaveExpense;
    private ArrayList<ParticipantView> participantViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editExpenseTitle = findViewById(R.id.edit_expense_title);
        editTotalAmount = findViewById(R.id.edit_total_amount);
        participantsContainer = findViewById(R.id.participants_container);
        btnAddParticipant = findViewById(R.id.btn_add_participant);
        btnSaveExpense = findViewById(R.id.btn_save_expense);

        // Listener to add new participant dynamically
        btnAddParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParticipantView();
            }
        });

        // Listener to save the expense
        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });

        // Listener to update contributions equally when total amount changes
        editTotalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { updateEqualContributions(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    // Method to add participant views dynamically
    private void addParticipantView() {
        final ParticipantView participantView = new ParticipantView(this);
        participantsContainer.addView(participantView.getView());
        participantViews.add(participantView);
        updateEqualContributions();
    }

    // Method to update all participant contributions equally
    private void updateEqualContributions() {
        String totalStr = editTotalAmount.getText().toString();
        if (totalStr.isEmpty() || participantViews.size() == 0) return;

        double totalAmount = Double.parseDouble(totalStr);
        double equalContribution = totalAmount / participantViews.size();

        for (ParticipantView pv : participantViews) {
            pv.setContribution(equalContribution);
        }
    }

    // Method to save the expense and display a confirmation
    private void saveExpense() {
        String title = editExpenseTitle.getText().toString();
        String totalAmount = editTotalAmount.getText().toString();
        if (title.isEmpty() || totalAmount.isEmpty() || participantViews.size() == 0) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Save expense logic here (e.g., save to a database)
        Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
    }

    // Inner class to handle participant views
    private class ParticipantView {
        private View view;
        private EditText editName, editContribution;

        public ParticipantView(AddExpenseActivity activity) {
            view = activity.getLayoutInflater().inflate(R.layout.activity_add_expense, participantsContainer, false);
            editName = view.findViewById(R.id.edit_participant_name);
            editContribution = view.findViewById(R.id.edit_contribution);
        }

        public View getView() { return view; }

        public void setContribution(double amount) {
            editContribution.setText(String.format("%.2f", amount));
        }
    }
}
