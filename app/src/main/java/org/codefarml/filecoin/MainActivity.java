package org.codefarml.filecoin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private String prikey = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UnsignedMessageAPI unsignedMessageAPI = Sign.createUnsignedMessageAPI();
        if(prikey != null){
            Sign.transaction_sign_raw(unsignedMessageAPI,prikey);
        }
    }
}