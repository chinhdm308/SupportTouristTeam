package dmc.supporttouristteam.Presenters.LovePlaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dmc.supporttouristteam.Models.LovePlace;
import dmc.supporttouristteam.R;

public class LovePlacesAdapter extends RecyclerView.Adapter<LovePlacesAdapter.LovePlacesHolder> {

    private List<LovePlace> list;
    private LovePlacesContract.View callback;

    public LovePlacesAdapter(List<LovePlace> list, LovePlacesContract.View callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public LovePlacesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_love_place, parent, false);
        return new LovePlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LovePlacesHolder holder, int position) {
        LovePlace lovePlace = list.get(position);

        holder.textPlaceName.setText(lovePlace.getPlaceName());
        holder.textAddress.setText(lovePlace.getPlaceAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class LovePlacesHolder extends RecyclerView.ViewHolder {
        TextView textPlaceName, textAddress;

        public LovePlacesHolder(@NonNull View itemView) {
            super(itemView);

            textPlaceName = itemView.findViewById(R.id.text_place_name);
            textAddress = itemView.findViewById(R.id.text_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.showLovePlaceDetail();
                }
            });
        }

    }
}
