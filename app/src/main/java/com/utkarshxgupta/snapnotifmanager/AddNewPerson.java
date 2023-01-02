package com.utkarshxgupta.snapnotifmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.utkarshxgupta.snapnotifmanager.Model.WhitelistModel;
import com.utkarshxgupta.snapnotifmanager.R;
import com.utkarshxgupta.snapnotifmanager.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class AddNewPerson extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newPersonText;
    private Button newPersonSaveButton;
    private DatabaseHandler db;
    private List<WhitelistModel> names;

    public static AddNewPerson newInstance() {
        return new AddNewPerson();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_name, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newPersonText = getView().findViewById(R.id.newTaskText);
        newPersonSaveButton = getView().findViewById(R.id.newTaskButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newPersonText.setText(task);
            if (task.length()>0) {
                newPersonSaveButton.setEnabled(true);
            }
        }
        newPersonText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    newPersonSaveButton.setEnabled(false);
                }
                else {
                    newPersonSaveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        newPersonSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newPersonText.getText().toString();
                if (personNotExists(text)) {
                    if (finalIsUpdate) {
                        db.updatePerson(bundle.getInt("id"), text);
                    }
                    else {
                        WhitelistModel person = new WhitelistModel();
                        person.setTask(text);
                        db.insertPerson(person);
                    }
                    dismiss();
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Person already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean personNotExists(String title) {
        names = db.getAllPersons();
        for (WhitelistModel name:names) {
            if (name.getTask().equals(title))
                return false;
        }
        return true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        super.onDismiss(dialog);

        }
    }
}
