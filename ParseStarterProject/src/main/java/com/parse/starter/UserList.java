package com.parse.starter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ArrayList<String> users;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        if (ParseUser.getCurrentUser().get("isFollowing") == null) {
            List<String> empthyList = new ArrayList<String>();
            ParseUser.getCurrentUser().put("isFollowing", empthyList);

        }


        users = new ArrayList();
//        users.add("James");
//        users.add("Bob");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setAdapter(arrayAdapter);

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    Log.i("AppInfo", "Row is checked");
                    ParseUser.getCurrentUser().getList("isFollowing").add(users.get(position));
                    ParseUser.getCurrentUser().saveInBackground();
                } else {
                    Log.i("AppInfo", "Row is not checked");
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    users.clear();
                    for (ParseUser user : objects) {
                        users.add(user.getUsername);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    for (String username : users) {
                        if (ParseUser.getCurrentUser().getList("isFollowing").contains("username")) {
                            listView.setItemChecked(users.indexOf(username), true);
                        }
                    }

                } else {
                    e.printStackTrace();
                }

            }
        });

//        if (ParseUser.getCurrentUser().get("isFollowing") == null) {
//
//            List<String> emptyList = new ArrayList<String>();
//            ParseUser.getCurrentUser().put("isFollowing", emptyList);
//
//        }

//        users = new ArrayList<String>();
//
//
//
//        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
//
//        final ListView listView = (ListView) findViewById(R.id.listView);
//        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//        listView.setAdapter(arrayAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                CheckedTextView checkedTextView = (CheckedTextView) view;
//                if (checkedTextView.isChecked()) {
//
//                    Log.i("AppInfo", "Row is checked");
//
//
//                    ParseUser.getCurrentUser().getList("isFollowing").add(users.get(position));
//                    ParseUser.getCurrentUser().saveInBackground();
//
//
//                } else {
//
//                    Log.i("AppInfo", "Row is not checked");
//
//                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));
//                    ParseUser.getCurrentUser().saveInBackground();
//
//                }
//
//            }
//        });


//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//
//                    users.clear();
//
//                    for (ParseUser user : objects) {
//
//                        users.add(user.getUsername());
//
//                    }
//
//                    arrayAdapter.notifyDataSetChanged();
//
//                    for (String username : users) {
//
//                        if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
//
//                            listView.setItemChecked(users.indexOf(username), true);
//
//                        }
//
//                    }
//
//
//                } else {
//
//                    e.printStackTrace();
//
//                }
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tweet) {
            AlertDialog.Builder builder = new AlertDialog.Builder();
            builder.setTitle("Send a Tweet");
            final EditText tweetContent = new EditText(this);
            builder.setView(tweetContent);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Log.i("AppInfo", String.valueOf(tweetContent.getText()));

                    ParseObject tweet = new ParseObject("tweet");
                    tweet.put("content",String.valueOf(tweetContent.getText()));
                    tweet.put("username", String.valueOf(ParseUser.getCurrentUser().getUsername()));
                    tweet.saveInBackground( new SaveCallback(){
                     @Override
                        public void done(ParseException e){
                         if (e==null){
                             Toast.makeText(getApplicationContext(), "Tweet was sent", Toast.LENGTH_LONG).show();
                         }else {
                             Toast.makeText(getApplicationContext(), "Tweet was not sent!", Toast.LENGTH_LONG).show();
                         }
                     }
                    });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
