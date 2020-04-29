package com.example.cis350finalprojecthomeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    List<Post> allPosts;
    List<Post> filteredPosts;
    boolean newPostType;
    Post.Category newPostCategory;
    Post newPost;

    String userID;
    Set<Post> pinnedPosts;

    PopupWindow popup;
    Map<Post.Category, String> categoryToString;
    Map<String, Post.Category> stringToCategory;
    int pageNum;
    public static final String ID = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = "";

        // login and get user ID
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);

        // start on page 0
        pageNum = 0;

        // set up map to link enum category to string representation
        categoryToString = new HashMap<Post.Category, String>();
        setUpCategoryToString();

        // set up post collections and add dummy posts for now (until we connect to database)
        allPosts = pullPosts();
        filteredPosts = new ArrayList<Post>(allPosts);

        // refresh displayed posts on page
        refreshPage();

        // init pinnedPosts, eventually we will pull this from Mongo
        pinnedPosts = new HashSet<Post>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            userID = data.getStringExtra(LoginActivity.EMAIL);
            TextView welcomeTextView = findViewById(R.id.welcome_text);
            welcomeTextView.setText(userID);
        }
    }


    /*
    ~~~~~~~~~~~~~~~  BUTTON CLICK FUNCTIONS  ~~~~~~~~~~~~~~~
    - onNewPostButtonClick
    - onSubmitButtonClick
    - onFilterClick
    - onFilterConfirmClick
    - onPinPost (1, 2, 3)
    - prevPageClick
    - nextPageClick
     */


    public void onNewPostButtonClick(View view) {

        // Set up popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.post_popup_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup = new PopupWindow(popupView, width, height, true);

        // Launch popup and define content
        popup.showAtLocation(view, Gravity.CENTER, 0, 0);
        View popupContent = popup.getContentView();

        // Defining spinners
        Spinner categorySpinner = popupContent.findViewById(R.id.popup_category);
        Spinner typeSpinner = popupContent.findViewById(R.id.popup_type);

        // Setting up spinner listeners + adapters
        setUpSimpleSpinner(categorySpinner, R.array.category_array, "post-category");
        setUpSimpleSpinner(typeSpinner, R.array.type_array, "post-type");

    }

    public void onSubmitButtonClick(View view) {

        // find the popup content and get the zip code, other post info stored as attributes
        View popupContent = popup.getContentView();
        EditText zipEditText = (EditText) popupContent.findViewById(R.id.popup_address_content);
        String zip = zipEditText.getText().toString();
        EditText tagsEditText = (EditText) popupContent.findViewById(R.id.popup_tags_content);
        Set<String> tags = parseTags(tagsEditText.getText().toString());

        // make the new post and add it to all posts
        newPost = new Post(newPostCategory, zip, newPostType, tags, null);
        allPosts.add(allPosts.size(), newPost);
        filteredPosts.add(filteredPosts.size(), newPost);

        try {
            AsyncTask pushPost = new PushNewPost();
            // pushPost.execute();
            // String response = (String) pushPost.get();

        } catch (Exception e) {
            // this means the post was not properly pushed to Mongo
        }

        // refresh page
        refreshPage();

        // exit popup window
        popup.dismiss();
    }

    public void onFilterClick(View view) {

        // Set up popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.filter_popup, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup = new PopupWindow(popupView, width, height, true);

        // Launch popup
        popup.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    //TODO: add in zip code filtering
    public void onFilterConfirmClick(View view) {

        // get popup content
        View popupContent = popup.getContentView();

        filteredPosts = new ArrayList<Post>(allPosts);
        List<Post> postsToRemove = new ArrayList<Post>();

        boolean filterCategory =
                ((CheckBox) popupContent.findViewById(R.id.filter_category)).isChecked();

        boolean filterType =
                ((CheckBox) popupContent.findViewById(R.id.filter_type)).isChecked();

        if (filterCategory) {
            boolean includeAcademic =
                    ((CheckBox) popupContent.findViewById(R.id.include_academic)).isChecked();
            boolean includeFood =
                    ((CheckBox) popupContent.findViewById(R.id.include_food)).isChecked();
            boolean includeClothing =
                    ((CheckBox) popupContent.findViewById(R.id.include_clothing)).isChecked();

            for (Post post : filteredPosts) {
                boolean passesFilter = (includeAcademic && post.category == Post.Category.EDUCATION) ||
                        (includeFood && post.category == Post.Category.FOOD) ||
                        (includeClothing && post.category == Post.Category.CLOTHING);
                if (!passesFilter) {
                    postsToRemove.add(post);
                }
            }

            filteredPosts.removeAll(postsToRemove);
            postsToRemove = new ArrayList<Post>();
        }


        if (filterType) {
            boolean includeGivers =
                    ((CheckBox) popupContent.findViewById(R.id.seeking_receipients)).isChecked();
            boolean includeTakers =
                    ((CheckBox) popupContent.findViewById(R.id.seeking_donors)).isChecked();

            for (Post post : filteredPosts) {
                boolean passesFilter = (includeTakers && post.seekingDonations) ||
                        (includeGivers && !post.seekingDonations);
                if (!passesFilter) {
                    postsToRemove.add(post);
                }
            }

            filteredPosts.removeAll(postsToRemove);
            postsToRemove = new ArrayList<Post>();
        }

        // set page num to 1 and refresh page
        pageNum = 0;
        refreshPage();

        // dismiss popup
        popup.dismiss();

    }

    //TODO: update for implementation of UpdatePost
    public void onPinPost(int postNum) {

        int idx = pageNum * 3 + postNum - 1;
        if (idx >= filteredPosts.size()) {
            return;
        }

        Post postToPin = filteredPosts.get(idx);

        if (postToPin.likes.contains(userID)) {
            pinnedPosts.remove(postToPin);
            postToPin.unlikePost(userID);
        } else {
            pinnedPosts.add(postToPin);
            postToPin.likePost(userID);
        }

        displayPostInIdx(postToPin, postNum - 1);

        try {

            AsyncTask updatePost = new UpdatePost();
            updatePost.execute();
            updatePost.get();

        } catch (Exception e) {
            // oops!
        }
    }

    public void pinPost1(View view) {
        onPinPost(1);
    }

    public void pinPost2(View view) {
        onPinPost(2);
    }

    public void pinPost3(View view) {
        onPinPost(3);
    }

    public void onSearchTagsClick(View view) {

        // Set up popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.tags_popup, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup = new PopupWindow(popupView, width, height, true);

        // Launch popup
        popup.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void onSearchConfirmClick(View view) {

        // get popup content
        View popupContent = popup.getContentView();

        // Getting user input filter configuration
        boolean strictSearch =
                ((RadioButton) popupContent.findViewById(R.id.allTagMatch)).isChecked();

        EditText inputTagsEditText = (EditText) popupContent.findViewById(R.id.search_tags);

        Set<String> searchTags = parseTags(inputTagsEditText.getText().toString());

        // run search
        filteredPosts = new ArrayList<Post>();

        if (strictSearch) {
            for (Post post : allPosts) {
                if (post.tags().containsAll(searchTags)) {
                    filteredPosts.add(filteredPosts.size(), post);
                }
            }
        } else {
            for (Post post : allPosts) {
                if (!Collections.disjoint(post.tags(), searchTags)) {
                    filteredPosts.add(filteredPosts.size(), post);
                }
            }
        }

        // set page num to 1 and refresh page
        pageNum = 0;
        refreshPage();

        // dismiss popup
        popup.dismiss();

    }

    public void nextPageClick(View view) {
        if (pageNum * 3 + 3 < filteredPosts.size()) {
            pageNum++;
        }

        refreshPage();
    }

    public void prevPageClick(View view) {
        if (pageNum > 0) {
            pageNum--;
        }

        refreshPage();
    }

    public void onProfileClick(View view) {
        // go to profile view
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra(ID, userID);
        startActivityForResult(intent, 1);
    }


    /*
    ~~~~~~~~~~~~~~~  POST CLASS  ~~~~~~~~~~~~~~~
     */





    /*
    ~~~~~~~~~~~~~~~  FUNCTIONS FOR CREATING AND DISPLAYING POSTS  ~~~~~~~~~~~~~~~
    - initPosts
    - refreshPosts
    - sortPostsBy
    - createDummyPosts
     */

    private void initPosts() {

        allPosts = new ArrayList<Post>();

        try {
            AsyncTask pullPosts = new PullPostsFromDB();
            pullPosts.execute();
            pullPosts.get();
        } catch (Exception e) {
            // this means we didn't correctly pull posts from Mongo
            Toast.makeText(this, "couldn't pull posts from Mongo :/", Toast.LENGTH_LONG);
        }

    }

    private void displayPostInIdx(Post post, int idx) {

        if (post == null) {
            return;
        }

        if (idx > 2) {
            idx = 2;
        } else if (idx < 0) {
            idx = 0;
        }

        int idxId = idx + 1;

        int titleId = idNameToInt("post" + idxId + "title");
        int categoryId = idNameToInt("post" + idxId + "category");
        int zipId = idNameToInt("post" + idxId + "zip");
        int typeId = idNameToInt("post" + idxId + "type");
        int tagsId = idNameToInt("post" + idxId + "tags");
        int pinsId = idNameToInt("post" + idxId + "pins");
        int comsId = idNameToInt("post" + idxId + "comments");
        int pinButtonId = idNameToInt("post" + idxId + "pinbutton");

        TextView titleView = findViewById(titleId);
        TextView categoryView = findViewById(categoryId);
        TextView zipView = findViewById(zipId);
        TextView typeView = findViewById(typeId);
        TextView tagsView = findViewById(tagsId);
        TextView pinsView = findViewById(pinsId);
        TextView comsView = findViewById(comsId);
        Button pinButton = findViewById(pinButtonId);

        if (post.empty) {
            titleView.setText("No post to display");
            categoryView.setText("");
            zipView.setText("");
            typeView.setText("");
            tagsView.setText("");
            pinsView.setText("");
        } else {
            String category = categoryToString.get(post.category);
            String zip = post.zipCode;
            String seekingDonations;
            ArrayList<String> comments = post.comments;

            if (post.seekingDonations) {
                seekingDonations = "Seeking donations";
            } else {
                seekingDonations = "Seeking recipients";
            }

            titleView.setText("Post " + (idx + 1));
            categoryView.setText(category);
            zipView.setText("Zip Code: " + zip);
            comsView.setText("Comments: " + comments.toString());
            typeView.setText(seekingDonations);
            tagsView.setText("Tags: " + post.tagsString());

            int numPins = post.numPins();
            String pinText = "";
            if (numPins == 0) {
                pinText = "No users have pinned this post";
            } else if (numPins == 1) {
                pinText = "1 user has pinned this post";
            } else {
                pinText = numPins + " users have pinned this post";
            }
            pinsView.setText(pinText);

            if (post.likes.contains(userID)) {
                pinButton.setText("Unpin");
            } else {
                pinButton.setText("Pin");
            }

        }

    }

    private void refreshPage() {

        for (int i = pageNum * 3; i < (pageNum * 3) + 3; i++) {
            if (i < filteredPosts.size()) {
                displayPostInIdx(filteredPosts.get(i), i - (pageNum * 3));
            } else {
                displayPostInIdx(new Post(), i - (pageNum * 3));
            }

        }

        TextView pageNumView = findViewById(R.id.pageNum);
        pageNumView.setText("Page" + (pageNum + 1));

    }

    /*
    ~~~~~~~~~~~~~~~  FUNCTIONS FOR SORTING + SEARCHING POSTS  ~~~~~~~~~~~~~~~
    - sortPostsBy
     */

    private void sortPostsBy(final String attributeId) {

        Comparator<Post> sortMethod = new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                switch (attributeId) {
                    case "post-category":
                        return o1.category.compareTo(o2.category);
                    case "post-location":
                        return o1.zipCode.compareTo(o2.zipCode);
                    default:
                        return 0;
                }
            }
        };

        Collections.sort(allPosts, sortMethod);

    }

    private void addDummyPosts() {
        allPosts.add(0, new Post(Post.Category.EDUCATION, "19104", true, null, null));
        allPosts.add(1, new Post(Post.Category.FOOD, "19111", false, null, null));
        allPosts.add(2, new Post(Post.Category.CLOTHING, "19210", false, null, null));

        for (Post post : allPosts) {
            filteredPosts.add(filteredPosts.size(), post);
        }
    }

    private Set<String> parseTags(String textInput) {
        if (textInput == null) {
            return new TreeSet<String>();
        }

        List<String> tags = Arrays.asList(textInput.toLowerCase().split(",| "));

        Set<String> out = new TreeSet<String>();
        for (String s : tags) {
            if (s.length() > 0) {
                out.add(s);
            }
        }

        return out;

    }

    /*
    ~~~~~~~~~~~~~~~  FUNCTIONS FOR COMMUNICATING WITH BACKEND  ~~~~~~~~~~~~~~~
    - pushPost
    - pullPosts
     */

    //TODO: implement this
    private void pushPost(Post post) {
        return;
    }

    //TODO: implement this
    private List<Post> pullPosts() {

        // try to establish a connection
        try {
            URL url = new URL("http://10.0.2.2:3000/mobilegethomeposts");
            AsyncTask<URL, String, String> task = new PullPostsFromDB();
            task.execute(url);

            // get the response and Toast it
            String msg = task.get();

            JSONObject jsonObj = new JSONObject(msg);
            JSONArray jsonArr = jsonObj.getJSONArray("posts");
            List<Post> pulledPosts = new ArrayList<Post>();

            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    JSONObject jsonPost = jsonArr.getJSONObject(i);
                    boolean type = jsonPost.getString("type").toLowerCase().equals("recipient");
                    String zip = jsonPost.getString("zip");
                    String category = jsonPost.getString("category");
                    Set<String> tags = new TreeSet<String>();
                    JSONArray comments = jsonPost.getJSONArray("comments");

                    ArrayList com = new ArrayList<String>();
                    for (int j = 0; i < comments.length(); i++) {
                        com.add(comments.getJSONObject(j));
                    }

                    tags.add(jsonPost.getString("tags"));
                    pulledPosts.add(new Post(category, zip, type, tags, com));
                } catch (Exception e) {}
            }


            return pulledPosts;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // unpack response into List<Post> instance and return
        return new ArrayList<Post>();
    }

    class PushNewPost extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            try {
                URL url = urls[0];

                // establish connection to http server
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                connect.setRequestMethod("POST");

                // package new post information in json format


                // send http POST request to server

                return "";

            } catch (Exception e) {
                return e.toString();
            }

        }
    }

    class UpdatePost extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... objects) {
            try {
                URL url = new URL("server/updatePost");

                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                connect.setRequestMethod("POST");
                connect.connect();

                return "";
            } catch (Exception e) {
                return e.toString();
            }
        }
    }

    class PullPostsFromDB extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {


            try {
                URL url = urls[0];

                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                // send http GET request to server
                connect.setRequestMethod("GET");
                connect.connect();

                // read response using Scanner
                Scanner in = new Scanner(url.openStream());
                String msg = "";

                while (in.hasNext()) {
                    msg = in.useDelimiter("\\A").next();
                }

                // JSONObject arrayOfPosts = new JSONObject(msg);

                // turn arrayOfPosts into allPosts
                Log.v("string results", msg);
                return msg;

            } catch (IOException e) {
                return e.toString();
            } catch (Exception e) {
                return e.toString();
            }
        }

    }



    /*
    ~~~~~~~~~~~~~~~  FUNCTIONS FOR INTERACTING WITH ANDROID VIEWS  ~~~~~~~~~~~~~~~
    - setUpSimpleSpinner
    - factoryItemListener
    - idNameToInt
     */


    private void setUpSimpleSpinner(Spinner spinner, int resourceID, String attributeID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, resourceID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(factoryItemListener(attributeID));
    }

    private AdapterView.OnItemSelectedListener factoryItemListener(final String attributeID) {
        return new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString().toLowerCase();

                if (attributeID.equals("post-category")) {
                    switch (selection) {
                        case "academic":
                            newPostCategory = Post.Category.EDUCATION;
                            break;
                        case "food":
                            newPostCategory = Post.Category.FOOD;
                            break;
                        case "clothing":
                            newPostCategory = Post.Category.CLOTHING;
                            break;
                        default:
                            break;
                    }
                } else if (attributeID.equals("post-type")) {
                    newPostType = selection.equals("seeking donations");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private int idNameToInt(String idName) {
        return getResources().getIdentifier(
                idName, "id", getPackageName());
    }


    /*
    ~~~~~~~~~~~~~~~  OLD (UNUSED) CLASSES AND FUNCTIONS  ~~~~~~~~~~~~~~~
    - enum Category
    - enum PostType
     */

    private void setUpCategoryToString() {
        categoryToString.put(Post.Category.EDUCATION, "Category: Academic supplies");
        categoryToString.put(Post.Category.FOOD, "Category: Food");
        categoryToString.put(Post.Category.CLOTHING, "Category: Clothing");
        categoryToString.put(Post.Category.NULL, "");
    }
}
