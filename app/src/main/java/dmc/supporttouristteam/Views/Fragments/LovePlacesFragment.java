package dmc.supporttouristteam.Views.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.LovePlace;
import dmc.supporttouristteam.Presenters.LovePlaces.LovePlacesAdapter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;
import dmc.supporttouristteam.Views.Activitis.FindNearbyPlacesActivity;

public class LovePlacesFragment extends Fragment implements View.OnClickListener{

    private Button buttonFindPlace;
    private RecyclerView recyclerLovePlace;
    private TextView textShow;

    private DatabaseReference lovePlacesRef;

    private List<LovePlace> lovePlaceList;

    private LovePlacesAdapter lovePlacesAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);


        lovePlacesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LovePlace lovePlace = dataSnapshot.getValue(LovePlace.class);
                        lovePlaceList.add(lovePlace);
                        lovePlacesAdapter.notifyDataSetChanged();

                    }
                    recyclerLovePlace.setVisibility(View.VISIBLE);
                    textShow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init(View view) {
        buttonFindPlace = view.findViewById(R.id.button_find_place);
        buttonFindPlace.setOnClickListener(this);

        recyclerLovePlace = view.findViewById(R.id.recycler_love_places);
        recyclerLovePlace.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLovePlace.setHasFixedSize(true);

        textShow = view.findViewById(R.id.text_show);

        lovePlacesRef = FirebaseDatabase.getInstance().getReference(Config.RF_LOVE_PLACES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        lovePlaceList = new ArrayList<>();
        lovePlacesAdapter = new LovePlacesAdapter(lovePlaceList);
        recyclerLovePlace.setAdapter(lovePlacesAdapter);

        recyclerLovePlace.setVisibility(View.INVISIBLE);
        textShow.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_love_places, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_find_place:
                Intent intent = new Intent(getContext(), FindNearbyPlacesActivity.class);
                startActivity(intent);
                break;
        }
    }
}