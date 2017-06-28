package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class ComposeActivity extends AppCompatActivity {

    EditText editText;
    TextView charCount;
    ProgressBar progressBar;
    private final String TAG = "ComposeActivity";
    private TwitterClient client;
    long reply_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient();
        editText = (EditText)findViewById(R.id.etTweetBody);
        charCount = (TextView)findViewById(R.id.tvCharCount);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        String replyAt = getIntent().getStringExtra("reply");
        reply_id = getIntent().getLongExtra("reply_id", -1);
        if (replyAt != null) {
            editText.setText("@" + replyAt + " ");
            editText.setSelection(editText.getText().toString().length());
        }

        //listener for the remaining characters
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {}
            @Override
            public void afterTextChanged(Editable s)
            {
                String charCountString = (140 - editText.getText().toString().length()) + "/140 remaining";
                charCount.setText(charCountString);
            }
        });
    }

    public void onSendTweet(View view) {
        Intent data = new Intent();
        progressBar.setVisibility(View.VISIBLE);
        String text = editText.getText().toString();
        sendTweet(text);
        //Log.i("Compose", text);
        //setResult(RESULT_OK, data); // set result code and bundle data for response
        //finish();
    }

    private void sendTweet(String message) {
        client.sendTweet( message, reply_id,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                debug("first on success");
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    User user = tweet.getUser();
                    Intent data = new Intent();
                    data.putExtra("tweet", tweet);
                    data.putExtra("user", user);
                    setResult(RESULT_OK, data);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                debug("second on success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                debug("error 1");
                log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                debug("error 2");
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                debug("error 3");
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public void debug(String message) {
        log.d(TAG, message);
    }
}
