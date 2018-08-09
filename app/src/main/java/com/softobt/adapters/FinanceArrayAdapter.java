package com.softobt.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softobt.mainapplication.PoultryApplication;
import com.softobt.models.FinanceSummary;
import com.softobt.models.PoultryException;
import com.softobt.poultrymanager.R;

import java.util.ArrayList;

/**
 * Created by Abdulgafar Obeitor on 8/1/2017.
 */
public class FinanceArrayAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FinanceSummary> financeSummaries;
    public FinanceArrayAdapter(Context context, ArrayList<FinanceSummary> financeSummaries){
        this.context = context;
        this.financeSummaries = financeSummaries;
    }

    @Override
    public int getCount() {
        return financeSummaries.size();
    }

    @Override
    public Object getItem(int position) {
        return financeSummaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_finance_summaries,null);
        }
        TextView name =(TextView) convertView.findViewById(R.id.finance_list_name);
        TextView amount =(TextView) convertView.findViewById(R.id.finance_list_amount);
        TextView date = (TextView)convertView.findViewById(R.id.finance_list_time);
        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.finance_list_summary);
        if(financeSummaries.get(position).isCredit()){
            layout.setBackgroundResource(R.drawable.green_btn);
        }
        else{
            layout.setBackgroundResource(R.drawable.red_btn);
        }
        name.setText(financeSummaries.get(position).getTitle());
        amount.setText(PoultryApplication.formatCash(financeSummaries.get(position).getAmount()));
        date.setText(financeSummaries.get(position).getDate()+" "+financeSummaries.get(position).getTime());
        return convertView;
    }
}
