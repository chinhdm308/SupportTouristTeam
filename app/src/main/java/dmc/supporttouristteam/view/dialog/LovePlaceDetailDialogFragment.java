package dmc.supporttouristteam.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class LovePlaceDetailDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageButton buttonClose;
    private EditText edtPlaceName, edtPlaceAddress, edtPlaceDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.dialog_love_place_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_close:
                dismiss();
                break;
        }
    }

    private void init() {
        buttonClose = getView().findViewById(R.id.button_close);
        buttonClose.setOnClickListener(this);

        edtPlaceName = getView().findViewById(R.id.edt_place_name);
        edtPlaceName.setText(Common.lovePlaceClicked.getPlaceName());

        edtPlaceAddress = getView().findViewById(R.id.edt_address);
        edtPlaceAddress.setText(Common.lovePlaceClicked.getPlaceAddress());

        edtPlaceDesc = getView().findViewById(R.id.edt_description);
        edtPlaceDesc.setText(Common.lovePlaceClicked.getPlaceDescription());

    }

}
