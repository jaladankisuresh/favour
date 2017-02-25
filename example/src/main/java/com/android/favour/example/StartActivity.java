package com.android.favour.example;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.favour.AddParticipantsActivity;
import com.android.favour.Models.ContinentGroup;
import com.android.favour.Models.Country;
import com.android.favour.NetworkIO.WebServiceClient;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private TextView lbl_show_selected_profiles;
    private WebServiceClient proxyService;

    private final String  popularCountriesRequestUrl = "http://10.0.2.2:3000/api/popular-countries";
    private final String findCountriesRequestUrl = "http://10.0.2.2:3000/api/countries-search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Type continentGroupArrayType = new TypeToken<List<ContinentGroup>>(){}.getType();
        lbl_show_selected_profiles = (TextView) findViewById(R.id.lbl_show_selected_profiles);
        Button btn_select_profiles = (Button) findViewById(R.id.btn_select_profiles);
        btn_select_profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddPlayersIntent = new Intent(StartActivity.this, AddParticipantsActivity.class);
                AddPlayersIntent.putExtra("getPopularCountries", popularCountriesRequestUrl);
                AddPlayersIntent.putExtra("findCountries", findCountriesRequestUrl);
                AddPlayersIntent.putExtra("selectableGroupArrayType", (Serializable) continentGroupArrayType);
                startActivityForResult(AddPlayersIntent, convertTo16Bit(view.getId()));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (convertTo16Bit(R.id.btn_select_profiles) == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                List<Country> selectedCountries = (List<Country>) data.getSerializableExtra("result_invitees");
                lbl_show_selected_profiles.setText(TextUtils.join(",", selectedCountries));
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private static int convertTo16Bit(int viewId) {
        return (viewId % 10000);
    }

}
