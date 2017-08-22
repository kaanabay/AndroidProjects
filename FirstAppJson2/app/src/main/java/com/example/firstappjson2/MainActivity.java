package com.example.firstappjson2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private static ArrayList<String> myList = new ArrayList<String>();
    private static final String baseUrl = "http://api.fixer.io/latest?base=";
    private static final String convertTo = "&symbols=";
    private static int from = 30, to = 29;
    private static double coeff = 1.0;
    private static JSONObject js;
    private static final String[] currencyCodes = new String[] {"AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD",
                                                "HRK", "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK",
                                                "NZD", "PHP", "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"};
    private static final String[] currencyNames = new String[] {"Avustralya Doları", "Bulgar Levası", "Brezilya Reali", "Kanada Doları",
            "İsviçre Frangı", "Çin Yuan Renminbi", "Çek Cumhuriyeti Korunası", "Danimarka Kronu",
            "Euro", "İngiliz Sterlini", "Hong Kong Doları", "Hırvat Kunası", "Macar Forinti",
            "Endonezya Rupiahı", "Yeni İsrail Şekeli", "Hindistan Rupisi", "Japon Yeni",
            "Güney Kore Wonu", "Meksika Pezosu", "Malezya Ringiti", "Norveç Kronu", "Yeni Zelanda Doları",
            "Filipinler Pezosu", "Polonya Zlotisi", "Romen Leyi", "Rus Rublesi", "İsveç Kronu",
            "Singapur Doları", "Tayland Bahtı", "Türk Lirası", "ABD Doları", "Güney Afrika Randı"};

    private static void fillArray () {
        for(int i=0; i<currencyCodes.length; i++)
            myList.add(currencyNames[i] + " (" + currencyCodes[i] + ")");
    }


    private void fetchTheRate() throws JSONException {
        final TextView mTextView = (TextView) findViewById(R.id.textViewTesting);
        JSONObject rates = (JSONObject) js.get("rates");
        String dn = rates.get(currencyCodes[to]).toString();
        double rate = Double.parseDouble(dn);

        double res = BigDecimal.valueOf(rate*coeff)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        mTextView.setText(Double.toString(res));
    }

    private void updateJsonWithVolley(final String code1, final String code2) {
        final TextView mTextView = (TextView) findViewById(R.id.textViewTesting);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = baseUrl + code1 + convertTo + code2;

        if(code1 == code2) {
            mTextView.setText("Two fields cannot be the same");
            return;
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,100));
                        try {
                            js = new JSONObject(response);
                            fetchTheRate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fillArray();


        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, myList);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner mySpinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        Spinner mySpinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        mySpinnerFrom.setAdapter(adp);
        mySpinnerTo.setAdapter(adp);
        onChangeEventsForSpinners(mySpinnerFrom, true);
        onChangeEventsForSpinners(mySpinnerTo, false);

        final TextView amountView = (TextView) findViewById(R.id.amount);

        amountView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                String amountStr = amountView.getText().toString();
                coeff = ("".equals(amountStr)) ? 1 : Double.parseDouble(amountStr);
                try {
                    fetchTheRate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        updateJsonWithVolley("USD","TRY"); //DEFAULT AT THE BEGINNING

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void onChangeEventsForSpinners(Spinner mySpinnerFrom, final boolean isFrom) {
        mySpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFrom)
                    from = i;
                else
                    to = i;
                updateJsonWithVolley(currencyCodes[from], currencyCodes[to]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

