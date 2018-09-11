package com.example.locatefaculty;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.BufferedReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by vibhor on 26-11-2017.
 */

public class purchase extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<SellBook> adapter;
    RelativeLayout purchase;

    //Add Emojicon
    EmojiconEditText emojiconEditText;
    EmojIconActions emojIconActions;
    private Drawer result;
    private Toolbar toolbar;
    private Typeface montserrat_regular;
    private String[] SOURCE_ARRAY = {"Chat Room","Back","Sell a book","Purchase a book"};
    private TextView mTitle;
    private AccountHeader accountHeader;
    private String SOURCE;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back)
        {
            Toast.makeText(purchase.this,"You are out of purchase section.", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                //Snackbar.make(chat,"Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else{
                //Snackbar.make(chat,"We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase1);
        purchase = (RelativeLayout)findViewById(R.id.purchase);
        createToolbar();
        createDrawer(savedInstanceState, toolbar, montserrat_regular);
        // emojIconActions.ShowEmojicon();

        //Check if not sign-in then navigate Signin page
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(purchase.this, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            //Load content
            displayChatMessage();
        }


    }
    private void createToolbar() {
        toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    private void createDrawer(Bundle savedInstanceState, final Toolbar toolbar, Typeface montserrat_regular) {
        SectionDrawerItem item1 = new SectionDrawerItem().withIdentifier(1).withName("CHAT SECTION");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Chat Room")
                .withIcon(R.drawable.chat).withTypeface(montserrat_regular);
        SectionDrawerItem item3 = new SectionDrawerItem().withIdentifier(3).withName("SELL BOOK SECTION")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Sell a book")
                .withIcon(R.drawable.purchase).withTypeface(montserrat_regular);
        SectionDrawerItem item5 = new SectionDrawerItem().withIdentifier(5).withName("PURCHASE BOOK SECTION")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName("Purchase a book")
                .withIcon(R.drawable.purchase).withTypeface(montserrat_regular);
        SectionDrawerItem item7 = new SectionDrawerItem().withIdentifier(7).withName("")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item8 = new PrimaryDrawerItem().withIdentifier(8).withName("Back")
                /*.withIcon(R.drawable.logout)*/.withTypeface(montserrat_regular);
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_placeholder)
                .withSavedInstance(savedInstanceState)
                .build();
        result = new DrawerBuilder()
                .withAccountHeader(accountHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(1)
                .addDrawerItems(item1, item2, item3, item4,item5, item6, item7, item8/*, item9,
                        item10, item11, item12, item13 /*item14/*, item15, item16, item17, item18*/)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int selected = (int) (long) drawerItem.getIdentifier();
                        switch (selected) {
                            case 2:
                                SOURCE = SOURCE_ARRAY[0];
                                startActivity(new Intent(purchase.this, chat.class));
                                //onLoadingSwipeRefreshLayout();
                                // mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 8:
                                SOURCE = SOURCE_ARRAY[1];
                                startActivity(new Intent(purchase.this, ProfileActivity.class));
                                //onLoadingSwipeRefreshLayout();
                                //mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 4:
                                SOURCE = SOURCE_ARRAY[2];
                                startActivity(new Intent(purchase.this, sell.class));
                                //onLoadingSwipeRefreshLayout();
                                //mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 6:
                                SOURCE = SOURCE_ARRAY[3];
                                startActivity(new Intent(purchase.this, purchase.class));
                                //nLoadingSwipeRefreshLayout();
                                //mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                          /*  case 7:
                                SOURCE = SOURCE_ARRAY[4];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 8:
                                SOURCE = SOURCE_ARRAY[5];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 9:
                                SOURCE = SOURCE_ARRAY[6];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 11:
                                SOURCE = SOURCE_ARRAY[7];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 12:
                                SOURCE = SOURCE_ARRAY[8];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 13:
                                SOURCE = SOURCE_ARRAY[9];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                           /* case 15:
                                openAboutActivity();
                                break;
                            case 16:
                                Intent browserSource = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://github.com/debo1994/MyTimes"));
                                startActivity(browserSource);
                                break;
                            case 17:
                                Intent browserAPI = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://newsapi.org/"));
                                startActivity(browserAPI);
                                break;
                            case 18:
                                sendEmail();
                                break;*/
                            default:
                                break;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }



    private void displayChatMessage() {

        ListView listOfMessage = (ListView)findViewById(R.id.list_of_purchase);
        adapter = new FirebaseListAdapter<SellBook>(this,SellBook.class,R.layout.plist_item,FirebaseDatabase.getInstance().getReference().child("book"))
        {
            @Override
            protected void populateView(View v, SellBook model, int position) {
                //Get references to the views of list_item.xml
                TextView name,condition,price,contact;
                name = (TextView) v.findViewById(R.id.pname);
                condition= (TextView) v.findViewById(R.id.pcondition);
                price = (TextView) v.findViewById(R.id.pprice);
                contact = (TextView) v.findViewById(R.id.pcontact);
                String k=model.getBookname();
                String t1=model.getCondition();
                String y=model.getPrice();
                String t=model.getContact();
                name.setText(k);
                condition.setText(t1);
                price.setText(y);
                contact.setText(t);

            }
        };
        listOfMessage.setAdapter(adapter);
    }
}