package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSettingsChangeListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private Party party;

    private RadioGroup bocksRadio;
    private CheckBox soloBockBox;
    private Button saveButton, cancelButton;

    private OnSettingsChangeListener settingsChangeListener;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(Party party) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            party = (Party) getArguments().getSerializable(MainActivity.ARG_PARTY);
        }

        getActivity().setTitle(getString(R.string.settings_frament_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bocksRadio = view.findViewById(R.id.settings_bocks);
        bocksRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton button = getView().findViewById(checkedId);
                int bocks = bockValue(button.getText().toString());
                if (bocks == 0) {
                    soloBockBox.setChecked(false);
                    soloBockBox.setEnabled(false);
                } else {
                    soloBockBox.setEnabled(true);
                }
            }
        });

        soloBockBox = view.findViewById(R.id.settings_bock_solo);
        soloBockBox.setChecked(party.getSettings().isSoloBockCalculation() && party.getSettings().getMaxBocks()!=0);

        cancelButton = view.findViewById(R.id.settings_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsCancelled();
            }
        });

        saveButton = view.findViewById(R.id.settings_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameSettings settings = new GameSettings(getMaxBocks(), isSoloBockCalculation());
                onSettingsChanged(settings);
            }
        });

        RadioButton bockRadio = getBockRadio(party.getSettings().getMaxBocks());
        bockRadio.setChecked(true);

        ConstraintLayout headerLayout = view.findViewById(R.id.settings_group_header);

        TextView groupText = (TextView) headerLayout.getViewById(R.id.group_header_name);
        groupText.setText(party.getName());

        if(party.getImageBytes() != null) {
            ImageView groupImage = (ImageView) headerLayout.getViewById(R.id.group_header_image);
            groupImage.setImageBitmap(party.getImage());
        }


    }

    private int getMaxBocks() {
        RadioButton button = getView().findViewById(bocksRadio.getCheckedRadioButtonId());
        String text = button.getText().toString();
        return bockValue(text);
    }

    private int bockValue(String text) {
        if(text.equals(getResources().getString(R.string.settings_bocks_single))) {
            return 1;
        } else if(text.equals(getResources().getString(R.string.settings_bocks_double))) {
            return 2;
        } else {
            return 0;
        }
    }

    private RadioButton getBockRadio(int bocks) {
        if(bocks == 1) {
            return getView().findViewById(R.id.settings_bock1);
        } else if(bocks == 2) {
            return getView().findViewById(R.id.settings_bock2);
        } else {
            return getView().findViewById(R.id.settings_bock0);
        }
    }

    private boolean isSoloBockCalculation() {
        if(soloBockBox.isEnabled()) {
            return soloBockBox.isChecked();
        }
        return false;
    }

    public void onSettingsChanged(GameSettings settings) {
        if (settingsChangeListener != null) {
            settingsChangeListener.onSettingsSaved(settings);
        }
    }

    public void onSettingsCancelled(){
        if(settingsChangeListener != null) {
            settingsChangeListener.onSettingsCancelled();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsChangeListener) {
            settingsChangeListener = (OnSettingsChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        settingsChangeListener = null;
    }

    public interface OnSettingsChangeListener {
        void onSettingsSaved(GameSettings settings);
        void onSettingsCancelled();
    }
}
