package com.example.vibhor.nearbyrestaurants;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vibhor on 06-04-2018.
 */

public class GetNearbyPlaceID extends AsyncTask<Object ,String ,String>
{
    //creating string to store url coming from mainActivity
    private String mWebServiceURL;

    //creating context
    private Context mcontext;

    //creating Interface object
    private HandleIdListener handleIdListener;

    //constructor
    public GetNearbyPlaceID(Context context, String url, HandleIdListener listener)
    {
        //setting context
        mcontext = context;

        //storing url coming from MainActivity
        mWebServiceURL = url;

        handleIdListener = listener;

    }

    @Override
    protected String doInBackground(Object... objects)
    {
        //creating okhttp client
        OkHttpClient okHttpClient = new OkHttpClient();

        //setting connectionTimeout in case of no internet or slow internet
        okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(120,TimeUnit.SECONDS);

        //generating request to fetch data
        Request request = new Request.Builder().url(mWebServiceURL).build();

        //string to store response of request
        String responseData=null;

        try {

            //getting response when request is passed
            Response response = okHttpClient.newCall(request).execute();

            //checking if request is successful
            if(response.isSuccessful())
            {
                //storing response of request in string format
                responseData = response.body().string();
            }
            else
            {
                //displaying log message
                Log.e("Error","Failure");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //returning response
        return responseData;
    }

    //onPostExecute method
    @Override
    protected void onPostExecute(String json)
    {
        super.onPostExecute(json);

        //returning json string to context
        handleIdListener.updateId(json);
    }

}
