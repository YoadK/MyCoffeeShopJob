package com.example.mycoffeeshop.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoffeeshop.Adapters.AdapterHomeRV;
import com.example.mycoffeeshop.Models.ProductModel;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.ViewModels.CoffeeAssistantViewModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CoffeeAssistantFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private RadioGroup radio_group_1, radio_group_2, radio_group_3, radio_group_4;
    private ArrayList<String> arrayListRadioGroup;
    private ArrayList<ProductModel> arrayListOriginal; // the original product list (the whole product list)
    private ArrayList<ProductModel> arrayListFiltered; //the product list of the filtered products
    private CoffeeAssistantViewModel coffeeAssistantViewModel;
    private String answer1, answer2, answer3, answer4;
    private boolean isEqualLists;
    private RecyclerView rv;
    private AdapterHomeRV adapter;
    private Button btnAssistant;
    private ScrollView scrollViewAssistant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coffee_assistant, container, false);

        initUI();
        initListeners();
        initRecyclerView();
        initRadioGroups();

        return mView;
    }

    private void initUI() {
        rv = mView.findViewById(R.id.recyclerViewAssistant);
        btnAssistant = mView.findViewById(R.id.btnAssistant);
        scrollViewAssistant = mView.findViewById(R.id.scrollViewAssistant);
        radio_group_1 = mView.findViewById(R.id.radio_group_1);
        radio_group_2 = mView.findViewById(R.id.radio_group_2);
        radio_group_3 = mView.findViewById(R.id.radio_group_3);
        radio_group_4 = mView.findViewById(R.id.radio_group_4);

        arrayListRadioGroup = new ArrayList<>();
        arrayListRadioGroup.clear();
        arrayListOriginal = new ArrayList<>();
        arrayListFiltered = new ArrayList<>();

        coffeeAssistantViewModel = new CoffeeAssistantViewModel();

        rv.setVisibility(View.GONE);

        answer1 = "espresso";
        answer2 = "milk";
        answer3 = "mild";
        answer4 = "regular";
    }

    private void initListeners() {
        btnAssistant.setOnClickListener(this);
    }

    private void initRecyclerView() {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(mView.getContext()));
    }

    private void initRadioGroups() {
        radio_group_1.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button1_1) {
                answer1 = "espresso";
            } else if (checkedId == R.id.radio_button2_1) {
                answer1 = "moka";
            } else if (checkedId == R.id.radio_button3_1) {
                answer1 = "filter";
            }
        });

        radio_group_2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button1_2) {
                answer2 = "milk";
            } else if (checkedId == R.id.radio_button2_2) {
                answer2 = "syrup";
            } else if (checkedId == R.id.radio_button3_2) {
                answer2 = "sugar";
            } else if (checkedId == R.id.radio_button4_2) {
                answer2 = "non-dairy";
            } else if (checkedId == R.id.radio_button5_2) {
                answer2 = "mix";
            } else if (checkedId == R.id.radio_button6_2) {
                answer2 = "none";
            }
        });

        radio_group_3.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button1_3) {
                answer3 = "mild";
            } else if (checkedId == R.id.radio_button2_3) {
                answer3 = "standard";
            } else if (checkedId == R.id.radio_button3_3) {
                answer3 = "strong";
            }
        });

        radio_group_4.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button1_4) {
                answer4 = "regular";
            } else if (checkedId == R.id.radio_button2_4) {
                answer4 = "decaf";
            }
        });
    }

    private void getReadFirebase() {
        coffeeAssistantViewModel.readFirebase()
                .collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListOriginal.clear();
                        arrayListFiltered.clear();

                        for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            ProductModel productItem = doc.toObject(ProductModel.class);
                            assert productItem != null;
                            productItem.setId(doc.getId());
                            arrayListOriginal.add(productItem);
                        }

                        for (int i = 0; i < arrayListOriginal.size(); i++) {
                            isEqualLists = arrayListOriginal.get(i).getKeyword().containsAll(arrayListRadioGroup);

                            Log.i("check", String.valueOf(isEqualLists));
                            if (isEqualLists) {
                                arrayListFiltered.add(arrayListOriginal.get(i));
                            }
                        }

                        adapter = new AdapterHomeRV(arrayListFiltered, mView.getContext());
                        rv.setAdapter(adapter);
                    } else {
                        Toast.makeText(mView.getContext(), "שגיאה בקבלת המידע", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAssistant:
                arrayListRadioGroup.add(answer1);
                arrayListRadioGroup.add(answer2);
                arrayListRadioGroup.add(answer3);
                arrayListRadioGroup.add(answer4);
                //taking care of de-caffein coffee selection by user.
                if (arrayListRadioGroup.contains("decaffeinated")) {
                    arrayListRadioGroup.clear();
                    arrayListRadioGroup.add("decaffeinated");
                }

                scrollViewAssistant.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

                getReadFirebase();
                break;
        }//switch END
    }//onClick END

}
