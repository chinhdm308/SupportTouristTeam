package dmc.supporttouristteam.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Presenters.AddGroup.AddGroupContract;
import dmc.supporttouristteam.R;

public class CreateGroupBottomSheetFragment extends BottomSheetDialogFragment {
    private EditText etGroupName;
    private Button buttonCancel, buttonCreate;
    private List<User> selectedParticipantList;

    private AddGroupContract.Presenter presenter;

    public CreateGroupBottomSheetFragment(List<User> selectedParticipantList, AddGroupContract.Presenter presenter) {
        this.selectedParticipantList = selectedParticipantList;
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etGroupName = view.findViewById(R.id.et_group_name);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonCreate = view.findViewById(R.id.button_create);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etGroupName.getText().toString().isEmpty()) {
                    StringBuilder name = new StringBuilder();
                    for (User i : selectedParticipantList) {
                        String[] tmp = i.getDisplayName().split(" ");
                        name.append(tmp[tmp.length - 1] + ",");
                    }
                    name.deleteCharAt(name.length() - 1);
                    presenter.getInteractor().createGroup(name.toString(), selectedParticipantList);
                } else {
                    presenter.getInteractor().createGroup(etGroupName.getText().toString(), selectedParticipantList);
                }
            }
        });
    }
}
