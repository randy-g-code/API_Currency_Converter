package com.example.randy.superconverter;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import static com.example.randy.superconverter.iconChanger.Flagupdate;

public class MainActivity extends AppCompatActivity
{

    TextView Rate, Total;
    EditText Amount;
    Spinner ConvertFrom, ConvertTo;
    Button b;
    ImageView flag1, flag2;

    String FromCountry, ToCountry;
    Double Amt;
    DecimalFormat Money = new DecimalFormat( "#,###.##" );

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);

        b = (Button) findViewById(R.id.button);
        Amount = (EditText)findViewById(R.id.edittext);

        flag1 = (ImageView)findViewById(R.id.imageView);
        flag2 = (ImageView)findViewById(R.id.imageView2);

        Total = (TextView)findViewById(R.id.textView9);
        Rate = (TextView)findViewById(R.id.textView8);

        ConvertFrom = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.country));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ConvertFrom.setAdapter(myAdapter);

        ConvertTo = (Spinner) findViewById(R.id.spinner);
        ConvertTo.setAdapter(myAdapter);

        //Changes Flag Icon Based on What is Selected
        ConvertFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int Flag = Flagupdate(ConvertFrom.getSelectedItem().toString());
                flag1.setImageResource(Flag);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ConvertTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int Flag = Flagupdate(ConvertTo.getSelectedItem().toString());
                flag2.setImageResource(Flag);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    public void Convert(View v) throws IOException, JSONException
    {
        //Grab Info From User's Selection
        FromCountry = ConvertFrom.getSelectedItem().toString();
        ToCountry = ConvertTo.getSelectedItem().toString();
        String EnterTest = Amount.getText().toString();

        //Test if User Entered in An Amount
        if(EnterTest == null || EnterTest.isEmpty()) {Amt = 0.0;}
        else {Amt = Double.parseDouble(Amount.getText().toString());}

        //Fetch Exchange Rates
        String Test = fetchRate();
        Rate.setText(Test);
        Total.setText(Test);
    }

    //Method to Start Async Task and Run Calculations
    public String fetchRate() throws IOException, JSONException
    {
        //Start AsyncTask
        new CCTask().execute();
        return "Loading";
    }

    //Async Task that Gathers Info From fixer.io
    //Exchange rates API is the JSON API that I Went With to Gather Current Exchange Rates
    public class CCTask extends AsyncTask<URL, Void, String>
    {
        @Override
        protected String doInBackground(URL... urls)
        {
            String request = null;
            Double Rate = 0.0;
            try
            {
                URL url = new URL("https://api.exchangeratesapi.io/latest?base="+FromCountry);
                request = Network.request(url);
                // Parse the entire JSON String
                JSONObject root = new JSONObject(request);
                // Get Exchange Rate For Country Entered
                JSONObject rates = root.getJSONObject("rates");

                Rate = rates.getDouble(ToCountry);
                Amt = Rate * Amt;
            }
            catch (JSONException JS) {JS.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            return Rate.toString();
        }

        @Override
        protected void onPostExecute(String s)
        {
            if(s != null && !s.equals(""))
            {
                //Set Results if Found
                Rate.setText(s);
                Total.setText(Money.format(Amt));
            }
        }
    }

}
