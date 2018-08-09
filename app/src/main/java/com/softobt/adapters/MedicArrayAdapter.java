package com.softobt.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.mainapplication.RealmController;
import com.softobt.models.clsMedic;
import com.softobt.poultrymanager.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Abdulgafar Obeitor on 2/27/2018.
 */
public class MedicArrayAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<clsMedic> medications;
    public MedicArrayAdapter(Context context, ArrayList<clsMedic> medications){
        this.context = context;
        this.medications = medications;
    }

    @Override
    public int getCount() {
        return medications.size();
    }

    @Override
    public Object getItem(int position) {
        return medications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView = inflater.inflate(R.layout.list_medics_summaries, null);
        TextView medicType = (TextView) convertView.findViewById(R.id.medic_type_inList);
        TextView dueDate = (TextView)convertView.findViewById(R.id.medic_due_date_inList);
        TextView name = (TextView)convertView.findViewById(R.id.medic_drug_inList);
        TextView disease = (TextView)convertView.findViewById(R.id.medic_disease_inList);
        TextView status = (TextView)convertView.findViewById(R.id.medic_status_inList);
        Button startBtn = (Button)convertView.findViewById(R.id.medic_start_inList);
        Button doneBtn = (Button)convertView.findViewById(R.id.medic_done_inList);
        Button purchaseBtn = (Button)convertView.findViewById(R.id.medic_purchase_detail);
        final clsMedic medic = medications.get(position);

        if(medic.isVaccine())medicType.setText("VACCINE");
        else medicType.setText("TREATMENT");
        dueDate.setText(medic.showDate());
        name.setText(medic.getName());
        disease.setText(medic.getDisease());
        status.setText(medic.showStatus());
        int color = 0;
        switch(medic.getStatus()){
            case clsMedic.NOT_DUE:
                startBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.GONE);
                color = R.color.notDue;
                break;
            case clsMedic.IS_TOMORROW:
                startBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.GONE);
                color = R.color.tomorrow;
                break;
            case clsMedic.IS_DUE:
                startBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.GONE);
                color = R.color.isDue;
                break;
            case clsMedic.IN_PROGRESS:
                startBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.VISIBLE);
                color = R.color.inProgress;
                break;
            case clsMedic.COMPLETED:
                startBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.GONE);
                color = R.color.complete;
                break;
            case clsMedic.PAST_DUE:
                startBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.VISIBLE);
                color = R.color.pastDue;
                break;
            default:
                startBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.GONE);
                break;
        }
        status.setTextColor(ContextCompat.getColor(this.context,color));
        if(medic.isBought())purchaseBtn.setVisibility(View.GONE);
        else purchaseBtn.setVisibility(View.VISIBLE);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medic.setStatus(clsMedic.IN_PROGRESS);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference medicRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Medic/"+medic.getDueDate()+"/"+medic.getId());
                medicRecordNode.setValue(medic);
                RealmController.getInstance().updateMedic(medic);
                notifyDataSetChanged();
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medic.setStatus(clsMedic.COMPLETED);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference medicRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Medic/"+medic.getDueDate()+"/"+medic.getId());
                medicRecordNode.setValue(medic);
                RealmController.getInstance().updateMedic(medic);
                notifyDataSetChanged();
            }
        });
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }
}
