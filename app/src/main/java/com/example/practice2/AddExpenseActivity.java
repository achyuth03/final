package com.example.practice2;
import com.google.firebase.firestore.DocumentReference;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editExpenseTitle, editTotalAmount;
    private LinearLayout participantsContainer;
    private Button btnAddParticipant, btnSaveExpense;
    private ArrayList<ParticipantView> participantViews = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

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

    // Method to save the expense in Firestore
    private void saveExpense() {
        String title = editExpenseTitle.getText().toString();
        String totalAmountStr = editTotalAmount.getText().toString();
        if (title.isEmpty() || totalAmountStr.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = Double.parseDouble(totalAmountStr);

        // Create a map to represent the expense
        Map<String, Object> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("totalAmount", totalAmount);

        // Add participants to the expense map
        ArrayList<Map<String, Object>> participants = new ArrayList<>();
        for (ParticipantView pv : participantViews) {
            Map<String, Object> participant = new HashMap<>();
            participant.put("name", pv.getName());
            participant.put("contribution", pv.getContribution());
            participants.add(participant);
        }
        expense.put("participants", participants);

        // Get a reference to the "expenses" collection in Firestore
        CollectionReference expensesRef = db.collection("expenses");

        // Save the expense data to Firestore
        // Save the expense data to Firestore
        expensesRef.add(expense)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Successfully saved the document
                        Toast.makeText(AddExpenseActivity.this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save the expense
                        Toast.makeText(AddExpenseActivity.this, "Failed to save expense.", Toast.LENGTH_SHORT).show();
                    }
                });
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

        public String getName() {
            return editName.getText().toString();
        }

        public double getContribution() {
            String contributionStr = editContribution.getText().toString();
            return contributionStr.isEmpty() ? 0.0 : Double.parseDouble(contributionStr);
        }

        public void setContribution(double amount) {
            editContribution.setText(String.format("%.2f", amount));
        }
    }
}
