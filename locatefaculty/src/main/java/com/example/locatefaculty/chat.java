package com.example.locatefaculty;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.TypedValue;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Detector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import javax.crypto.Cipher;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static android.provider.CalendarContract.Instances.BEGIN;
import static android.text.TextUtils.TruncateAt.END;

public class chat extends AppCompatActivity {
    //rsa part2
    private final static String RSA = "RSA";
    public static PublicKey uk;
    public final static String Public=//"-----BEGIN PUBLIC KEY-----\n" +
            "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgFYEFnb7Fz57gLjDEbnoKxQyLJte\n" +
            "9ttcweab+1GT1+AIc1vTvdPub3LV/iAqp5MhxXJt6Yb8Y/bq9p0zPZU2j/tJMo8v\n" +
            "9m6clsqRDwDh3NUxZvRuUlsoY85yUaBGCX+vX4/JjovTU9Wz2lwN935p0X0IGYv1\n" +
            "t04nTu3Nrc6cutthAgMBAAE=";
           // "-----END PUBLIC KEY-----";
    private final static String PRIVATE_KEY="-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIICWgIBAAKBgFYEFnb7Fz57gLjDEbnoKxQyLJte9ttcweab+1GT1+AIc1vTvdPu\n" +
            "b3LV/iAqp5MhxXJt6Yb8Y/bq9p0zPZU2j/tJMo8v9m6clsqRDwDh3NUxZvRuUlso\n" +
            "Y85yUaBGCX+vX4/JjovTU9Wz2lwN935p0X0IGYv1t04nTu3Nrc6cutthAgMBAAEC\n" +
            "gYACvOKSKu/Ud03jk6HuzusAG5C76UvmM4+gbXqUYCZQ5TP2ykiShFZby1/pWjI3\n" +
            "Rq3oNINoXoQ4CcaLvmPJahEvHvTxsmrqeCACjZfGdyAHbFnc6G5bVvYXHzk+8zIm\n" +
            "OfgsC3GPmocdwflQ5YMq1IXS0j8d8AY3r6cs32fdU2zE+QJBAJVwFzY/wnmuIqpP\n" +
            "Nm0KzIf/4eEBPOenQT18dspsLc2I9C+pEIcwmGUIoOehquCmcsJJ0H0IZ5Zu5CWg\n" +
            "TzHsPfMCQQCTWlHDo+jhEg2QjrHNHsqEbYuFTrEc+Kzz/WTTmDyDicgS8xkbskyj\n" +
            "HvCOJqie7r2vXFmk2CvMKtWM9pTbs1JbAkAP9rZ0FCGZUBHh8a5VvbaVvK8Lk09H\n" +
            "S+W7RMWH4ECQWVYElWHvnzBYcrCFrg6MCxvtR2dfQ/uU5Wm330U2hw8jAkAID3vD\n" +
            "iPbXQQaO7s9ZGiMX5Wopa6mSnSPQ4P6f+Ibu5x2Ts8qe0j0Q+0NLc1/r+cFUk2ev\n" +
            "9cdHjtxJrr9r2T5bAkBm+QSx2fmQTgpL/RCA5HenyL2zCP8ilRSVQjwsLaFO7RLz\n" +
            "TajUJR6c3bOKRfesowzPzr6/ckCAqAfqFcZYdKuW\n" +
            "-----END RSA PRIVATE KEY-----";
    public   static PrivateKey rk;
    //rsa part 2

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout chat;

    //Add Emojicon
    EmojiconEditText emojiconEditText;
    ImageView emojiButton,submitButton;
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
            Toast.makeText(chat.this,"You are out of chat room.", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            /*AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });*/
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
        //rsa part1
        try {
            StringBuilder pkcs1Lines = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new StringReader(PRIVATE_KEY));
            String line;
            while ((line = rdr.readLine()) != null) {
                pkcs1Lines.append(line);
            }
            String pkcs1Pem = pkcs1Lines.toString();
            pkcs1Pem = pkcs1Pem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
            pkcs1Pem = pkcs1Pem.replace("-----END RSA PRIVATE KEY-----", "");
            pkcs1Pem = pkcs1Pem.replaceAll("\\s+","");
            byte [] pkcs8EncodedBytes = Base64.decode(pkcs1Pem, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                try{
                    rk= kf.generatePrivate(keySpec);
                }
                catch (InvalidKeySpecException e){
                }
            }
            catch (NoSuchAlgorithmException e) {
                System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
            }
        } catch (Exception e1) {
// TODO Auto-generated catch block
            e1.printStackTrace();
        }
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(org.jose4j.base64url.Base64.decode(Public));
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            try{
               uk= keyFactory.generatePublic(pubKeySpec);
            }
            catch (InvalidKeySpecException e){
            }
        }
        catch (NoSuchAlgorithmException e) {
            System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
        }
        //rsa part1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat1);
        createToolbar();
        createDrawer(savedInstanceState, toolbar, montserrat_regular);
        chat = (RelativeLayout)findViewById(R.id.chat);
        emojiButton = (ImageView)findViewById(R.id.emoji_button);
        submitButton = (ImageView)findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText)findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(),chat,emojiButton,emojiconEditText);
       // emojIconActions.ShowEmojicon();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(emojiconEditText.getText().toString())){
                    Toast.makeText(chat.this,"message cant be empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    String s=encrypt(emojiconEditText.getText().toString());
                    String m=encrypt(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    FirebaseDatabase.getInstance().getReference().child("chat").push().setValue(new ChatMessage(s,m));
                    emojiconEditText.setText("");
                    emojiconEditText.requestFocus();

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
            Toast.makeText(chat.this, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
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
                                startActivity(new Intent(chat.this, chat.class));
                                //onLoadingSwipeRefreshLayout();
                                // mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 8:
                                SOURCE = SOURCE_ARRAY[1];
                                startActivity(new Intent(chat.this, ProfileActivity.class));
                                //onLoadingSwipeRefreshLayout();
                                //mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 4:
                                SOURCE = SOURCE_ARRAY[2];
                                startActivity(new Intent(chat.this, sell.class));
                                //onLoadingSwipeRefreshLayout();
                                //mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 6:
                                SOURCE = SOURCE_ARRAY[3];
                                startActivity(new Intent(chat.this, purchase.class));
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

        ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference().child("chat"))
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (EmojiconTextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                String k=model.getMessageText();
                String y=decrypt(k);
                String t=model.getMessageUser();
                String t1=decrypt(t);
                messageText.setText(y);
                messageUser.setText(t1);
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

            }
        };
        listOfMessage.setAdapter(adapter);
    }
    //rsa parT 3
    private static byte[] encrypt(String text, PublicKey pubRSA)
            throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);
        return cipher.doFinal(text.getBytes());
    }
    public final static String encrypt(String text) {
        try {
            return byte2hex(encrypt(text, uk));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public final static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static byte[] decrypt(byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, rk);
        return cipher.doFinal(src);
    }
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("hello");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}