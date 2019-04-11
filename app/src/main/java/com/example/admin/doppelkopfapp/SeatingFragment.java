package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SeatingFragment extends Fragment {

    private Party party;
    private OnSeatingChangedListener listener;

    private RecyclerView recyclerView;

    public SeatingFragment() {
        // Required empty public constructor
    }

    public static SeatingFragment newInstance(Party party) {
        SeatingFragment fragment = new SeatingFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY, party);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            party = (Party)getArguments().getSerializable(MainActivity.ARG_PARTY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.seating_recycler_view);

        Button save = view.findViewById(R.id.seating_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSeatingChanged(null); //todo
            }
        });

        Button cancel = view.findViewById(R.id.seating_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSeatingCanceled();
            }
        });

        ConstraintLayout headerLayout = view.findViewById(R.id.seating_group_header);

        TextView groupText = (TextView) headerLayout.getViewById(R.id.group_header_name);
        groupText.setText(party.getName());

        if(party.getImageBytes() != null) {
            ImageView groupImage = (ImageView) headerLayout.getViewById(R.id.group_header_image);
            groupImage.setImageBitmap(party.getImage());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSeatingChangedListener) {
            listener = (OnSeatingChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSeatingChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSeatingChangedListener {
        void onSeatingChanged(List<Long> ids);
        void onSeatingCanceled();
    }
}