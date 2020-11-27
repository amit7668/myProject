package com.amit.heybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneLoginActivity extends AppCompatActivity
{

    private Button sendVerificationCodeBtn, verifyBtn;
    private EditText InputPhoneNumber,InputVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        sendVerificationCodeBtn = findViewById(R.id.send_verification_code_btn);
        verifyBtn = findViewById(R.id.verify_btn);

        InputPhoneNumber = findViewById(R.id.phone_number_input);
        InputVerificationCode = findViewById(R.id.verification_code_input);


        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                sendVerificationCodeBtn.setVisibility(View.INVISIBLE);


                verifyBtn.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);


            }
        });

    }
}