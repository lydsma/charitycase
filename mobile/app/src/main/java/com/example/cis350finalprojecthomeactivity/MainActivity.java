package com.example.cis350finalprojecthomeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
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

    String userID;

    List<Post> allPosts;
    List<Post> filteredPosts;
    Post newPost;

    int pageNum;

    PopupWindow popup;
    boolean newPostType;
    Category newPostCategory;

    Map<Category, String> categoryToString;

    Set<Post> pinnedPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get user ID
        userID = "";

        // start on page 0
        pageNum = 0;

        // set up map to link enum category to string representation
        categoryToString = new HashMap<Category, String>();
        setUpCategoryToString();

        // set up post collections and add dummy posts for now (until we connect to database)
        allPosts = new ArrayList<Post>();
        filteredPosts = new ArrayList<Post>();
        addDummyPosts();

        // refresh displayed posts on page
        refreshPage();

        // init pinnedPosts, eventually we will pull this from Mongo
        pinnedPosts = new HashSet<Post>();
    }


    /*
    ~~~~~~~~~~~~~~~  BUTTON CLICK FUNCTIONS  ~~~~~~~~~~~~~~~
    - onNewPostButtonClick
    - onSubmitButtonClick
    - onSortButtonClick
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
        newPost = new Post(newPostCategory, zip, newPostType, tags);
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

        // Getting user input filter configuration
        boolean includeAcademic =
                ((CheckBox) popupContent.findViewById(R.id.include_academic)).isChecked();
        boolean includeFood =
                ((CheckBox) popupContent.findViewById(R.id.include_food)).isChecked();
        boolean includeClothing =
                ((CheckBox) popupContent.findViewById(R.id.include_clothing)).isChecked();
        boolean includeGivers =
                ((CheckBox) popupContent.findViewById(R.id.seeking_receipients)).isChecked();
        boolean includeTakers =
                ((CheckBox) popupContent.findViewById(R.id.seeking_donors)).isChecked();

        // perform necessary filtering
        filteredPosts = new ArrayList<Post>();
        for (Post post : allPosts) {
            boolean filterCategory = (includeAcademic && post.category == Category.EDUCATION) ||
                    (includeFood && post.category == Category.FOOD) ||
                    (includeClothing && post.category == Category.CLOTHING);

            boolean filterType = (includeTakers && post.seekingDonations) ||
                    (includeGivers && !post.seekingDonations);

            boolean passesFilter = filterCategory && filterType;

            if (passesFilter) {
                filteredPosts.add(filteredPosts.size(), post);
            }
        }

        // set page num to 1 and refresh page
        pageNum = 0;
        refreshPage();

        // dismiss popup
        popup.dismiss();

    }

    //TODO: update for implementation of UpdatePost
    public void onPinPost(int postNum) {
        Post postToPin = filteredPosts.get(pageNum * 3 + postNum - 1);
        pinnedPosts.add(postToPin);
        postToPin.likePost(userID);

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


    /*
    ~~~~~~~~~~~~~~~  POST CLASS  ~~~~~~~~~~~~~~~
     */


    private class Post {

        Category category;
        String zipCode;
        boolean seekingDonations;
        Set<String> likes;
        Set<String> tags;
        boolean empty;

        // represents empty post, can't have a final static instance bc it's an inner class
        public Post() {
            empty = true;
        }

        public Post(Category cat, String zip, boolean type, Set<String> tgs) {
            empty = false;
            this.category = cat;
            this.zipCode = zip;
            this.seekingDonations = type;
            this.likes = new TreeSet<String>();
            this.tags = tgs;
        }

        public String tagsString() {
            if (this.tags == null || this.tags.size() == 0) {
                return "";
            } else {
                String out = "";
                for (String s : this.tags) {
                    out += "#" + s + " ";
                }

                return out.substring(0, out.length() - 1);
            }
        }

        public Set<String> tags() {
            if (this.tags == null) {
                return new TreeSet<String>();
            } else {
                return this.tags;
            }
        }

        public void likePost(String user) {
            likes.add(user);
        }

    }


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

        TextView titleView = findViewById(titleId);
        TextView categoryView = findViewById(categoryId);
        TextView zipView = findViewById(zipId);
        TextView typeView = findViewById(typeId);
        TextView tagsView = findViewById(tagsId);

        if (post.empty) {
            titleView.setText("No post to display");
            categoryView.setText("");
            zipView.setText("");
            typeView.setText("");
            tagsView.setText("");
        } else {
            String category = categoryToString.get(post.category);
            String zip = post.zipCode;
            String seekingDonations;

            if (post.seekingDonations) {
                seekingDonations = "Seeking donations";
            } else {
                seekingDonations = "Seeking recipients";
            }

            titleView.setText("Post " + (idx + 1));
            categoryView.setText(category);
            zipView.setText("Zip Code: " + zip);
            typeView.setText(seekingDonations);
            tagsView.setText("Tags: " + post.tagsString());
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
    ~~~~~~~~~~~~~~~  FUNCTIONS FOR SORTING POSTS  ~~~~~~~~~~~~~~~
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
        allPosts.add(0, new Post(Category.EDUCATION, "19104", true, null));
        allPosts.add(1, new Post(Category.FOOD, "19111", false, null));
        allPosts.add(2, new Post(Category.CLOTHING, "19210", false, null));

        for (Post post : allPosts) {
            filteredPosts.add(filteredPosts.size(), post);
        }
    }

    /*
    ~~~~~~~~~~~~~~~  OTHER FUNCTIONS  ~~~~~~~~~~~~~~~
    - parseTags
    -
     */

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


        // establish connection to http server


        // package new post information in json format


        // send http POST request to server
    }

    //TODO: implement this
    private List<Post> pullPosts() {

        String url = "dummy/url";


        // establish connection to http server


        // send http GET request to server and get response


        // unpack response into List<Post> instance and return
        return null;
    }

    class PushNewPost extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            try {
                URL url = urls[0];

                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                connect.setRequestMethod("POST");

                // do more here

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
                URL url = new URL("server/requestPosts");

                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                connect.setRequestMethod("GET");
                connect.connect();

                Scanner in = new Scanner(url.openStream());
                String msg = in.nextLine();

                JSONObject arrayOfPosts = new JSONObject(msg);

                // turn arrayOfPosts into allPosts

                return "";

            } catch (IOException e) {
                return e.toString();
            } catch (JSONException e) {
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
                            newPostCategory = Category.EDUCATION;
                            break;
                        case "food":
                            newPostCategory = Category.FOOD;
                            break;
                        case "clothing":
                            newPostCategory = Category.CLOTHING;
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


    private enum Category {
        FOOD, EDUCATION, CLOTHING, NULL
    }

    private enum PostType {
        SEEK_DONOR, SEEK_RECIPIENT
    }

    private void setUpCategoryToString() {
        categoryToString.put(Category.EDUCATION, "Category: Academic supplies");
        categoryToString.put(Category.FOOD, "Category: Food");
        categoryToString.put(Category.CLOTHING, "Category: Clothing");
        categoryToString.put(Category.NULL, "");
    }
}
