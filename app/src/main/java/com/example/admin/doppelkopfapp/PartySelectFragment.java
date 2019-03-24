package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PartySelectFragment.OnPartySelectListener} interface
 * to handle interaction events.
 * Use the {@link PartySelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartySelectFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private FloatingActionButton addButton;

    private PartyManager partyManager;

    private OnPartySelectListener selectListener;

    public PartySelectFragment() {
        // Required empty public constructor
    }


    public static PartySelectFragment newInstance(PartyManager partyManager) {
        PartySelectFragment fragment = new PartySelectFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.ARG_PARTY_MANAGER, partyManager);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.partyManager = (PartyManager) getArguments().getSerializable(MainActivity.ARG_PARTY_MANAGER);
        }

        getActivity().setTitle(getString(R.string.party_select_fragment_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PartySelectAdapter(selectListener, partyManager);
        initRecyclerView();
        adapter.notifyDataSetChanged();

        addButton = view.findViewById(R.id.party_select_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPartyAddClick();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.party_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void onPartySelect(int pos) {
        if (selectListener != null) {
            selectListener.onPartySelected(pos);
        }
    }

    public void onPartyAddClick() {
        if(selectListener != null) {
            selectListener.onPartyAddClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPartySelectListener) {
            selectListener = (OnPartySelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPartySelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectListener = null;
    }


    public interface OnPartySelectListener {
        void onPartySelected(int pos);
        void onPartyAddClicked();
        void onPartyDeleted(int pos, RecyclerView.Adapter adapter);
        void onPartyEdited(int pos);
    }

}
