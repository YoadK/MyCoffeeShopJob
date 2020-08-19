package com.example.mycoffeeshop.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycoffeeshop.OthersPackage.EmailPasswordPhoneValidator;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.ViewModels.UserProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private static final String TAG = "UserProfileFragment-Chk";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private UserProfileViewModel userProfileViewModel;
    //buttons
    private Button btnEditDetails;
    private Button btnSaveDetails;
    //edit-text fields
    private EditText name;
    private EditText dateOfBirth;
    private EditText phoneNumber;
    private TextView emailAddress;
    private EditText streetName;
    private EditText streetNumber;
    private EditText city;
    private EditText zipCode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_userprofile, container, false);

        initUI();
        initListeners();
        getReadFirebase();

        return mView;
    }

    private void initUI() {
        btnEditDetails = mView.findViewById(R.id.userProfileButtonEditDetails);
        btnSaveDetails = mView.findViewById(R.id.userProfileButtonSaveDetails);

        emailAddress = mView.findViewById(R.id.userProfileEmail);
        name = mView.findViewById(R.id.userProfileName);
        dateOfBirth = mView.findViewById(R.id.userProfileDateOfBirth);
        phoneNumber = mView.findViewById(R.id.userProfilePhoneNumber);
        streetName = mView.findViewById(R.id.userProfileStreetName);
        streetNumber = mView.findViewById(R.id.userProfileStreetNumber);
        city = mView.findViewById(R.id.userProfileCity);
        zipCode = mView.findViewById(R.id.userProfileZipCode);

        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);

        emailAddress.setText(currentFirebaseUser.getEmail());

        disableAllFieldsForEdit();
    }

    private void initListeners() {
        btnSaveDetails.setOnClickListener(this);
        btnEditDetails.setOnClickListener(this);
    }

    private void getReadFirebase() {
        DocumentReference docRef = userProfileViewModel.readFirebase().collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    name.setText(document.getString("fullName"));
                    dateOfBirth.setText(document.getString("dateOfBirth"));
                    phoneNumber.setText(document.getString("phoneNumber"));
                    streetName.setText(document.getString("streetName"));
                    streetNumber.setText(document.getString("streetNumber"));
                    city.setText(document.getString("city"));
                    zipCode.setText(document.getString("zipCode"));
                }
            } else {
                Toast.makeText(mView.getContext(), "שגיאה בקבלת המידע", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableAllFieldsForEdit() {
        // init all fields - edit fields enabled
        name.setEnabled(true);
        dateOfBirth.setEnabled(true);
        phoneNumber.setEnabled(true);
        streetName.setEnabled(true);
        streetNumber.setEnabled(true);
        city.setEnabled(true);
        zipCode.setEnabled(true);
    }

    private void disableAllFieldsForEdit() {
        // init all fields - edit fields disabled
        name.setEnabled(false);
        dateOfBirth.setEnabled(false);
        phoneNumber.setEnabled(false);
        streetName.setEnabled(false);
        streetNumber.setEnabled(false);
        city.setEnabled(false);
        zipCode.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userProfileButtonEditDetails:

                btnSaveDetails.setVisibility(View.VISIBLE);

                enableAllFieldsForEdit();
                break;
            case R.id.userProfileButtonSaveDetails:
                //save all data..
                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                DocumentReference docRef = fireStoreDB.collection("users")
                        .document(uid);
                Map<String, Object> user = new HashMap<>();

                //hold the text that the user has entered
                String temp_fullname = name.getText().toString();//2
                String temp_dateofbirth = dateOfBirth.getText().toString();//3
                String temp_phonenumber = phoneNumber.getText().toString();//4
                String temp_streetname = streetName.getText().toString();//5
                String temp_streetnumber = streetNumber.getText().toString();//6
                String temp_city = city.getText().toString();//7
                String temp_zipcode = zipCode.getText().toString();//8

                //get text and add it to temporary user
                if (EmailPasswordPhoneValidator.getInstance().isValidPhoneNumber(temp_phonenumber) && !phoneNumber.getText().toString().isEmpty()) {
                    user.put("fullName", temp_fullname);
                    user.put("dateOfBirth", temp_dateofbirth);
                    user.put("phoneNumber", temp_phonenumber);
                    user.put("streetName", temp_streetname);
                    user.put("streetNumber", temp_streetnumber);
                    user.put("city", temp_city);
                    user.put("zipCode", temp_zipcode);

                    docRef.update(user)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "profile card was successfully created to user: " + temp_fullname + " (" + emailAddress + ") "))
                            .addOnFailureListener(e -> Log.w(TAG, "Error creating profile card for user: " + temp_fullname + " (" + emailAddress + ") ", e));
                } else {
                    Toast.makeText(mView.getContext(), "The phone number is invalid", Toast.LENGTH_SHORT).show();
                }

                disableAllFieldsForEdit();
                break;
        }
    }

}
