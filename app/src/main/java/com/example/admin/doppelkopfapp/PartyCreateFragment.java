package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.InputStream;
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
    private TextInputEditText groupText;
    private List<TextInputEditText> playerTexts;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_party_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupText = view.findViewById(R.id.party_create_group_text);

        playerTexts = new ArrayList<>();
        LinearLayout layout = view.findViewById(R.id.party_create_player_layout);

        for(int i = 0; i < 10; i++) {
            ConstraintLayout playerLayout =
                    (ConstraintLayout) View.inflate(getContext(), R.layout.new_player, null);
            TextInputEditText editText = playerLayout.findViewById(R.id.new_player_text);
            int req = i < 4 ? R.string.player_required : R.string.player_optional;
            editText.setHint(getString(R.string.player) + " " + (i+1) + " " + getString(req));
            playerTexts.add(editText);
            layout.addView(playerLayout);
        }

        Button createButton = view.findViewById(R.id.party_create_create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onCreateParty(getParty());
                } catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancelButton = view.findViewById(R.id.party_create_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreatePartyCancel();
            }
        });

        imageButton = view.findViewById(R.id.party_create_image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 100);
                intent.putExtra("outputY", 100);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("return-data", true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_group_image)), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                this.bitmap = bitmap;
                imageButton.setImageBitmap(bitmap);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Party getParty() {
        Party party = new Party(getGroupName(),getPlayers(), MyUtils.getDate());
        if(bitmap != null) {
            party.setImage(bitmap);
        }
        return party;
    }


    private String getGroupName(){
        String name = groupText.getText().toString();
        if(name.trim().isEmpty()) {
            throw new RuntimeException(getString(R.string.error_required_field_not_initialized));
        } else {
            return name.trim();
        }
    }

    private List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for(int i = 0; i < playerTexts.size(); i++) {
            TextInputEditText editText = playerTexts.get(i);
            String name = editText.getText().toString();
            if(name.trim().isEmpty()) {
                if(i < 4)
                    throw new RuntimeException(getString(R.string.error_required_field_not_initialized));
            } else {
                players.add(new Player( editText.getText().toString().trim()));
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
