package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * Created by mbankole on 6/28/17.
 */

public class ComposeTweetDialogFragment extends DialogFragment implements View.OnClickListener{
// ...

    EditText editText;
    TextView charCount;
    ProgressBar progressBar;
    Button btSubmit;
    TextView tvTitle;
    private final String TAG = "ComposeActivityFragment";
    private TwitterClient client;
    long reply_id;

    public ComposeTweetDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetDialogFragment newInstance(String title) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public static ComposeTweetDialogFragment newInstance(String title, String screenName, long rtId) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("screenName", screenName);
        args.putLong("rtId", rtId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        client = TwitterApp.getRestClient();
        editText = (EditText)view.findViewById(R.id.etTweetBody);
        charCount = (TextView)view.findViewById(R.id.tvCharCount);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        btSubmit = (Button)view.findViewById(R.id.btSubmit);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);

        btSubmit.setOnClickListener(this);
        tvTitle.setText(getArguments().getString("title"));

        String replyAt = getArguments().getString("screenName");
        reply_id = getArguments().getLong("rtId", -1);
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
                String charCountString = (140 - editText.getText().toString().length()) + "/140 left";
                charCount.setText(charCountString);
            }
        });
    }

    @Override
    public void onClick(View v) {
        onSendTweet(v);
    }

    public interface ComposeTweetDialogListener {
        void onFinishEditTweet(Tweet tweet);
    }


    public void onSendTweet(View view) {
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
                    ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                    listener.onFinishEditTweet(tweet);
                    dismiss();
                    return;
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