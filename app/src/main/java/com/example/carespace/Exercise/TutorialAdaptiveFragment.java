package com.example.carespace.Exercise;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carespace.R;


public class TutorialAdaptiveFragment extends Fragment implements TutorialCardAdapter.onItemClickedListener{

    //vars
    private int tabNum;

    //widgets
    private RecyclerView rv;

    //adapter
    TutorialCardAdapter adapter;

    //model
    TutorialModel tutorialModel;


    public TutorialAdaptiveFragment(int tabNum) { this.tabNum = tabNum; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adaptive, container, false);

        tutorialModel = new TutorialModel();

        rv = view.findViewById(R.id.rv_exercise);
        setupTutorial();

        return view;
    }

    private void setupTutorial()
    {
        //setup tutorial card view
        adapter = new TutorialCardAdapter(getActivity(),this ,tabNum);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int position)
    {
        Intent intent = new Intent(getActivity(),TutorialIntroduction.class);
        //pass file
        intent.putExtra("tabNum",tabNum);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}