package edu.scu.mortgagecalculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MyActivity extends Activity {

    EditText principal_amount;
    SeekBar interest_rate;
    TextView seek_value;
    RadioGroup loan_term;
    RadioButton loan_term_7yr;
    RadioButton loan_term_15yr;
    RadioButton loan_term_30yr;
    CheckBox taxes_insurance;
    Button calculate;
    TextView payment;

    float monthly_payment;
    float progress_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        principal_amount = (EditText) findViewById(R.id.text_principal);
        interest_rate = (SeekBar) findViewById(R.id.seekBar_interest);
        seek_value = (TextView) findViewById(R.id.text_seek_value);
        loan_term = (RadioGroup) findViewById(R.id.radioGroup_loanTerm);
        loan_term_7yr = (RadioButton) findViewById(R.id.radioButton_7yr);
        loan_term_15yr = (RadioButton) findViewById(R.id.radioButton_15yr);
        loan_term_30yr = (RadioButton) findViewById(R.id.radioButton_30yr);
        taxes_insurance = (CheckBox) findViewById(R.id.checkBox_taxes);
        calculate = (Button) findViewById(R.id.button_calculate);
        payment = (TextView) findViewById(R.id.text_payment);


        //Shared preferences restores value even if user kills the application
        // whereas SavedInstance Bundle restores values only on orientation change and if system killed the application

        if (savedInstanceState != null) {
            String payment_display = savedInstanceState.getString("payment");
            String seek_value_display = savedInstanceState.getString("seek_value");
            payment.setText(payment_display);
            seek_value.setText(seek_value_display);
        }


//        final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        if (sharedPref.contains("payment")) {
//            payment.setText(String.valueOf(sharedPref.getString("payment", null)));
//        }


        interest_rate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //By default seekbar values varies from 0 to 100.
                progress_value = ((float) progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seek_value.setText(progress_value + "/10.0");
            }
        });


        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float amount_borrowed = 0;
                float interest_value = 0;
                int loan_term_months = 0;
                float tax_insurance_value = 0;


                //Convert the string value from EditText input to double
                if (principal_amount.getText().toString().matches("")) {

                    Toast.makeText(getApplicationContext(), "Please enter amount borrowed in Principal", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    amount_borrowed = Float.parseFloat(principal_amount.getText().toString());
                }

                //Monthly interest in decimal form (annual interest rate / 1200)
                interest_value = progress_value / 1200;


                //Number of months in loan term
                if (loan_term.getCheckedRadioButtonId() == R.id.radioButton_7yr) {
                    loan_term_months = 84;
                } else if (loan_term.getCheckedRadioButtonId() == R.id.radioButton_15yr) {
                    loan_term_months = 180;
                } else if (loan_term.getCheckedRadioButtonId() == R.id.radioButton_30yr) {
                    loan_term_months = 360;
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a loan term", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Tax and Insurance
                if (taxes_insurance.isChecked()) {
                    // tax and insurance value is 0.1% of amount borrowed, if checkbox is checked
                    tax_insurance_value = amount_borrowed * 0.001f;
                } else {
                    tax_insurance_value = 0;
                }

                //Monthly payment calculation
                if (interest_value == 0) {
                    monthly_payment = (amount_borrowed / loan_term_months) + tax_insurance_value;
                } else {
                    monthly_payment = (float) ((amount_borrowed * interest_value)
                            / (1 - Math.pow(1 + interest_value, -loan_term_months)) + tax_insurance_value);
                }

                //Display monthly payment
                payment.setText("Your monthly payment is " + monthly_payment);

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("payment", payment.getText().toString());
        outState.putString("seek_value", seek_value.getText().toString());
        super.onSaveInstanceState(outState);

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        //saving data on OnPause() method to avoid loosing it at orientation change.
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.clear();
//        editor.putString("payment", payment.getText().toString());
//        editor.commit();
//    }

}
