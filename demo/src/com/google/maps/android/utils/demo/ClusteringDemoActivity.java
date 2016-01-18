/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.utils.demo;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.utils.demo.model.BaseDemoActivity;
import com.google.maps.android.utils.demo.model.MyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class ClusteringDemoActivity extends BaseDemoActivity {
    private ClusterManager<MyItem> mClusterManager;
    private final static String mLogTag = "GeoJsonDemo";
    private final String mGeoJsonUrl
            = "https://data.sfgov.org/resource/ritf-b9ki.json";
    // initialize time variable

    double entry_time=0;
    double thirty = 0;




    @Override
    protected void startDemo() {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.773972, -122.431297), 10));

        mClusterManager = new ClusterManager<MyItem>(this, getMap());
        getMap().setOnCameraChangeListener(mClusterManager);

        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        // Download the GeoJSON file
        downloadGeoJsonFile.execute(mGeoJsonUrl);

        Toast.makeText(this, "Zoom in to find data", Toast.LENGTH_LONG).show();









    }



    private class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {



            try {
                URL url = new URL(mGeoJsonUrl);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestProperty("X-App-Token", "nOOgZHt8V7HN8W0HMJEV4fWgx");
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                // Close the stream
                reader.close();
                stream.close();


                JSONArray array;

                array = new JSONArray(result.toString());



                // get number of milliseconds in 30 days ago
                thirty = TimeUnit.MILLISECONDS.convert(-30, TimeUnit.DAYS);


                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String latitude = obj.optString("y").toString();
                    String longitude = obj.optString("x").toString();
                    String date = obj.optString("date").substring(0,10);

                    double lat = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);

                    DateFormat formatter ;
                    Date date_format;

                    formatter = new SimpleDateFormat("yyyy-mm-dd");
                    try {
                        date_format = formatter.parse(date);
                        entry_time= date_format.getTime();

                    } catch (ParseException e){
                        e.printStackTrace();
                    }




                    MyItem offsetItem = new MyItem(lat,longi);
                    if (entry_time > thirty) {
                        mClusterManager.addItem(offsetItem);

                    }



                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
                Log.e(mLogTag, "GeoJSON file could not be read");

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

        }


    }






}