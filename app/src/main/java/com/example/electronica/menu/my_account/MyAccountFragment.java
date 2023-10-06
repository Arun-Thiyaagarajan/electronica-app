package com.example.electronica.menu.my_account;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.electronica.R;
import com.example.electronica.SharedPref;
import com.example.electronica.login.LoginActivity;

import java.util.Objects;

public class MyAccountFragment extends Fragment {

    Button myAccountLoginBtn;
    String username, userEmail;
    TextView profileImg, profileName, profileEmail;
    RelativeLayout placeholderMyAccount, myAccountLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeholderMyAccount = view.findViewById(R.id.placeholderMyAccount);
        myAccountLayout = view.findViewById(R.id.myAccountLayout);

        profileImg = view.findViewById(R.id.profileImg);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);

        username = SharedPref.getName(requireContext());
        userEmail = SharedPref.getEmail(requireContext());

        // Checks Authentication: if not logged in placeholder layout else cart layout
        if(!username.equals("")){
            myAccountLayout.setVisibility(View.VISIBLE);
            placeholderMyAccount.setVisibility(View.GONE);
        } else{
            placeholderMyAccount.setVisibility(View.VISIBLE);
            myAccountLayout.setVisibility(View.GONE);
        }

        if (!username.isEmpty()) {
            profileImg.setText(String.valueOf(username.charAt(0)));
            profileName.setText(username);
            profileEmail.setText(userEmail);
        } else {
            profileImg.setText("");
            profileName.setText("");
            profileEmail.setText("");
        }

        myAccountLoginBtn = view.findViewById(R.id.myAccountLoginBtn);
        myAccountLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                // Finishing Main Activity
                finishLoginActivity();
                requireActivity().finish();
            }
        });
    }

    private void finishLoginActivity() {
        if (LoginActivity.instance != null) {
            LoginActivity.instance.finish();
        }
    }
}