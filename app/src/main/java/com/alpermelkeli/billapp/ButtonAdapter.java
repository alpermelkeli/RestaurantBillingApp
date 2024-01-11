package com.alpermelkeli.billapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    private List<String> masaList;
    private OnButtonClickListener listener;

    public interface OnButtonClickListener {
        void onButtonClicked(String masaId);
    }

    public ButtonAdapter(List<String> masaList, OnButtonClickListener listener) {
        this.masaList = masaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);
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
            btnMasa.setText(masaId);
        }
    }

    public String getMasaId(int position) {
        return masaList.get(position);
    }
}
