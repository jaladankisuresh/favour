package com.android.favour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.android.favour.Adapters.EmptyViewRecyclerAdapter;
import com.android.favour.Adapters.MultiSelectOptionsArrayAdapter;
import com.android.favour.AndroidPlus.ProfileAutoCompleteView;
import com.android.favour.Models.ContinentGroup;
import com.android.favour.Models.Selection;
import com.android.favour.NetworkIO.GsonRequest;
import com.android.favour.NetworkIO.WebServiceClient;
import com.android.favour.R;
import com.google.gson.reflect.TypeToken;
import com.tokenautocomplete.TokenCompleteTextView;

public class AddParticipantsActivity extends AppCompatActivity
        implements WebServiceClient.Listener<List<Selection.SelectableGroup>>,
        MultiSelectOptionsArrayAdapter.SelectOptionsFilterListener,
        Selection.SelectableListener {

    private RecyclerView list_app_profiles;
    //private List<ContinentGroup> popularCountriesGroups;
    private ProfileAutoCompleteView auto_select_profiles;
    private MultiSelectOptionsArrayAdapter selectableFilterAdapter;
    private AutocompleteTextWatcher textWatcher;
    private String findCountriesRequestUrl;
    private WebServiceClient proxyService;
    private final String LOG_APP_TAG = "FAVOUR";
    private final Type continentGroupArrayType = new TypeToken<List<ContinentGroup>>() {}.getType();

    public AddParticipantsActivity() {
        textWatcher = new AutocompleteTextWatcher();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_participants);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final String  popularCountriesRequestUrl = getIntent().getStringExtra("getPopularCountries");
        //findCountriesRequestUrl = getIntent().getStringExtra("findCountries");
        final String  popularCountriesRequestUrl = "http://10.0.2.2:3000/api/popular-countries";
        findCountriesRequestUrl = "http://10.0.2.2:3000/api/countries-search";

        auto_select_profiles = (ProfileAutoCompleteView) findViewById(R.id.auto_select_profiles);
        auto_select_profiles.addTextChangedListener(textWatcher);
        auto_select_profiles.setTokenListener(textWatcher);

        list_app_profiles = (RecyclerView) findViewById(R.id.list_app_profiles);
        list_app_profiles.setLayoutManager(new GridLayoutManager(list_app_profiles.getContext(), 1));
        list_app_profiles.setAdapter(EmptyViewRecyclerAdapter.getAdapter());

        proxyService = new WebServiceClient(this, continentGroupArrayType);
        proxyService.makeGet(popularCountriesRequestUrl);
    }

    private void updateItemToSelectedTokens(Selection.Selectable token){
        auto_select_profiles.clearCompletionText();

        if(token.isSelected()) {
            auto_select_profiles.addObject(token);
        }
        else {
            auto_select_profiles.removeObject(token);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_go, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_submit:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result_invitees", (Serializable) auto_select_profiles.getObjects());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectableClicked(Selection.Selectable selectItem) {
        updateItemToSelectedTokens(selectItem);
    }

    @Override
    public List autoComplete(CharSequence charSequence) {

        String curText = charSequence.toString();
        String getUrlParamsString = String.format("%s/%s", findCountriesRequestUrl, curText);

        List<Selection.SelectableGroup> searchResponse = (List<Selection.SelectableGroup>) proxyService.makeBlockingRequest(
                this, GsonRequest.RequestType.GET, getUrlParamsString, continentGroupArrayType, null);
        return auto_select_profiles.enoughToFilter() ? searchResponse : null;
    }

    @Override
    public void onSuccessResponse(List<Selection.SelectableGroup> dataObj) {
        //this.popularCountriesGroups = dataObj;
        selectableFilterAdapter = new MultiSelectOptionsArrayAdapter(this, dataObj);
        list_app_profiles.setAdapter(selectableFilterAdapter);
    }

    @Override
    public void onErrorResponse(String errMessage) {
        Log.e(LOG_APP_TAG, errMessage);
    }

    @Override
    public Context getListenerContext() {
        return this;
    }


    private class AutocompleteTextWatcher
            implements TextWatcher, TokenCompleteTextView.TokenListener {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Filter f = selectableFilterAdapter.getFilter();
            if(auto_select_profiles.enoughToFilter()){
                f.filter(auto_select_profiles.currentCompletionText());
            }
            else {
                f.filter(null);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void onTokenAdded(Object token) {

        }

        @Override
        public void onTokenRemoved(Object token) {
            Selection.Selectable selectItem = (Selection.Selectable) token;
            if(selectItem.isSelected()) {
                selectItem.setSelected(false);
                selectableFilterAdapter.updateItemToSelectedTokens(selectItem);
            }
        }
    }

}


