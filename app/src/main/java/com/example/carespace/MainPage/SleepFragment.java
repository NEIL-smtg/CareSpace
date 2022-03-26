package com.example.carespace.MainPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carespace.PlayAudio.AudioAdapter;
import com.example.carespace.PlayAudio.SleepRecyclerViewAdapter;
import com.example.carespace.PlayAudio.playAudio;
import com.example.carespace.R;

public class SleepFragment extends Fragment implements SleepRecyclerViewAdapter.onItemClickListener {

    private RecyclerView rv_soundscape,rv_asmr,rv_sleep_better,rv_anxious,rv_depressed,rv_feeling_down,rv_coaching;
    SleepRecyclerViewAdapter sleepAdapter;
    AudioAdapter audioAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        audioAdapter = new AudioAdapter(getActivity());

        rv_soundscape = (RecyclerView) view.findViewById(R.id.rv_soundscape);
        rv_asmr = (RecyclerView) view.findViewById(R.id.rv_asmr);
        rv_sleep_better = (RecyclerView) view.findViewById(R.id.rv_sleep_better);
        rv_anxious = (RecyclerView) view.findViewById(R.id.rv_anxious);
        rv_depressed = (RecyclerView) view.findViewById(R.id.rv_depressed);
        rv_feeling_down = (RecyclerView) view.findViewById(R.id.rv_feeling_down);
        rv_coaching = (RecyclerView) view.findViewById(R.id.rv_coaching);

        sleepAdapter = new SleepRecyclerViewAdapter(getActivity(), this);

        setupRV();

        return view;
    }

    private void setupRV()
    {
        rv_soundscape.setAdapter(sleepAdapter);
        rv_asmr.setAdapter(sleepAdapter);
        rv_sleep_better.setAdapter(sleepAdapter);
        rv_anxious.setAdapter(sleepAdapter);
        rv_depressed.setAdapter(sleepAdapter);
        rv_feeling_down.setAdapter(sleepAdapter);
        rv_coaching.setAdapter(sleepAdapter);

    }

    @Override
    public void onItemClicked(int position) {

        //send control to play audio
        Intent intent = new Intent(getActivity(), playAudio.class);
        intent.putExtra("audio_name",audioAdapter.audioNameList[position]);
        intent.putExtra("position",position);
        getActivity().startActivity(intent);
    }
}