package com.example.admin.doppelkopfapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeatingFragment extends Fragment implements OnStartDragListener {

    private Party party;
    private List<Long> databaseIds;
    private OnSeatingChangedListener listener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ItemTouchHelper ith;

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
            databaseIds = new ArrayList<>();
            for(long id : party.getCurrentGame().getPlayersDataBaseIds()) {
                databaseIds.add(id);
            }
        }
        getActivity().setTitle(R.string.seating);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SeatingAdapter(party, databaseIds, this);

        recyclerView = view.findViewById(R.id.seating_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        addItemTouchHelper(recyclerView);

        Button save = view.findViewById(R.id.seating_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSeatingChanged(databaseIds);
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

    private void addItemTouchHelper(RecyclerView view) {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(databaseIds, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                for(int i = 0; i < adapter.getItemCount(); i++){
                    adapter.notifyItemChanged(i);
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };

        ith = new ItemTouchHelper(callback);
        ith.attachToRecyclerView(view);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        ith.startDrag(viewHolder);
    }

    public interface OnSeatingChangedListener {
        void onSeatingChanged(List<Long> ids);
        void onSeatingCanceled();
    }
}
