package com.example.locatefaculty;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public class sell extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    ScrollView sell;
    //Add Emojicon
    EditText name,price,condition,contact;
    Button upload;
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
            Toast.makeText(sell.this,"You are out of selling section.", Toast.LENGTH_SHORT).show();
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

            }
            else{
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell1);
        createToolbar();
        createDrawer(savedInstanceState, toolbar, montserrat_regular);
        sell = (ScrollView)findViewById(R.id.sellm);
        upload = (Button) findViewById(R.id.sellbuuton);
        name=(EditText)findViewById(R.id.sellname);
        price=(EditText)findViewById(R.id.sellprice);
        condition=(EditText)findViewById(R.id.sellcondition);
        contact=(EditText)findViewById(R.id.sellphone);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(name.getText().toString())){
                    Toast.makeText(sell.this,"Please enter book name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(price.getText().toString())){
                    Toast.makeText(sell.this,"Please enter price",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(condition.getText().toString())){
                    Toast.makeText(sell.this,"Please enter book condition",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(contact.getText().toString())){
                    Toast.makeText(sell.this,"Please enter your contact",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    FirebaseDatabase.getInstance().getReference().child("book").push().setValue(new SellBook(name.getText().toString(),price.getText().toString(),condition.getText().toString(),contact.getText().toString()));
                    Toast.makeText(sell.this,"successfully uploaded",Toast.LENGTH_SHORT).show();
                    name.setText("");
                    price.setText("");
                    contact.setText("");
                    condition.setText("");
                    startActivity(new Intent(sell.this, purchase.class));

                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Check if not sign-in then navigate Signin page
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(sell.this, "Welcome to seeling book page  " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            //Load content
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
                                startActivity(new Intent(sell.this, chat.class));
                                //onLoadingSwipeRefreshLayout();
                                // mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 8:
                                SOURCE = SOURCE_ARRAY[1];
                                startActivity(new Intent(sell.this, ProfileActivity.class));
                                break;
                            case 4:
                                SOURCE = SOURCE_ARRAY[2];
                                startActivity(new Intent(sell.this, sell.class));

                                break;
                            case 6:
                                SOURCE = SOURCE_ARRAY[3];
                                startActivity(new Intent(sell.this, purchase.class));
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }
}