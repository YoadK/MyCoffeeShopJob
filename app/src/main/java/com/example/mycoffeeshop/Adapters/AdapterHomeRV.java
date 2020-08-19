package com.example.mycoffeeshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycoffeeshop.Models.ProductModel;
import com.example.mycoffeeshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdapterHomeRV extends RecyclerView.Adapter<AdapterHomeRV.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description, price, weight;
        private EditText numEditText;
        private ImageView imageCoffee;
        private ImageButton imgAdd, imgDecrease;
        private Button addToCart_btn;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            weight = itemView.findViewById(R.id.weight);
            imageCoffee = itemView.findViewById(R.id.imageCoffee);
            numEditText = itemView.findViewById(R.id.item_chosen_amount_of_coffee);
            imgAdd = itemView.findViewById(R.id.plus_coffee_amount);
            imgDecrease = itemView.findViewById(R.id.minus_coffee_amount);
            addToCart_btn = itemView.findViewById(R.id.addToCart_btn);
        }

    }

    private ArrayList<ProductModel> list_data;// holds all products
    private Context context;
    private final LayoutInflater mInflater;
    private int numQuantity = 1;
    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //c'tor
    public AdapterHomeRV(ArrayList<ProductModel> list_data, Context context) {
        this.list_data = list_data;
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.coffee_product_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ProductModel localProductModel = list_data.get(position);
        holder.title.setText(localProductModel.getTitle());
        holder.description.setText(localProductModel.getDescription());
        holder.price.setText(String.valueOf(localProductModel.getPrice()));
        holder.weight.setText(String.valueOf(localProductModel.getWeight_unit()));
        Glide.with(mInflater.getContext()).load(localProductModel.getImg()).into(holder.imageCoffee);
        holder.numEditText.setText(String.valueOf(numQuantity));

        holder.imgAdd.setOnClickListener(v -> {
            numQuantity++;
            holder.numEditText.setText(String.valueOf(numQuantity));
        });

        holder.imgDecrease.setOnClickListener(v -> {
            if (numQuantity > 1) {
                numQuantity--;
                holder.numEditText.setText(String.valueOf(numQuantity));
            }
        });

        //what happens when user presses "add to cart"
        holder.addToCart_btn.setOnClickListener((View v) -> {
            Map<String, Object> userShoppingCartProduct = new HashMap<>();
            userShoppingCartProduct.put("title", localProductModel.getTitle());
            userShoppingCartProduct.put("description", localProductModel.getDescription());
            userShoppingCartProduct.put("price", localProductModel.getPrice());
            userShoppingCartProduct.put("weight_unit", localProductModel.getWeight_unit());
            userShoppingCartProduct.put("img", localProductModel.getImg());
            userShoppingCartProduct.put("quantity", numQuantity);

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef
                    .collection("users")
                    .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                    .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                    .document(localProductModel.getTitle());
            docIdRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Toast.makeText(mInflater.getContext(), "הפריט קיים כבר ברשימת סל הקניות", Toast.LENGTH_SHORT).show();
                    } else {
                        fireStoreDB
                                .collection("users")
                                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                                .document(localProductModel.getTitle())
                                .set(userShoppingCartProduct)
                                .addOnSuccessListener(aVoid -> Toast.makeText(mInflater.getContext(), "פעולת הוספת הפריט לסל הקניות,הצליחה", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(mInflater.getContext(), "שגיאה בהוספת הפריט לסל הקניות", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(mInflater.getContext(), "שגיאה בהוספת הפריט", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

}
