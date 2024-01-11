package com.alpermelkeli.billapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ButtonAdapterKasa extends RecyclerView.Adapter<ButtonAdapterKasa.ButtonViewHolder> {

    private List<String> masaList;
    private OnButtonClickListener listener;

    public interface OnButtonClickListener {
        void onButtonClicked(String masaId);
    }

    public ButtonAdapterKasa(List<String> masaList, OnButtonClickListener listener) {
        this.masaList = masaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button_kasa, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        String masaId = masaList.get(position);
        holder.bind(masaId);
    }

    @Override
    public int getItemCount() {
        return masaList.size();
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {

        private Button btnMasa;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMasa = itemView.findViewById(R.id.helvakat);

            btnMasa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String masaId = getMasaId(position);
                        listener.onButtonClicked(masaId);
                    }
                }
            });
        }

        public void bind(String masaId) {
            System.out.println(masaId);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference masaRef = firestore.collection("Masalar").document(masaId);
            masaRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Double fiyat = documentSnapshot.getDouble("toplamFiyat");
                        if (fiyat != null) {
                            btnMasa.append("\nFiyat: " + fiyat.toString());
                        }

                        // Update the button text to display the price information

                        btnMasa.setText(masaId + "\nFiyat: " + fiyat);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the failure case
                }
            });
        }
    }

    public String getMasaId(int position) {
        return masaList.get(position);
    }
}
