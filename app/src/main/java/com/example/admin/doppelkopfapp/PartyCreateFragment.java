package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
    private Map<ConstraintLayout, Long> playerLayouts;

    private Party party;
    private boolean isNew = false;
    private Bitmap bitmap;

    private OnPartyCreateListener partyCreateListener;

    public PartyCreateFragment() {
        // Required empty public constructor
    }

    public static PartyCreateFragment newInstance(Party party) {
        PartyCreateFragment fragment = new PartyCreateFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.party = (Party) getArguments().getSerializable(MainActivity.ARG_PARTY);
            this.isNew = this.party == null;
        }
        if(isNew){
            getActivity().setTitle(getString(R.string.party_create_fragment_title));
        } else {
            getActivity().setTitle(getString(R.string.party_edit_fragment_title));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupText = view.findViewById(R.id.party_create_group_text);
        if(!isNew){
            groupText.setText(party.getName());
        }

        playerLayouts = new LinkedHashMap<>();
        LinearLayout linearLayout = view.findViewById(R.id.party_create_player_layout);

        addPlayerAddButtons(linearLayout);
        int start = 4;
        if(!isNew){
            start = this.party.getPlayers().size();
        }
        for(int i = 0; i < start; i++) {
            addPlayerInput(linearLayout, i);
        }
        reloadPlayerHints();

        Button createButton = view.findViewById(R.id.party_create_create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onCreateParty(getParty(), isNew);
                } catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!isNew){
            createButton.setText(R.string.save);
        }

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
        try {
            imageButton.setImageBitmap(party.getImage());
        } catch(Exception ignore){}
    }

    private void addPlayerInput(final LinearLayout parentLayout, int index){
        final ConstraintLayout playerLayout =
                (ConstraintLayout) View.inflate(getContext(), R.layout.new_player, null);
        TextInputEditText editText = playerLayout.findViewById(R.id.new_player_text);
        long id = -1;
        if(party != null && index < party.getPlayers().size()) {
            id = party.getPlayers().get(index).getDataBaseId();
            editText.setText(party.getPlayers().get(index).getName());
        }
        playerLayouts.put(playerLayout, id);

        final long finalId = id;

        ImageView buttonRemove = playerLayout.findViewById(R.id.new_player_remove);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerLayouts.size() <= 4) {
                    return;
                }
                if(finalId != -1 && !isNew && party.hasPlayerUse(party.getPlayerByDBId(finalId))){
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_player_title)
                            .setMessage(R.string.message_cannot_delete_player)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.ok, null).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_player_title)
                            .setMessage(R.string.confirmation_delete_player)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    parentLayout.removeView(playerLayout);
                                    playerLayouts.remove(playerLayout);
                                    reloadPlayerHints();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }

            }
        });

        parentLayout.addView(playerLayout, playerLayouts.size()-1);
    }

    private void reloadPlayerHints(){
        int i = 0;
        for (ConstraintLayout playerLayout : playerLayouts.keySet()){
            TextInputLayout inputLayout = playerLayout.findViewById(R.id.new_player_text_input_layout);
            long id = playerLayouts.get(playerLayout);
            String hint;
            Log.e("id", "" + id);
            if(id == -1){
                int req = i < 4 ? R.string.player_required : R.string.player_optional;
                hint = getString(R.string.player) + " " + (i+1) + " " + getString(req);
            } else {
                hint = party.getPlayerByDBId(id).getName();
            }
            inputLayout.setHint(hint);
            i++;
        }
    }

    private void addPlayerAddButtons(final LinearLayout parentLayout){
        ConstraintLayout buttonLayout =
                (ConstraintLayout) View.inflate(getContext(), R.layout.new_player_button, null);
        TextView textAdd = buttonLayout.findViewById(R.id.new_player_add);
        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayerInput(parentLayout, playerLayouts.size());
                reloadPlayerHints();
                final ScrollView scrollView = getView().findViewById(R.id.party_create_scroll_view);
                scrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        parentLayout.addView(buttonLayout);
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
        if(isNew){
            Party party = new Party(getGroupName(), getPlayers(), MyUtils.getDate());
            if(bitmap != null) {
                party.setImage(bitmap);
            }
            return party;
        } else {
            Party party = this.party;
            party.setName(getGroupName());
            party.setPlayers(getPlayers());
            party.setLastDate(MyUtils.getDate());
            if(bitmap != null){
                party.setImage(bitmap);
            }
            return party;
        }
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
        for(ConstraintLayout layout : playerLayouts.keySet()) {
            TextInputEditText editText = layout.findViewById(R.id.new_player_text);
            String name = editText.getText().toString();
            if(!name.trim().isEmpty()) {
                Player p = new Player( editText.getText().toString().trim() );
                if(!isNew && playerLayouts.get(layout) != -1){
                    p.setDataBaseId(playerLayouts.get(layout));
                }
                players.add(p);
            } else if(!isNew && playerLayouts.get(layout) != -1){
                throw new RuntimeException(getString(R.string.error_player_field_empty));
            }
        }
        if(players.size() < 4) {
            throw new RuntimeException(getString(R.string.error_required_field_not_initialized));
        }
        return players;
    }


    public void onCreateParty(Party party, boolean isNew) {
        if (partyCreateListener != null) {
            partyCreateListener.onPartyCreated(party, isNew);
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
        void onPartyCreated(Party party, boolean isNew);
        void onPartyCreateCancelled();
    }
}
