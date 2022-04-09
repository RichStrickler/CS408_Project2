package edu.jsu.mcis.cs408.project2;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.jsu.mcis.cs408.project2.databinding.FragmentClueBinding;

public class ClueFragment extends Fragment implements TabFragment {

    private FragmentClueBinding binding;

    private CrosswordViewModel model;

    public ClueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get Shared Model Reference

        model = new ViewModelProvider(requireActivity()).get(CrosswordViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentClueBinding.inflate(getLayoutInflater(), container, false);

        // Update the view
        updateClues();

        return binding.getRoot();

    }

    private void updateClues() {

        String cluesAcross = model.getCluesAcross().getValue();
        String cluesDown = model.getCluesDown().getValue();

        binding.aContainer.setText(cluesAcross);
        binding.aContainer.setMovementMethod(new ScrollingMovementMethod());

        binding.dContainer.setText(cluesDown);
        binding.dContainer.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public String getTabTitle() {
        return "Clues";
    }

}