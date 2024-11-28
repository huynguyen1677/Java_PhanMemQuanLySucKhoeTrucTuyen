package com.example.carevault.fragments;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;
import com.example.carevault.Adapters.Adapter;
import com.example.carevault.Adapters.Note1;
import com.example.carevault.Alarms.AppointmentReminders;
import com.example.carevault.R;
import com.example.carevault.Utility;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CalenderFragment extends Fragment {
    FloatingActionButton plus;
    RecyclerView recyclerView;
    ImageButton menu;
    Adapter noteAdapter;
    LottieAnimationView lottie;

    // Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        plus = view.findViewById(R.id.notebuton);
        lottie = view.findViewById(R.id.lottie);
        recyclerView = view.findViewById(R.id.recyclerview);

        plus.setOnClickListener(v -> startActivity(new Intent(getContext(), AppointmentReminders.class)));
        setupRecyclerView();

        return view;
    }

    void setupRecyclerView() {
        // Query to fetch data ordered by timestamp
        Query query = db.collection("Alarms") // Adjust collection name
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note1> options = new FirestoreRecyclerOptions.Builder<Note1>()
                .setQuery(query, Note1.class)
                .build();

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new Adapter(options, requireContext());
        recyclerView.setAdapter(noteAdapter);

        // Handling Lottie animation visibility
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                // No data, show animation
                lottie.setVisibility(View.VISIBLE);
                lottie.playAnimation();
            } else {
                // Data exists, hide animation
                lottie.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting documents.", e));
    }

    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();

        // FirestoreRecyclerAdapter automatically updates, no need to manually notify dataset changed
    }
}