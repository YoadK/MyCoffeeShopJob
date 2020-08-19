package com.example.mycoffeeshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.mycoffeeshop.ViewModels.ShoppingCartViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterShoppingRV extends RecyclerView.Adapter<AdapterShoppingRV.ViewHolder> {

    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();
    private List<ProductModel> list_data; // holds all products
    private Context context;
    private final LayoutInflater mInflater;
    private ShoppingCartViewModel shoppingCartViewModel;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description, price, weight;
        private EditText numEditText;
        private ImageView imageCoffee;
        private ImageButton imgAdd, imgDecrease, deleteBtn;

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
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }

    public AdapterShoppingRV(ArrayList<ProductModel> list_data, Context context) {
        this.list_data = list_data;
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
        shoppingCartViewModel = new ShoppingCartViewModel();
    }

    @NonNull
    @Override
    public AdapterShoppingRV.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.coffee_shopping_adapter, parent, false);
        return new AdapterShoppingRV.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterShoppingRV.ViewHolder holder, final int position) {
        final ProductModel productModel = list_data.get(position);
        holder.title.setText(productModel.getTitle());
        holder.description.setText(productModel.getDescription());
        holder.price.setText(String.valueOf(productModel.getPrice()));
        holder.weight.setText(String.valueOf(productModel.getWeight_unit()));
        Glide.with(mInflater.getContext()).load(productModel.getImg()).into(holder.imageCoffee);
        holder.numEditText.setText(String.valueOf(productModel.getQuantity()));

        // update: 04072020
        holder.imgAdd.setOnClickListener(v -> {
            int currentQty = list_data.get(position).getQuantity();
            int finalQty = currentQty + 1;
            list_data.get(position).setQuantity(finalQty);
            updateDbWithCurrentQuantity(productModel, finalQty);
            notifyDataSetChanged();
        });

        // update: 04072020
        holder.imgDecrease.setOnClickListener(v -> {
            int currentQty = list_data.get(position).getQuantity();
            if (currentQty > 1) {
                int finalQty = currentQty - 1;
                list_data.get(position).setQuantity(finalQty);
                updateDbWithCurrentQuantity(productModel, finalQty);
            }
            notifyDataSetChanged();
        });

        holder.deleteBtn.setOnClickListener(v -> {
            shoppingCartViewModel.readFirebase()
                    .collection("users")
                    .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                    .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                    .document(productModel.getTitle())
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(mInflater.getContext(), "מוחק: " + productModel.getTitle(), Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(mInflater.getContext(), "שגיאה במחיקת הפריט: " + e, Toast.LENGTH_LONG).show());
            removeAt(position);
        });
    }

    private void removeAt(int position) {
        list_data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    private void updateDbWithCurrentQuantity(ProductModel productModel, int theUpdatedQuantity) {
        Map<String, Object> userShoppingCartProduct = new HashMap<>();
        userShoppingCartProduct.put("quantity", theUpdatedQuantity);

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef
                .collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                .document(productModel.getTitle());
        docIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (!document.exists()) {
                    Toast.makeText(mInflater.getContext(), "שגיאה ברשימת סל הקניות- הרשימה אינה קיימת.", Toast.LENGTH_SHORT).show();
                } else {
                    fireStoreDB
                            .collection("users")
                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                            .document(productModel.getTitle())
                            .update(userShoppingCartProduct)
                            .addOnSuccessListener(aVoid -> Toast.makeText(mInflater.getContext(), "פעולת עדכון הכמות בסל הקניות,הצליחה", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(mInflater.getContext(), "שגיאה בעדכון הכמות בסל הקניות", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(mInflater.getContext(), "שגיאה בעדכון סל הקניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
