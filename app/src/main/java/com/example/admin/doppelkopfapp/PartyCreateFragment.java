package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PartyCreateFragment.OnPartyCreateListener} interface
 * to handle interaction events.
 * Use the {@link PartyCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartyCreateFragment extends DialogFragment {

    public static int PICK_IMAGE = 23;

    private ImageButton imageButton;
    private Button cancelButton;
    private Button createButton;
    private TextInputEditText groupText;
    private TextInputEditText[] playerTexts;
    private Bitmap bitmap;

    private OnPartyCreateListener partyCreateListener;

    public PartyCreateFragment() {
        // Required empty public constructor
    }

    public static PartyCreateFragment newInstance() {
        PartyCreateFragment fragment = new PartyCreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        getActivity().setTitle(getString(R.string.party_create_fragment_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_party_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupText = view.findViewById(R.id.party_create_group_text);

        int[] playerTextIds = new int[]{R.id.party_create_player_text1, R.id.party_create_player_text2,
                R.id.party_create_player_text3, R.id.party_create_player_text4,
                R.id.party_create_player_text5, R.id.party_create_player_text6};

        playerTexts = new TextInputEditText[6];
        for(int i = 0; i < 6; i++ ) {
            playerTexts[i] = view.findViewById(playerTextIds[i]);
        }

        createButton = view.findViewById(R.id.party_create_create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onCreateParty(getParty());
                } catch(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton = view.findViewById(R.id.party_create_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreatePartyCancel();
            }
        });

        imageButton = view.findViewById(R.id.group_header_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Group Image"), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                this.bitmap = bitmap;
                imageButton.setImageBitmap(Bitmap.createScaledBitmap(
                        bitmap, imageButton.getWidth(), imageButton.getHeight(), true));
            } catch (Exception e) {
                Log.e(getClass().getName(), "Couldn't load Image");
            }
        }
    }

    private Party getParty() {
        Party party = new Party(getGroupName(),getPlayers(), MyUtils.getDate());
        party.setDatabaseId(23232); //todo change databaseid
        if(bitmap != null) {
            party.setImage(bitmap);
        }
        return party;
    }


    private String getGroupName(){
        String name = groupText.getText().toString();
        if(name.isEmpty()) {
            throw new RuntimeException(getString(R.string.error_required_field_not_initialized));
        } else {
            return name;
        }
    }

    private List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for(int i = 0; i < playerTexts.length; i++) {
            TextInputEditText editText = playerTexts[i];
            String name = editText.getText().toString();
            if(name == null || name.isEmpty()) {
                if(i < 4)
                    throw new RuntimeException(getString(R.string.error_required_field_not_initialized));
            } else { //todo change databaseid
                players.add(new Player(i * 23, editText.getText().toString()));
            }
        }
        return players;
    }


    public void onCreateParty(Party party) {
        if (partyCreateListener != null) {
            partyCreateListener.onPartyCreated(party);
        }
    }

    public void onCreatePartyCancel() {
        if(partyCreateListener != null) {
            partyCreateListener.onPartyCreateCancelled();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPartyCreateListener) {
            partyCreateListener = (OnPartyCreateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        partyCreateListener = null;
    }


    public interface OnPartyCreateListener {
        void onPartyCreated(Party party);
        void onPartyCreateCancelled();
    }
}
