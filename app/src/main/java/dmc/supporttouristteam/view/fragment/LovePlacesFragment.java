package dmc.supporttouristteam.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.data.model.LovePlace;
import dmc.supporttouristteam.view.adapter.LovePlacesAdapter;
import dmc.supporttouristteam.presenter.love_places.LovePlacesContract;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;
import dmc.supporttouristteam.view.activity.FindNearbyPlacesActivity;
import dmc.supporttouristteam.view.dialog.LovePlaceDetailDialogFragment;

public class LovePlacesFragment extends Fragment implements View.OnClickListener, LovePlacesContract.View {

    private Button buttonFindPlace;
    private RecyclerView recyclerLovePlace;
    private TextView textShow;
    private FloatingActionButton fab;

    private DatabaseReference lovePlacesRef;

    private List<LovePlace> lovePlaceList;

    private LovePlacesAdapter lovePlacesAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        lovePlacesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    lovePlaceList.clear();
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

    private void init() {
        buttonFindPlace = getView().findViewById(R.id.button_find_place);
        buttonFindPlace.setOnClickListener(this);

        recyclerLovePlace = getView().findViewById(R.id.recycler_love_places);
        recyclerLovePlace.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLovePlace.setHasFixedSize(true);

        textShow = getView().findViewById(R.id.text_show);

        lovePlacesRef = FirebaseDatabase.getInstance().getReference(Common.RF_LOVE_PLACES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        lovePlaceList = new ArrayList<>();
        lovePlacesAdapter = new LovePlacesAdapter(lovePlaceList, this);
        recyclerLovePlace.setAdapter(lovePlacesAdapter);

        recyclerLovePlace.setVisibility(View.INVISIBLE);
        textShow.setVisibility(View.VISIBLE);

        fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(this);
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
            case R.id.fab:
                Dialog dialogLoading = new Dialog(getContext());
                dialogLoading.setContentView(R.layout.dialog_loading);

                if (dialogLoading.getWindow() != null) {
                    dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                dialogLoading.show();
                break;
        }
    }

    @Override
    public void showLovePlaceDetail(int pos) {
        Common.lovePlaceClicked = lovePlaceList.get(pos);
        FragmentManager fragmentManager = getFragmentManager();
        LovePlaceDetailDialogFragment newFragment = new LovePlaceDetailDialogFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }
}