package com.softobt.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.softobt.mainapplication.PoultryApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abdulgafar Obeitor on 7/31/2017.
 */
public class clsFinance {
    private String id;
    private String direction;
    private String desc;
    private int status;
    private double amount;
    private String name;
    private double amountPaid;
    private int itemCount;
    private float unitPrice;
    private String type;
    private ArrayList<String> summaries;
    @Exclude
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getDirection(){
        return direction;
    }
    public void setDirection(String direction){
        this.direction = direction;
    }
    public String getDesc(){
        return desc;
    }
    public void setDesc(String desc){
        this.desc = desc;
    }
    public int getStatus(){
        return status;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public double getAmount(){
        return amount;
    }
    public void setAmount(double amount){
        this.amount = amount;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public double getAmountPaid(){
        return amountPaid;
    }
    public void setAmountPaid(double amountPaid){
        this.amountPaid = amountPaid;
    }
    public int getItemCount(){
        return itemCount;
    }
    public void setItemCount(int itemCount){
        this.itemCount = itemCount;
    }
    public float getUnitPrice(){
        return unitPrice;
    }
    public void setUnitPrice(float unitPrice){
        this.unitPrice = unitPrice;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public ArrayList<String> getSummaries() {
        return summaries;
    }

    public void setSummaries(ArrayList<String> summaries) {
        this.summaries = summaries;
    }

    public static final String CREDIT  = "CREDIT", DEBIT = "DEBIT";
    public static final int INCOMPLETE = 0, COMPLETE = 1, OVERPAY = 2;
    public static final String INVEST_TYPE = "invest", BIRD_SALE_TYPE = "bird", EGG_SALE_TYPE  = "egg",
            FIXED_ITEM_PURCHASE = "fixed_purchase", MEDIC_PURCHASE = "medicine",BIRD_PURCHASE = "bird_purchase",
            FEED_PURCHASE = "feed",SERVICE_TYPE = "service",LOAN_PAYMENT = "loan_pay";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
    public clsFinance(){

    }

    /**
     * Create a payment for service rendered on farm
     * @param amount
     * @param amountPaid
     * @param desc
     * @param name
     * @param transDate
     * @return
     */
    public static FinanceSummary createServicePayment(double amount, double amountPaid, String desc, String name, Calendar transDate){
        clsFinance finance = new clsFinance();
        finance.setDirection(DEBIT);
        finance.setItemCount(1);
        finance.setType(SERVICE_TYPE);
        finance.setAmount(amount);
        finance.setAmountPaid(amountPaid);
        if(amount>amountPaid){
            finance.setStatus(INCOMPLETE);
        }
        else if(amount==amountPaid){
            finance.setStatus(COMPLETE);
        }
        else{
            finance.setStatus(OVERPAY);
        }
        finance.setName(name);
        finance.setDesc(desc);
        finance.setStatus(COMPLETE);
        finance.setUnitPrice(0);
        finance.setId(sdf.format(transDate.getTime()));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance").push();
        financeNode.setValue(finance);
        String mainId = financeNode.getKey();

        String date = new SimpleDateFormat("yyyyMMdd").format(transDate.getTime());
        DatabaseReference summaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary/"+date).push();
        FinanceSummary financeSummary = new FinanceSummary(transDate,finance.getName(),finance.getAmountPaid(),finance.getDirection(),mainId);
        summaryNode.setValue(financeSummary);

        finance.summaries = new ArrayList<>();
        finance.summaries.add(summaryNode.getKey());
        financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance/"+mainId+"/summaries");
        financeNode.setValue(finance.summaries);
        return financeSummary;
    }

    /**
     * Create a loan repayment
     * @param amount
     * @param desc
     * @param transDate
     * @return
     */
    public static FinanceSummary createLoanRepay(double amount, String desc, Calendar transDate){
        clsFinance finance = new clsFinance();
        finance.setDirection(DEBIT);
        finance.setItemCount(1);
        finance.setType(LOAN_PAYMENT);
        finance.setAmount(amount);
        finance.setAmountPaid(amount);
        finance.setName("Loan Repay");
        finance.setDesc(desc);
        finance.setStatus(COMPLETE);
        finance.setUnitPrice(0);
        finance.setId(sdf.format(transDate.getTime()));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance").push();
        financeNode.setValue(finance);
        String mainId = financeNode.getKey();

        String date = new SimpleDateFormat("yyyyMMdd").format(transDate.getTime());
        DatabaseReference summaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary/"+date).push();
        FinanceSummary financeSummary = new FinanceSummary(transDate,finance.getName(),finance.getAmountPaid(),finance.getDirection(),mainId);
        summaryNode.setValue(financeSummary);

        finance.summaries = new ArrayList<>();
        finance.summaries.add(summaryNode.getKey());
        financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance/"+mainId+"/summaries");
        financeNode.setValue(finance.summaries);
        return financeSummary;
    }

    /**
     * Create incoming money from investment
     * @param amount
     * @param desc
     * @param transDate
     * @return
     */
    public static FinanceSummary createInvestment(double amount,String desc, Calendar transDate){
        clsFinance finance = new clsFinance();
        finance.setDirection(CREDIT);
        finance.setItemCount(1);
        finance.setType(INVEST_TYPE);
        finance.setAmount(amount);
        finance.setAmountPaid(amount);
        finance.setName("Investment");
        finance.setDesc(desc);
        finance.setStatus(COMPLETE);
        finance.setUnitPrice(0);
        finance.setId(sdf.format(transDate.getTime()));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance").push();
        financeNode.setValue(finance);
        String mainId = financeNode.getKey();

        String date = new SimpleDateFormat("yyyyMMdd").format(transDate.getTime());
        DatabaseReference summaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary/"+date).push();
        FinanceSummary financeSummary = new FinanceSummary(transDate,finance.getName(),finance.getAmountPaid(),finance.getDirection(),mainId);
        summaryNode.setValue(financeSummary);

        finance.summaries = new ArrayList<>();
        finance.summaries.add(summaryNode.getKey());
        financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance/"+mainId+"/summaries");
        financeNode.setValue(finance.summaries);
        return financeSummary;
    }

    /**
     * Create incoming money from sale
     * @param name
     * @param desc
     * @param itemCount
     * @param amount
     * @param amountPaid
     * @param type BIRD_SALE_TYPE, EGG_SALE_TYPE
     * @param transDate
     * @return
     */
    public static FinanceSummary createSale(String name,String desc,int itemCount,double amount,double amountPaid, String type, Calendar transDate){
        clsFinance finance = new clsFinance();
        finance.setDirection(CREDIT);
        finance.setItemCount(itemCount);
        finance.setType(type);
        if(amount>amountPaid){
            finance.setStatus(INCOMPLETE);
        }
        else if(amount==amountPaid){
            finance.setStatus(COMPLETE);
        }
        else{
            finance.setStatus(OVERPAY);
        }
        finance.setAmountPaid(amountPaid);
        finance.setAmount(amount);
        finance.setName(name);
        finance.setDesc(desc);
        finance.setUnitPrice((float)amount/itemCount);
        finance.setId(sdf.format(transDate.getTime()));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance").push();
        financeNode.setValue(finance);
        String mainId = financeNode.getKey();

        String date = new SimpleDateFormat("yyyyMMdd").format(transDate.getTime());
        DatabaseReference summaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary/"+date).push();
        FinanceSummary financeSummary = new FinanceSummary(transDate,finance.getName(),finance.getAmountPaid(),finance.getDirection(),mainId);
        summaryNode.setValue(financeSummary);

        finance.summaries = new ArrayList<>();
        finance.summaries.add(summaryNode.getKey());
        financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance/"+mainId+"/summaries");
        financeNode.setValue(finance.summaries);
        return financeSummary;
    }

    /**
     * Create outgoing money, expenditure
     * @param name
     * @param desc
     * @param itemCount
     * @param amountPaid
     * @param amount
     * @param type FIXED_ITEM_PURCHASE, BIRD_PURCHASE, FEED_PURCHASE, MEDIC_PURCHASE
     * @param transDate
     * @return
     */
    public static FinanceSummary createExpenditure(String name, String desc,int itemCount,double amountPaid, double amount,String type, Calendar transDate){
        clsFinance finance  = new clsFinance();
        finance.setDirection(DEBIT);
        finance.setItemCount(itemCount);
        finance.setType(type);
        if(amount>amountPaid){
            finance.setStatus(INCOMPLETE);
        }
        else if(amount==amountPaid){
            finance.setStatus(COMPLETE);
        }
        else{
            finance.setStatus(OVERPAY);
        }
        finance.setAmountPaid(amountPaid);
        finance.setAmount(amount);
        finance.setName(name);
        finance.setDesc(desc);
        finance.setUnitPrice((float)(amount/itemCount));
        finance.setId(sdf.format(transDate.getTime()));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance").push();
        financeNode.setValue(finance);
        String mainId = financeNode.getKey();

        String date = new SimpleDateFormat("yyyyMMdd").format(transDate.getTime());
        DatabaseReference summaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary/"+date).push();
        FinanceSummary financeSummary = new FinanceSummary(transDate,finance.getName(),finance.getAmountPaid(),finance.getDirection(),mainId);
        summaryNode.setValue(financeSummary);

        finance.summaries = new ArrayList<>();
        finance.summaries.add(summaryNode.getKey());
        financeNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Finance/"+mainId+"/summaries");
        financeNode.setValue(finance.summaries);
        return financeSummary;
    }
}
