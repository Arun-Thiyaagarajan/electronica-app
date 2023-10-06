package com.example.electronica.menu.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.electronica.MainActivity;
import com.example.electronica.R;
import com.example.electronica.SharedPref;
import com.example.electronica.menu.cart.CartFragment;
import com.example.electronica.menu.home.adapter.CategoryAdapter;
import com.example.electronica.menu.home.adapter.HotSalesAdapter;
import com.example.electronica.menu.home.adapter.PopularProductsAdapter;
import com.example.electronica.menu.home.model.CategoryModel;
import com.example.electronica.menu.home.model.HomeModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView userNameTitle;
    String name;
    private SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView nestedScrollView;
    private RecyclerView hotSalesRecyclerView, popularProductsRecyclerView, categoryRecyclerView;
    private HotSalesAdapter hotSalesAdapter;
    private CategoryAdapter categoryAdapter;
    private PopularProductsAdapter popularProductsAdapter;
    private List<HomeModel> hotSalesList, popularProductsList;
    private List<CategoryModel> categoryList;
    AlertDialog dialog;
    FirebaseFirestore firestore;
    FloatingActionButton cartFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear existing data and load new data from Firebase
                refreshData();
            }
        });

        userNameTitle = view.findViewById(R.id.userNameTitle);
        name = SharedPref.getName(requireContext());

        if (!name.equals("")) {
            String[] names = name.split(" ");
            if (names.length > 0) {
                userNameTitle.setText(names[0]);
            }
        } else {
            userNameTitle.setText("Buddy");
        }

        MainActivity mainActivity = (MainActivity) requireActivity();

        hotSalesRecyclerView = view.findViewById(R.id.hotSalesRecyclerView);
        popularProductsRecyclerView = view.findViewById(R.id.popProductsRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        LinearLayoutManager hotLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        hotSalesRecyclerView.setLayoutManager(hotLayoutManager);
        popularProductsRecyclerView.setLayoutManager(popularLayoutManager);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Find and set click listener for FloatingActionButton
        cartFab = view.findViewById(R.id.cartFab);
        cartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Cart Under Development", Toast.LENGTH_SHORT).show();
                replaceFragment(new CartFragment());
                mainActivity.updateBottomNavigationBar(R.id.cart);
                mainActivity.updateNavigationView(R.id.cart);
                // Scroll the NestedScrollView to the top
               //nestedScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        firestore = FirebaseFirestore.getInstance();

        setCategoryAdapter();
        setHotSalesAdapter();
        setPopularProductsAdapter();
        loadData();
    }

    private void setCategoryAdapter() {
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setHotSalesAdapter() {
        hotSalesList = new ArrayList<>();
        hotSalesAdapter = new HotSalesAdapter(requireContext(), hotSalesList);
        hotSalesRecyclerView.setAdapter(hotSalesAdapter);
    }

    private void setPopularProductsAdapter() {
        popularProductsList = new ArrayList<>();
        popularProductsAdapter = new PopularProductsAdapter(requireContext(), popularProductsList);
        popularProductsRecyclerView.setAdapter(popularProductsAdapter);
    }

    private void refreshData() {
        // Clear existing data
        categoryList.clear();
        hotSalesList.clear();
        popularProductsList.clear();

        // Load new data from Firebase
        loadData();
    }

    private void loadData() {

        categoryList.add(new CategoryModel("Mobiles", R.drawable.iphone_14));
        categoryList.add(new CategoryModel("Laptops", R.drawable.macbook));
        categoryList.add(new CategoryModel("Earbuds", R.drawable.earbuds));
        categoryList.add(new CategoryModel("Headphones", R.drawable.sony_headphones));
        categoryList.add(new CategoryModel("TVs", R.drawable.samsung_tv4));

        firestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            hotSalesList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                HomeModel homeModel = document.toObject(HomeModel.class);

                                hotSalesList.add(homeModel);
                            }
                            hotSalesAdapter.notifyItemRangeChanged(0,hotSalesList.size());
                            dialog.dismiss();
                        }
                    }
                });

        firestore.collection("popular_products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            popularProductsList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                HomeModel homeModel = document.toObject(HomeModel.class);

                                popularProductsList.add(homeModel);
                            }
                            popularProductsAdapter.notifyItemRangeChanged(0,popularProductsList.size());
                            dialog.dismiss();
                        }
                    }
                });

        // After loading data, dismiss the refresh indicator
        swipeRefreshLayout.setRefreshing(false);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}
