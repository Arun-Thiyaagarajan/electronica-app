package com.example.electronica.menu.home.home_detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.TransitionManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.electronica.R;
import com.example.electronica.menu.home.model.HomeModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    int quantity = 1;
    Boolean favBtnBool = false;
    ImageView productImg;
    TextView productName, productPrice, productDesc, readMoreInfo;
    LinearLayout parentLayout;
    private List<HomeModel> homeModelList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button continueBtn = findViewById(R.id.continueBtn);
        Button applyBtn = findViewById(R.id.applyBtn);

        EditText couponIpBox = findViewById(R.id.couponIpBox);

        ImageView popMenu = findViewById(R.id.popMenu);
        ImageView favBtn = findViewById(R.id.favBtn);
        ImageView minusBtn = findViewById(R.id.minusBtn);
        ImageView plusBtn = findViewById(R.id.plusBtn);
        ImageView backBtn = findViewById(R.id.backBtn);
        productImg = findViewById(R.id.productImg);
        registerForContextMenu(productImg);

        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDesc = findViewById(R.id.productDesc);
        readMoreInfo = findViewById(R.id.readMoreInfo);

        TextView qtyTxt = findViewById(R.id.qtyTxt);
        qtyTxt.setText(String.valueOf(quantity));

        // Back button Logic
        backBtn.setOnClickListener(view -> onBackPressed());

        popMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        parentLayout = findViewById(R.id.productInfoContainer);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productDesc.setText(bundle.getString("Description"));
            productName.setText(bundle.getString("Name"));
            productPrice.setText("₹ " + bundle.getString("Price"));
            Glide.with(this).load(bundle.getString("Image")).into(productImg);
        }

        readMoreInfo.setVisibility(View.GONE);
        readMoreInfo.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;

            @Override
            public void onClick(View v) {

                isExpanded = !isExpanded;

                if (isExpanded) {
                    productDesc.setMaxLines(Integer.MAX_VALUE);
                    readMoreInfo.setText("Show Less");
                } else {
                    productDesc.setMaxLines(3);
                    readMoreInfo.setText("Read More");
                }
                TransitionManager.beginDelayedTransition(parentLayout);
            }
        });

        // Check if the description contains more than 30 words
        String descriptionText = productDesc.getText().toString().trim();
        String[] words = descriptionText.split("\\s+");
        if (words.length > 30) {
            readMoreInfo.setVisibility(View.VISIBLE); // Show "Read More" button
        }

        // Favourite Button Logic
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favBtnBool) {
                    Toast.makeText(DetailActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                    favBtn.setImageResource(R.drawable.heart_checked);
                    favBtnBool = true;
                }
                else {
                    Toast.makeText(DetailActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                    favBtn.setImageResource(R.drawable.heart);
                    favBtnBool = false;
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    --quantity;
                    qtyTxt.setText(String.valueOf(quantity));
                }
                else {
                    Toast.makeText(DetailActivity.this, "Minimum Quantity Required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity < 10) {
                    ++quantity;
                    qtyTxt.setText(String.valueOf(quantity));
                }
                else {
                    Toast.makeText(DetailActivity.this, "Max Quantity Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couponCode = couponIpBox.getText().toString();

                if(applyBtn.getText().toString().equals("Applied")) {
                    Toast.makeText(DetailActivity.this, "Coupon applied already", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (couponCode.isEmpty()) {
                        Toast.makeText(DetailActivity.this, "Coupon Code Empty", Toast.LENGTH_SHORT).show();
                    } else if (couponCode.length() < 6) {
                        Toast.makeText(DetailActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        applyBtn.setText("Applied");
                        hideKeyboardFrom(DetailActivity.this, v);
                        couponIpBox.clearFocus();
                        Toast.makeText(DetailActivity.this, "Applied Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Hide Keyboard on Focus = false
    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.pop_up_menu); // Inflate your menu resource
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.shareOption) {
                    shareProductDetails();
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.saveOption){
            saveImageToGallery(productImg);
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void shareProductDetails() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "Check out this product:\n\n" +
                "Name: " + productName.getText().toString() + "\n\n" +
                "Price: " + productPrice.getText().toString() + "\n\n" +
                "Description:\n" + productDesc.getText().toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Product Details"));
    }

    private void saveImageToGallery(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Media scanner to refresh gallery
            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.getAbsolutePath()}, null,
                    (path, uri) -> {
                        // Image saved
                        Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

}