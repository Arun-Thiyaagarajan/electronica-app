package com.example.electronica.menu.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronica.R;
import com.example.electronica.SharedPref;
import com.example.electronica.login.LoginActivity;
import com.example.electronica.menu.home.home_detail.DetailActivity;

public class CartFragment extends Fragment {

    Button cartLoginBtn, applyBtn, placeOrderBtn;
    EditText couponIpBox;
    TextView locationOption, userNameImg, deliverToName;
    String username;
    RelativeLayout placeholderCart;
    NestedScrollView cartPageLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeholderCart = view.findViewById(R.id.placeholderCart);
        cartPageLayout = view.findViewById(R.id.cartPageLayout);

        locationOption = view.findViewById(R.id.locationOption);
        userNameImg = view.findViewById(R.id.userNameImg);
        deliverToName = view.findViewById(R.id.deliverToName);

        applyBtn = view.findViewById(R.id.applyBtn);
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn);

        couponIpBox = view.findViewById(R.id.couponIpBox);

        username = SharedPref.getName(requireContext());

        // Checks Authentication: if not logged in placeholder layout else cart layout
        if(!username.equals("")){
            cartPageLayout.setVisibility(View.VISIBLE);
            placeholderCart.setVisibility(View.GONE);
        } else{
            placeholderCart.setVisibility(View.VISIBLE);
            cartPageLayout.setVisibility(View.GONE);
        }

        if (!username.isEmpty()) {
            String[] names = username.split(" ");
            if (names.length > 0) {
                deliverToName.setText(names[0]);
            }
            userNameImg.setText(String.valueOf(username.charAt(0)));
        } else {
            // Handle the case where the username is empty
            userNameImg.setText("Buddy"); // Or set a default value
        }

        cartLoginBtn = view.findViewById(R.id.cartLoginBtn);
        cartLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                finishLoginActivity();
                requireActivity().finish();
            }
        });

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Cart Under Development", Toast.LENGTH_SHORT).show();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couponCode = couponIpBox.getText().toString();

                if(applyBtn.getText().toString().equals("Applied")) {
                    Toast.makeText(requireActivity(), "Coupon applied already", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (couponCode.isEmpty()) {
                        Toast.makeText(requireActivity(), "Coupon Code Empty", Toast.LENGTH_SHORT).show();
                    } else if (couponCode.length() < 6) {
                        Toast.makeText(requireActivity(), "Not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        applyBtn.setText("Applied");
                        hideKeyboardFrom(requireActivity(), v);
                        couponIpBox.clearFocus();
                        Toast.makeText(requireActivity(), "Applied Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        locationOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Will be implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void finishLoginActivity() {
        if (LoginActivity.instance != null) {
            LoginActivity.instance.finish();
        }
    }
}