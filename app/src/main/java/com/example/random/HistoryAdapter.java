package com.example.random;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.random.databinding.ItemHistoryBinding;
import com.example.random.db.HistoryEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryEntity> historyList;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public HistoryAdapter(List<HistoryEntity> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEntity item = historyList.get(position);
        
        String type = item.getType();
        String translatedType = type;
        android.content.Context ctx = holder.binding.getRoot().getContext();
        switch (type) {
            case "NUMBER": translatedType = ctx.getString(R.string.type_number); break;
            case "STRING": translatedType = ctx.getString(R.string.type_string); break;
            case "DICE": translatedType = ctx.getString(R.string.type_dice); break;
            case "COIN": translatedType = ctx.getString(R.string.type_coin); break;
            case "MEME": translatedType = ctx.getString(R.string.type_meme); break;
        }
        
        holder.binding.tvType.setText(translatedType);
        
        String result = item.getResult();
        String displayResult = result;
        
        if (type.equals("COIN")) {
            if (result.equals("HEADS") || result.equals("Heads") || result.equals("Orzeł")) {
                displayResult = ctx.getString(R.string.coin_heads);
            } else if (result.equals("TAILS") || result.equals("Tails") || result.equals("Reszka")) {
                displayResult = ctx.getString(R.string.coin_tails);
            }
        } else if (type.equals("DICE")) {
            if (result.contains(";")) {
                String[] parts = result.split(";", 2);
                try {
                    int count = Integer.parseInt(parts[0]);
                    displayResult = ctx.getString(R.string.dice_history_format, count, parts[1]);
                } catch (Exception e) {
                    // fallback
                }
            } else if (!result.startsWith("Rolled") && !result.startsWith("Wyrzucono")) {
                // If there's another format, keep it as is.
                // It's a legacy pre-formatted string, keep it as is
            }
        }
        
        holder.binding.tvResult.setText(displayResult);
        holder.binding.tvDate.setText(sdf.format(new Date(item.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;
        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
