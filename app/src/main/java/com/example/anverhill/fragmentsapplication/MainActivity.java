package com.example.anverhill.fragmentsapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.anverhill.fragmentsapplication.Database.DatabaseRepository;
import com.example.anverhill.fragmentsapplication.Fragments.PersonDetailFragment;
import com.example.anverhill.fragmentsapplication.Fragments.PersonListFragment;
import com.example.anverhill.fragmentsapplication.Models.Person;
import com.example.anverhill.fragmentsapplication.Models.PersonDetail;
import com.example.anverhill.fragmentsapplication.Services.AppConfiguration;
import com.example.anverhill.fragmentsapplication.Services.AppSession;
import com.example.anverhill.fragmentsapplication.Services.DeviceConnectivityHelper;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ProgressDialog progressDialog;
    private Context context;
    private boolean updateInProgress = false;

    public enum UpdateResult {
        error,newDataCollected, noNewData, noInternetConnection
    }
    private UpdateResult updateResult;
    private List<Person> personList;
    private int listCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        if (getResources().getBoolean(R.bool.is_landscape)) {
            return;
        }
        if (savedInstanceState != null) {
            getFragmentManager().executePendingTransactions();
            Fragment fragmentById = getFragmentManager().
                    findFragmentById(R.id.fragment_container);
            if (fragmentById!=null) {
                getFragmentManager().beginTransaction()
                        .remove(fragmentById).commit();
            }
        }

        PersonListFragment listFragment = new PersonListFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, listFragment, getResources().getString(R.string.listFragment)).commit();
        personList = DatabaseRepository.getInstance(context).getPersonList();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void updatePersonListRequest(boolean checkVisibility) {
        boolean listVisible = false;
        PersonListFragment personListFragment;
        if(checkVisibility)
        {
            if (getResources().getBoolean(R.bool.is_landscape)) {
                personListFragment = (PersonListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.list_fragment);

                if(personListFragment != null && personListFragment.isVisible())
                {
                    listVisible = true;
                }
            }
            else
            {
                FragmentManager fm = getSupportFragmentManager();
                personListFragment = (PersonListFragment)fm.findFragmentByTag(getResources().getString(R.string.listFragment));
                if (personListFragment != null && personListFragment.isVisible()) {
                    listVisible = true;
                }
            }
        }
        else
        {
            listVisible = true;
        }
        if(!updateInProgress && listVisible)
        {
            updateInProgress = true;
            updateResult = UpdateResult.error;
            final Thread loginThread = new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage(getString(R.string.loading));
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                        }
                    });

                    try {
                        String urlPath = AppConfiguration.ApiEndpointUrl;
                        DeviceConnectivityHelper.getInstance().setContext(context);
                        if (DeviceConnectivityHelper.getInstance().isInternetOn()) {
                            URL url = new URL(urlPath);
                            HttpURLConnection request = (HttpURLConnection) url.openConnection();
                            request.setDefaultUseCaches(false);
                            request.setDoInput(true);
                            request.setDoOutput(false);
                            request.setRequestMethod("GET");
                            request.setRequestProperty("Accept-Encoding", "gzip");
                            request.setRequestProperty(HTTP.CONTENT_TYPE, "text/plain; charset=utf-8");
                            request.setConnectTimeout(AppConfiguration.DefaultConnectionTimeout);
                            request.setReadTimeout(AppConfiguration.DefaultSocketTimeout);
                            try {
                                Object entity = request.getContent();
                                if (entity != null&& request.getResponseCode() == 200) {
                                    InputStream inStream = request.getInputStream();
                                    BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
                                    StringBuilder sb = new StringBuilder();
                                    String result;
                                    while ((result = br.readLine()) != null) {
                                        sb.append(result);
                                    }
                                    result = sb.toString();
                                    inStream.close();
                                    request.disconnect();
                                    JSONArray jsonArray = new JSONArray(result);

                                    if (jsonArray.length() > 0) {
                                        updateResult = UpdateResult.newDataCollected;
                                        for (int i = 0; i < jsonArray.length(); i++)
                                        {
                                            JSONObject json = jsonArray.getJSONObject(i);
                                            Person person = new Person();
                                            person.objectId = json.getLong("id");
                                            if(personList.size() == 0 || (personList.size() > 0 && !personList.contains(person)))
                                            {
                                                Person.syncFromJson(context, json);
                                            }
                                        }
                                        personList = DatabaseRepository.getInstance(context).getPersonList();
                                        if(listCount == personList.size())
                                        {
                                            updateResult = UpdateResult.noNewData;
                                        }
                                        else
                                        {
                                            listCount = personList.size();
                                        }
                                    } else {
                                        updateResult = UpdateResult.noNewData;
                                    }
                                }
                            } catch (Exception e) {
                                updateResult = UpdateResult.error;
                            }
                        } else {
                            updateResult = UpdateResult.noInternetConnection;
                        }
                    } catch (Exception e) {
                        Log.d("Authentication", "Exception occurred " + e.getMessage());
                        updateResult = UpdateResult.error;
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            updateInProgress = false;
                            ProcessPersonListUpdateResult(updateResult);
                        }
                    });
                }
            });
            loginThread.start();
        }
    }

    private void ProcessPersonListUpdateResult(UpdateResult updateRequestResult)
    {
        switch (updateRequestResult) {
            case error:
                //show error occured
                updateInfo(getResources().getString(R.string.error_person_list), true);
                break;

            case newDataCollected:
                updateInfo("", false);
                break;

            case noNewData:
                //display no new data info
                updateInfo("", false);
                Toast.makeText(context, getResources().getString(R.string.no_new_data_person_list),
                        Toast.LENGTH_LONG).show();
                break;
            case noInternetConnection:
                //display internet required info
                updateInfo(getResources().getString(R.string.no_internet_connection), true);
                break;

            default:
                break;
        }
    }

    private void ProcessPersonDetailUpdateResult(UpdateResult updateRequestResult)
    {
        switch (updateRequestResult) {
            case error:
                //show error occured
                updateInfo(getResources().getString(R.string.error_person_detail), true);
                break;

            case newDataCollected:
                updateInfo("", false);
                updatePersonDetail();
                break;

            case noNewData:
                //display no new data info
                updateInfo(getResources().getString(R.string.no_new_data_person_list), false);
                break;
            case noInternetConnection:
                //display internet required info
                updateInfo(getResources().getString(R.string.no_internet_connection), true);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(personList == null)
        {
            if(context == null)
            {
                context = this;
            }
            personList = DatabaseRepository.getInstance(context).getPersonList();
        }
        if (getResources().getBoolean(R.bool.is_landscape)) {
            PersonListFragment personListFragment = (PersonListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.list_fragment);

            if(personListFragment != null)
            {
                personListFragment.setPersonData(personList);
                personListFragment.updateUI(false);
                personListFragment.updateInfo("", false);
            }
            if(AppSession.getInstance().GetPersonDetailId() > 0L)
            {
                PersonDetailFragment personDetailFragment = (PersonDetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.detail_fragment);
                if(personDetailFragment != null)
                {
                    PersonDetail personDetail = DatabaseRepository.getInstance(context).getPersonDetail(AppSession.getInstance().GetPersonDetailId());
                    if(personDetail != null)
                    {
                        personDetailFragment.updateDetails(personDetail);
                    }
                }
            }
        }
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            PersonListFragment personListFragment = (PersonListFragment)fm.findFragmentByTag(getResources().getString(R.string.listFragment));
            if(personListFragment != null)
            {
                personListFragment.setPersonData(personList);
                personListFragment.updateUI(false);
                personListFragment.updateInfo("", false);
            }
        }
        if(AppSession.getInstance().GetCountDownTime() == 0L)
        {
            updatePersonListRequest(false);
        }
    }

    private void updatePersonDetailRequest() {
        PersonDetail personDetail = null;
        if(AppSession.getInstance().GetPersonDetailId() > 0L)
        {
            personDetail = DatabaseRepository.getInstance(context).getPersonDetail(AppSession.getInstance().GetPersonDetailId());
        }
        if(personDetail != null)
        {
            updatePersonDetail();
        }
        else
        {
            if(!updateInProgress)
            {
                updateInProgress = true;
                updateResult = UpdateResult.error;
                final Thread loginThread = new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage(getString(R.string.loading_detail));
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(true);
                                progressDialog.show();
                            }
                        });

                        try {
                            AppSession currentAppSession = AppSession.getInstance();
                            String urlPath = AppConfiguration.ApiEndpointUrl + "/" + currentAppSession.GetPersonId();
                            DeviceConnectivityHelper.getInstance().setContext(context);
                            if (DeviceConnectivityHelper.getInstance().isInternetOn()) {
                                URL url = new URL(urlPath);
                                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                                request.setDefaultUseCaches(false);
                                request.setDoInput(true);
                                request.setDoOutput(false);
                                request.setRequestProperty("Accept-Encoding", "gzip");
                                request.setRequestProperty(HTTP.CONTENT_TYPE, "text/plain; charset=utf-8");
                                request.setConnectTimeout(AppConfiguration.DefaultConnectionTimeout);
                                request.setReadTimeout(AppConfiguration.DefaultSocketTimeout);
                                try {
                                    Object entity = request.getContent();
                                    if (entity != null && request.getResponseCode() == 200) {
                                        InputStream inStream = request.getInputStream();
                                        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
                                        StringBuilder sb = new StringBuilder();
                                        String result;
                                        while ((result = br.readLine()) != null) {
                                            sb.append(result);
                                        }
                                        result = sb.toString();
                                        inStream.close();
                                        request.disconnect();
                                        JSONObject object = new JSONObject(result);

                                        if (object.length() > 0) {
                                            updateResult = UpdateResult.newDataCollected;
                                            long selectedPersonId = AppSession.getInstance().GetPersonId();
                                            Person person = null;
                                            for(int i =0; i< personList.size(); i++)
                                            {
                                                Person tempPerson = personList.get(i);
                                                if(tempPerson.objectId == selectedPersonId)
                                                {
                                                    person = tempPerson;
                                                    break;
                                                }
                                            }
                                            if(person != null)
                                            {
                                                long personDetailId =  PersonDetail.syncFromJson(context, object);
                                                if(personDetailId > 0L)
                                                {
                                                    AppSession.getInstance().SetPersonDetailId(personDetailId);
                                                    person.detailId = personDetailId;
                                                    DatabaseRepository.getInstance(context).updatePerson(person.objectId, person.firstName, person.lastName, person.detailId);
                                                }
                                            }
                                        } else {
                                            updateResult = UpdateResult.noNewData;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.d("Authentication", "Exception occurred " + e.getMessage());
                                    updateResult = UpdateResult.error;
                                }
                            } else {
                                updateResult = UpdateResult.noInternetConnection;
                            }
                        } catch (Exception e) {
                            Log.d("Authentication", "Exception occurred " + e.getMessage());
                            updateResult = UpdateResult.error;
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                updateInProgress = false;
                                ProcessPersonDetailUpdateResult(updateResult);
                            }
                        });
                    }
                });
                loginThread.start();
            }

        }
    }

    private void saveSelectedPersonId(long personId)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(getResources().getString(R.string.selected_person_id), personId);
        AppSession.getInstance().SetPersonId(personId);
        editor.apply();
    }

    private void updateInfo(String info, boolean isError){
        PersonListFragment personListFragment;
        if (getResources().getBoolean(R.bool.is_landscape)) {
            personListFragment = (PersonListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.list_fragment);
            if(personListFragment != null)
            {

                personListFragment.setPersonData(personList);
                personListFragment.updateUI(false);
                personListFragment.updateInfo(info, isError);
                personListFragment.updateTimer();
            }
        }
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            personListFragment = (PersonListFragment) fm.findFragmentById(R.id.fragment_container);
            if(personListFragment != null)
            {
                personListFragment.setPersonData(personList);
                personListFragment.updateUI(false);
                personListFragment.updateInfo(info, isError);
                personListFragment.updateTimer();
            }
        }
    }
    private void updatePersonDetail()
    {
        if (getResources().getBoolean(R.bool.is_landscape)) {
            PersonDetail personDetail = DatabaseRepository.getInstance(context).getPersonDetail(AppSession.getInstance().GetPersonDetailId());
            PersonDetailFragment personDetailFragment = (PersonDetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.detail_fragment);
            if(personDetailFragment != null)
            {
                personDetailFragment.updateDetails(personDetail);
            }
        }
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            PersonListFragment personListFragment = (PersonListFragment) fm.findFragmentById(R.id.fragment_container);
            if(personListFragment != null)
            {
                personListFragment.stopTimer();
            }

            PersonDetailFragment personDetailFragment = new PersonDetailFragment();
            Bundle args = new Bundle();
            args.putLong(getResources().getString(R.string.selected_person_id), AppSession.getInstance().GetPersonDetailId());
            personDetailFragment.setArguments(args);

            fm.beginTransaction()
                    .replace(R.id.fragment_container, personDetailFragment, getResources().getString(R.string.detailFragment)).addToBackStack(null).commit();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Person person = personList.get(position);
        saveSelectedPersonId(person.objectId);
        AppSession.getInstance().SetPersonDetailId(person.detailId);
        updatePersonDetailRequest();
    }
}
