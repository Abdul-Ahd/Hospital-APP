package com.example.gis;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnlineDB {

    private static final String BASE_URL = "http:/192.168.137.218:3000/data";
    // Method to fetch data from OnlineDB based on table name
    public static void getData(String query, Context context, final OnGetDataListener listener) {

        new GetDataTask(context, listener).execute(BASE_URL + "?query=" + query);

    }
    public interface OnGetDataListener {
        void onDataRetrieved(Cursor cursor);
        void onError(String errorMessage);
    }

    public static void postData(String tableName, String data, Context context) {
        // Perform POST request to insert data
        new PostDataTask(context).execute(BASE_URL + "?table=" + tableName, data);

    }

    public static void updateData(String tableName, int id, String data) {
        // Perform PUT request to update data
        new PutDataTask().execute(BASE_URL + "/" + id + "?table=" + tableName, data);
    }

    public static void deleteData(String tableName, int id) {
        // Perform DELETE request to delete data
        new DeleteDataTask().execute(BASE_URL + "/" + id + "?table=" + tableName);
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static class GetDataTask extends AsyncTask<String, Void, List<String>> {
        private Context context;
        private OnGetDataListener listener;
        private String check;

        public GetDataTask(Context context, OnGetDataListener listener) {



            this.listener = listener;
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> resultList = new ArrayList<>();
            String urlStr = params[0];

            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response into a list of strings
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        resultList.add(jsonArray.getString(i));
                    }
                } else {
                    resultList.add("Server returned HTTP error code: " + responseCode);
                }
                connection.disconnect();
            } catch (IOException | JSONException e) {
                resultList.add("IOException occurred: " + e.getMessage());
            }

            return resultList;
        }

        @Override
        protected void onPostExecute(List<String> resultList) {
            if (!resultList.isEmpty()) {
                // Convert the list of strings to a cursor (this is just a dummy example)
                Cursor cursor = createCursorFromList(resultList);

                // Notify the listener with the retrieved cursor
                listener.onDataRetrieved(cursor);
            } else {
                listener.onError("Failed to retrieve data.");
            }

        }

        // Dummy method to create a cursor from a list of strings
        private Cursor createCursorFromList(List<String> resultList) {
            // Check if the resultList is empty or null
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            // Get the first JSON object to determine the column names
            JSONObject firstObject;
            try {
                firstObject = new JSONObject(resultList.get(0));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            // Define column names based on the keys of the first JSON object
            String[] columns = new String[firstObject.length()];
            Iterator<String> keys = firstObject.keys();
            int columnIndex = 0;
            while (keys.hasNext()) {
                columns[columnIndex++] = keys.next();
            }

            // Create a MatrixCursor with the defined columns
            MatrixCursor cursor = new MatrixCursor(columns);

            // Add rows to the cursor
            try {
                for (String data : resultList) {
                    JSONObject jsonObject = new JSONObject(data);

                    // Extract values for each column based on the keys
                    Object[] rowValues = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        rowValues[i] = jsonObject.optString(columns[i], "");
                    }

                    // Add a new row to the cursor with extracted values
                    cursor.addRow(rowValues);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return cursor;
        }



    }


    private static class PostDataTask extends AsyncTask<String, Void, String> {
        private Context context;

        public PostDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlStr = params[0];
            String data = params[1];
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
                int responseCode = connection.getResponseCode();
                Log.d("PostDataTask", "Response Code: " + responseCode);
                connection.disconnect();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Data posted successfully";
                } else {
                    return "Error posting data to server. Response Code: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }

    private static class PutDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String urlStr = params[0];
            String data = params[1];
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
                int responseCode = connection.getResponseCode();
                Log.d("PutDataTask", "Response Code: " + responseCode);
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class DeleteDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String urlStr = params[0];
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                Log.d("DeleteDataTask", "Response Code: " + responseCode);
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}