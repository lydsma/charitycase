package com.example.cis350finalprojecthomeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String userID;

    List<Post> allPosts;
    int pageNum;

    PopupWindow newPostPopup;
    String newPostType;
    String newPostCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNum = 0;

        allPosts = new ArrayList<Post>();

        // work with dummy posts for now (until we connect to database)
        addDummyPosts();

        // Refresh directory of posts
        refreshPage();

        // Home activity has button for new post and spinner for sorting posts
        Button newPost = (Button) findViewById(R.id.newPost);
        Spinner sortPosts = (Spinner) findViewById(R.id.sortPosts);

        // Set up sort post spinner contents
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortPosts.setAdapter(sortAdapter);

        // Toast.makeText(this, allPosts.size(), Toast.LENGTH_SHORT);

//        // Set up sort post selection listener
//        sortPosts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//
//        });

    }


    /*
    ~~~~~~~~~~~~~~~  BUTTON CLICK FUNCTIONS  ~~~~~~~~~~~~~~~
    - onNewPostButtonClick
    - onSubmitButtonClick
    - onSortButtonClick
     */


    public void onNewPostButtonClick(View view) {

        // Set up popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.post_popup_window, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        newPostPopup = new PopupWindow(popupView, width, height, true);

        // Launch popup and define content
        newPostPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        View popupContent = newPostPopup.getContentView();

        // Defining spinners
        Spinner categorySpinner = popupContent.findViewById(R.id.popup_category);
        Spinner typeSpinner = popupContent.findViewById(R.id.popup_type);

        // Setting up spinner listeners + adapters
        setUpSimpleSpinner(categorySpinner, R.array.category_array, "category");
        setUpSimpleSpinner(typeSpinner, R.array.type_array, "type");

    }

    public void onSubmitButtonClick(View view) {

        // find the popup content and get the zip code, other post info stored as attributes
        View popupContent = newPostPopup.getContentView();
        EditText zipEditText = (EditText) popupContent.findViewById(R.id.popup_address_content);
        String zip = zipEditText.getText().toString();

        // make the new post and add it to all posts
        Post newPost = new Post(newPostCategory, zip, newPostType);

        int tl = allPosts.size();

        allPosts.add(tl, newPost);
        displayPostInIdx(allPosts.get(tl), 0);

        // exit popup window
        newPostPopup.dismiss();
    }

    public void onSortButtonClick(View view) {

    }

    public void refreshButtonClick(View view) {

    }

    public void nextPageClick(View view) {
        pageNum++;
        refreshPage();

    }

    public void prevPageClick(View view) {
        if (pageNum == 0) {
            return;
        } else {
            pageNum--;
            refreshPage();
        }
    }


    /*
    ~~~~~~~~~~~~~~~  POST CLASS  ~~~~~~~~~~~~~~~
     */


    private class Post {

        String category;
        String zipCode;
        String postType;
        List<String> likes;

        public Post(String cat, String zip, String type) {
            this.category = cat;
            this.zipCode = zip;
            this.postType = type;
            this.likes = new ArrayList<String>();
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
    }

    private void displayPostInIdx(Post post, int idx) {

        if (post == null) {
            return;
        }

        if (idx > 3) {
            idx = 3;
        } else if (idx < 0) {
            idx = 0;
        }

        int idxId = idx + 1;

        int titleId = idNameToInt("post" + idxId + "title");
        int categoryId = idNameToInt("post" + idxId + "category");
        int zipId = idNameToInt("post" + idxId + "zip");
        int typeId = idNameToInt("post" + idxId + "type");

        String category = post.category;
        String zip = post.zipCode;
        String type = post.postType;

        TextView titleView = findViewById(titleId);
        TextView categoryView = findViewById(categoryId);
        TextView zipView = findViewById(zipId);
        TextView typeView = findViewById(typeId);

        titleView.setText("Post " + (idx + 1));
        categoryView.setText(category);
        zipView.setText(zip);
        typeView.setText(type);

    }

    private void refreshPage() {

        for (int i = pageNum * 4; i < Math.min((pageNum * 4) + 4, allPosts.size()); i++) {
            displayPostInIdx(allPosts.get(i), i - (pageNum * 4));
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
                    case "category":
                        return o1.category.compareTo(o2.category);
                    case "type":
                        return o1.postType.compareTo(o2.postType);
                    case "location":
                        return o1.zipCode.compareTo(o2.zipCode);
                    default:
                        return 0;
                }
            }
        };

        Collections.sort(allPosts, sortMethod);

    }

    private void addDummyPosts() {
        allPosts.add(0, new Post("academic", "19104", "seeking donations"));
        allPosts.add(1, new Post("food", "19111", "offering donations"));
        allPosts.add(2, new Post("clothing", "19210", "offering donations"));
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
        return null;
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
                String selection = parent.getItemAtPosition(position).toString();

                if (attributeID.equals("category")) {
                    newPostCategory = selection;
                } else if (attributeID.equals("type")) {
                    newPostType = selection;
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
        FOOD, EDUCATION, CLOTHING
    }

    private enum PostType {
        SEEK_DONOR, SEEK_RECIPIENT
    }
}
