package com.softobt.poultrymanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Abdulgafar Obeitor on 6/5/2017.
 */

public class Start extends Fragment {
    private Button NEW_POULTRY;
    private Button EXISTING_POULTRY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.start_fragment,container,false);
        NEW_POULTRY = (Button)returnView.findViewById(R.id.new_poultry);
        EXISTING_POULTRY = (Button)returnView.findViewById(R.id.existing_poultry);
        NEW_POULTRY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                        .replace(R.id.container,new CreateFarm()).commit();
            }
        });
        EXISTING_POULTRY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                        .replace(R.id.container,new ExistingFarm()).commit();
            }
        });
        return returnView;
    }
}
