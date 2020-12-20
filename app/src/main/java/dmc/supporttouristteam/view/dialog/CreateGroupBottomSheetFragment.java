package dmc.supporttouristteam.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import dmc.supporttouristteam.data.model.User;
import dmc.supporttouristteam.presenter.add_group.AddGroupContract;
import dmc.supporttouristteam.R;

public class CreateGroupBottomSheetFragment extends BottomSheetDialogFragment {
    private EditText edtGroupName;
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

        edtGroupName = view.findViewById(R.id.edt_group_name);
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
                if (edtGroupName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Tên nhóm trống", Toast.LENGTH_SHORT).show();
//                    StringBuilder name = new StringBuilder();
//                    for (User i : selectedParticipantList) {
//                        String[] tmp = i.getDisplayName().split(" ");
//                        name.append(tmp[tmp.length - 1] + ",");
//                    }
//                    name.deleteCharAt(name.length() - 1);
//                    presenter.getInteractor().createGroup(name.toString(), selectedParticipantList, 1);
                } else {
                    presenter.getInteractor().createGroup(edtGroupName.getText().toString(), selectedParticipantList, 1);
                }
            }
        });
    }
}
