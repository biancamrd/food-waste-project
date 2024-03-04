package com.example.licenta.activities;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Brand;
import com.example.licenta.classes.Ingredient;
import com.example.licenta.adapter.IngredientAdapter;
import com.example.licenta.classes.Product;
import com.example.licenta.classes.User;

import com.example.licenta.helpers.ExpirationComparator;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StorageActivity extends AppCompatActivity {
    public List<Ingredient> userIngredients;
    private RecyclerView ingredientRecyclerView;
    private IngredientAdapter ingredientAdapter;
    SearchView searchView;
    Integer userId;
    Button addIngredientButton;
    Button searchButton;
    Button scanButton;
    Button detectButton;
    ImageView previewImageView;
    ImageView sortImageView;
    List<String> labels;
    private Bitmap bitmap;
    private String[] classOrder;
    private int numClasses;
    String cameraPermission[];
    String storageWritePermission[];
    int selectedIngredientPosition = 0;
    Uri imageUri;
    private Set<Ingredient> allIngredients = new HashSet<>();
    private Brand currentBrand;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    Context context = StorageActivity.this;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> cropLauncher;
    private ActivityResultLauncher<Intent> detectLauncher;
    private AlertDialog productDialog;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 10001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        userIngredients = new ArrayList<>();
        searchView = findViewById(R.id.searchView);
        sortImageView = findViewById(R.id.sortImageView);
        detectButton = findViewById(R.id.detectIngredient);
        addIngredientButton = findViewById(R.id.addIngredient);
        scanButton = findViewById(R.id.scanIngredient);
        previewImageView = findViewById(R.id.previewImageView);
        ingredientRecyclerView = findViewById(R.id.ingredientRecyclerView);
        ingredientAdapter = new IngredientAdapter(StorageActivity.this, userIngredients);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchButton = findViewById(R.id.searchIngredient);

        cameraPermission = new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageWritePermission = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        labels = readLabelsFile();

        populateIngredientsList();

        setupSortClickListener();

        setupSearchButton();

        setupScanClickListener();

        setupItemTouchHelper();

        setupIngredientDeleteListener();

        setupGalleryLauncher();

        setupCameraLauncher();

        setupCropLauncher();

        setupDetectLauncher();

        setupAddIngredientClickListener();

        setupDetectClickListener();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    populateIngredientsList();
                } else {
                    List<Ingredient> filteredIngredients = filterIngredientsByText(newText);
                    ingredientAdapter.filterIngredients(filteredIngredients);
                }
                return true;
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (productDialog != null && productDialog.isShowing()) {
            productDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void setupScanClickListener() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageImportDialog();
            }
        });
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(view -> {
            List<Ingredient> selectedIngredients = ingredientAdapter.getSelectedIngredients();
            ArrayList<String> selectedIngredientNames = new ArrayList<>();
            for (Ingredient ingredient : selectedIngredients) {
                if (ingredient.isSelected()) {
                    selectedIngredientNames.add(ingredient.getName());
                }
            }
            if (selectedIngredientNames.isEmpty()) {
                Toast.makeText(this, "Selecting at least one ingredient is required.", Toast.LENGTH_LONG).show();
            } else {
                Intent searchResultsIntent = new Intent(StorageActivity.this, SearchResultsActivity.class);
                searchResultsIntent.putStringArrayListExtra("selectedIngredients", selectedIngredientNames);
                startActivity(searchResultsIntent);
            }
        });
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                selectedIngredientPosition = viewHolder.getAdapterPosition();
                showUpdateDialog();
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(ingredientRecyclerView);
    }

    private void setupSortClickListener() {
        sortImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(userIngredients, new ExpirationComparator());
                ingredientAdapter.notifyDataSetChanged();
            }
        });
    }


    private List<Ingredient> filterIngredientsByText(String searchText) {
        List<Ingredient> filteredList = new ArrayList<>();
        if (searchText.isEmpty()) {
            filteredList.addAll(allIngredients);
        } else {
            String lowercaseQuery = searchText.toLowerCase();
            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getName().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(ingredient);
                }
            }
        }
        return filteredList;
    }


    private void performSearch(String query) {
        List<Ingredient> filteredIngredients = filterIngredientsByText(query);
        ingredientAdapter.filterIngredients(filteredIngredients);
        userIngredients.clear();
        userIngredients.addAll(filteredIngredients);
    }

    private void setupDetectClickListener() {
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });
    }

    private void setupIngredientDeleteListener() {
        ingredientAdapter.setOnDeleteClickListener(new IngredientAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Ingredient ingredient = userIngredients.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(StorageActivity.this);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete this ingredient?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteIngredient(ingredient.getId());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        startCrop(imageUri);
                    }
                });
    }

    private void setupDetectLauncher() {
        detectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            performObjectDetection(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Image capture failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupCropLauncher() {
        cropLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                            handleCropResult(cropResult);
                        }
                    } else if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Intent data = result.getData();
                        if (data != null) {
                            CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                            Exception error = cropResult.getError();
                            Toast.makeText(this, "" + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleCropResult(CropImage.ActivityResult result) {
        Uri resultUri = result.getUri();
        previewImageView.setImageURI(resultUri);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) previewImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < items.size(); i++) {
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }

            String recognizedText = sb.toString().trim();
            fetchBrandByName(recognizedText);
        }
    }

    private void createBrand(String brandName) {
        String baseURL = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Brand brand = new Brand(brandName);

        Call<Void> call = apiService.createBrand(brand);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Brand added successfully", Toast.LENGTH_LONG).show();
                    fetchBrandByName(brand.getName());
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorMessage = response.errorBody().string();
                            Log.e("Creating brand error", errorMessage);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Error creating brand", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showAddBrandDialog(String unrecognizedBrand) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Brand Not Found");
        builder.setMessage("The brand '" + unrecognizedBrand + "' was not found. Do you want to add it?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAddBrandInputDialog();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showAddBrandInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Brand");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String brandName = input.getText().toString();
                createBrand(brandName);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void setupAddIngredientClickListener() {
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIngredientDialog();
            }
        });
    }



    private void createProduct(String productName, Brand brand) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("name", productName);
        requestBody.add("brand", JsonParser.parseString(new Gson().toJson(brand)));

        Call<JsonObject> call = apiService.createProduct(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse != null) {
                        Long productId = jsonResponse.get("id").getAsLong();
                        String productName = jsonResponse.get("name").getAsString();
                        JsonObject brandObject = jsonResponse.get("brand").getAsJsonObject();
                        Long brandId = brandObject.get("id").getAsLong();
                        Toast.makeText(StorageActivity.this, "Product added successfully", Toast.LENGTH_LONG).show();
                        fetchProductsByBrandId(currentBrand.getId());
                    }
                } else {
                    Log.e("Error creating product", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error creating product", t.getMessage());
            }
        });
    }


    private void fetchBrandByName(String recognizedText) {
        String baseURL = "http://" + IP_ADDRESS + ":8080/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<JsonObject> call = apiService.getBrandByName(recognizedText.toLowerCase());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    try {
                        Long brandId = jsonObject.get("id").getAsLong();
                        currentBrand = new Brand();
                        currentBrand.setId(brandId);
                        fetchProductsByBrandId(brandId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (response.code() == 404) {
                        showAddBrandDialog(recognizedText);
                    } else {
                        Log.e("Error fetching brand", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void fetchProductsByBrandId(Long brandId) {
        Log.e("DEBUG", "fetchProductsByBrandId called");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<JsonArray> call = apiService.getProductsByBrandId(brandId);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    Log.d("DEBUG", "API response received. Success: " + response.isSuccessful() + ", Status code: " + response.code());

                    List<Product> products = new ArrayList<>();
                    JsonArray jsonArray = response.body();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        try {
                            JsonObject productJson = jsonArray.get(i).getAsJsonObject();
                            Long id = productJson.get("id").getAsLong();
                            String name = productJson.get("name").getAsString();

                            Product product = new Product();
                            product.setId(id);
                            product.setName(name);

                            products.add(product);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    showProductAlertDialog(products);

                } else {
                    Log.e("Error fetching products", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Error fetching products", t.getMessage());
            }
        });
    }


    private void showProductAlertDialog(List<Product> products) {
        Log.d("DEBUG", "showProductAlertDialog called with " + products.size() + " products");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Product");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null);
        SearchView searchView = (SearchView) view.findViewById(R.id.search);
        ListView listView = view.findViewById(R.id.list);

        final ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(products);
        listView.setAdapter(arrayAdapter);

        if (products.isEmpty()) {
            builder.setMessage("No products available");
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        builder.setView(view);
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Add Product", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog.Builder builderInner = new AlertDialog.Builder(StorageActivity.this);
                builderInner.setTitle("Add a new product");

                final EditText input = new EditText(StorageActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builderInner.setView(input);

                builderInner.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String productName = input.getText().toString();
                        createProduct(productName, currentBrand);
                    }
                });
                builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builderInner.show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = arrayAdapter.getItem(position);
                handleSelectedProduct(selectedProduct);
            }
        });

        productDialog = builder.create();

        productDialog.show();
    }


    private void handleSelectedProduct(Product selectedProduct) {
        String productName = selectedProduct.getName();
        Ingredient ingredient = new Ingredient(productName);
        User user = new User();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);
        user.setId(userId);
        ingredient.setUser(user);
        addIngredientToServer(ingredient);
    }

    private void startCrop(Uri imageUri) {
        Intent cropIntent = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this);

        cropLauncher.launch(cropIntent);
    }

    private void pickGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageData = data.getData();
                            startCrop(imageData);
                        }
                    }
                });
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(cameraIntent);
    }

    private void deleteIngredient(Long ingredientId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<Void> call = apiService.deleteIngredient(ingredientId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StorageActivity.this, "Ingredient deleted successfully!", Toast.LENGTH_LONG).show();
                    deleteIngredientFromList(ingredientId);
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Error deleting ingredient", t.getMessage());
            }
        });
    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storageWritePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void populateIngredientsList() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        String url = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService userService = retrofit.create(MyApiService.class);
        Call<List<Ingredient>> call = userService.getUserIngredients(userId);

        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                if (response.isSuccessful()) {
                    List<Ingredient> ingredientList = response.body();
                    if (ingredientList != null) {
                        try {
                            allIngredients.clear();
                            userIngredients.clear();
                            for (Ingredient ingredient : ingredientList) {
                                User user = new User();
                                user.setId(userId);
                                ingredient.setUser(user);
                                userIngredients.add(ingredient);
                            }

                            allIngredients.clear();
                            allIngredients.addAll(userIngredients);
                            ingredientAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(StorageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(StorageActivity.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                Toast.makeText(StorageActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        detectLauncher.launch(cameraIntent);
    }


    private void deleteIngredientFromList(Long ingredientId) {
        Log.d("delete", "ingredientId = " + ingredientId);
        for (int i = 0; i < userIngredients.size(); i++) {
            if (userIngredients.get(i).getId() == ingredientId) {
                userIngredients.remove(i);
                ingredientAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showAddIngredientDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_ingredient, null);

        EditText ingredientNameEditText = dialogView.findViewById(R.id.ingredientName);
        EditText ingredientQuantityEditText = dialogView.findViewById(R.id.ingredientQuantity);
        EditText ingredientUnitEditText = dialogView.findViewById(R.id.ingredientUnit);
        EditText ingredientExpirationDateEditText = dialogView.findViewById(R.id.ingredientExpirationDate);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Ingredient");
        builder.setView(dialogView);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = ingredientNameEditText.getText().toString();
                int quantity = Integer.parseInt(ingredientQuantityEditText.getText().toString());
                String unit = ingredientUnitEditText.getText().toString();
                Date expirationDate = null;
                try {
                    expirationDate = format.parse(ingredientExpirationDateEditText.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                User user = new User();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userId", 0);
                user.setId(userId);

                Ingredient ingredient = new Ingredient();
                ingredient.setUser(user);
                ingredient.setName(name);
                ingredient.setQuantity(quantity);
                ingredient.setUnit(unit);
                ingredient.setExpirationDate(expirationDate);

                addIngredientToServer(ingredient);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void addIngredientToServer(Ingredient ingredient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        JsonObject ingredientJson = new JsonObject();
        JsonObject userJson = new JsonObject();

        try {
            userJson.addProperty("id", ingredient.getUser().getId());
            ingredientJson.add("user", userJson);
            ingredientJson.addProperty("name", ingredient.getName());
            ingredientJson.addProperty("quantity", ingredient.getQuantity());
            ingredientJson.addProperty("unit", ingredient.getUnit());
            if (ingredient.getExpirationDate() == null) {
                ingredientJson.add("expirationDate", JsonNull.INSTANCE);
            } else {
                ingredientJson.addProperty("expirationDate", formatter.format(ingredient.getExpirationDate()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.addIngredient(ingredientJson);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StorageActivity.this, "Ingredient added successfully!", Toast.LENGTH_LONG).show();
                    userIngredients.add(ingredient);

                    ingredientAdapter.notifyDataSetChanged();
                    Activity activity = (Activity) context;
                    activity.recreate();
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String json = gson.toJson(new ArrayList<>(userIngredients));
                    editor.putString("USER_INGREDIENTS", json);
                    editor.apply();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error adding ingredient", t.getMessage());
            }
        });
    }

    private void updateIngredientOnServer(Ingredient ingredient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        JSONObject ingredientJson = new JSONObject();
        JSONObject userJson = new JSONObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            userJson.put("id", ingredient.getUser().getId());
            ingredientJson.put("user", userJson);
            ingredientJson.put("name", ingredient.getName());
            ingredientJson.put("quantity", ingredient.getQuantity());
            ingredientJson.put("unit", ingredient.getUnit());
            if (ingredient.getExpirationDate() == null) {
                ingredientJson.put("expirationDate", null);
            } else {
                ingredientJson.put("expirationDate", formatter.format(ingredient.getExpirationDate()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), ingredientJson.toString());

        Call<JsonObject> call = apiService.updateIngredient(ingredient.getId(), requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StorageActivity.this, "Ingredient updated successfully!", Toast.LENGTH_LONG).show();

                    ingredientAdapter.notifyDataSetChanged();
                } else {
                    handleErrorResponse(response.errorBody());
                    Log.e("update", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("update", t.getMessage());
            }
        });
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StorageActivity.this);
        builder.setTitle("Update Ingredient");

        View viewInflated = LayoutInflater.from(StorageActivity.this).inflate(R.layout.dialog_update_ingredient, null);

        final EditText ingredientNameEditText = viewInflated.findViewById(R.id.updateName);
        final EditText ingredientQuantityEditText = viewInflated.findViewById(R.id.updateQuantity);
        final EditText ingredientUnitEditText = viewInflated.findViewById(R.id.updateUnit);
        final EditText ingredientExpirationDateEditText = viewInflated.findViewById(R.id.updateExpirationDate);

        builder.setView(viewInflated);
        Ingredient selectedIngredient = userIngredients.get(selectedIngredientPosition);
        ingredientNameEditText.setText(selectedIngredient.getName());
        ingredientUnitEditText.setText(selectedIngredient.getUnit());
        ingredientQuantityEditText.setText(String.valueOf((int) selectedIngredient.getQuantity()));
        Date expirationDate = selectedIngredient.getExpirationDate();
        if (expirationDate != null) {
            String formattedDate = format.format(expirationDate);
            ingredientExpirationDateEditText.setText(formattedDate);
        } else {
            ingredientExpirationDateEditText.setText("");
        }

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIngredient.setName(ingredientNameEditText.getText().toString());
                selectedIngredient.setUnit(ingredientUnitEditText.getText().toString());
                selectedIngredient.setQuantity(Integer.parseInt(ingredientQuantityEditText.getText().toString()));

                if (!ingredientExpirationDateEditText.getText().toString().isEmpty()) {
                    Date expirationDate = null;
                    try {
                        expirationDate = format.parse(ingredientExpirationDateEditText.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    selectedIngredient.setExpirationDate(expirationDate);
                } else {
                    selectedIngredient.setExpirationDate(null);
                }
                updateIngredientOnServer(selectedIngredient);
                ingredientAdapter.notifyDataSetChanged();
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredientAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void handleErrorResponse(ResponseBody errorBody) {
        try {
            if (errorBody != null) {
                String errorString = errorBody.string();
                JSONObject errorJson = new JSONObject(errorString);
                String errorMessage = errorJson.getString("message");
                int errorCode = errorJson.getInt("code");
                Log.e("StorageActivity", "Error with code " + errorCode + ": " + errorMessage);
                Toast.makeText(StorageActivity.this, "Error with code " + errorCode + ": " + errorMessage, Toast.LENGTH_LONG).show();
            } else {
                Log.e("StorageActivity", "Error body is null");
                Toast.makeText(StorageActivity.this, "Error body is null", Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> readLabelsFile() {
        List<String> labels = new ArrayList<>();
        try {
            InputStream labelsInput = getAssets().open("class_order.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(labelsInput));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return labels;
    }

    private Interpreter getInterpreter() throws IOException {
        MappedByteBuffer modelBuffer = (MappedByteBuffer) loadModelFile();

        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);

        Interpreter interpreter = new Interpreter(modelBuffer, options);

        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("class_order.txt");
        classOrder = readClassOrder(inputStream);

        numClasses = interpreter.getOutputTensor(0).shape()[1];
        if (classOrder.length != numClasses) {
            throw new IllegalArgumentException("The number of classes in the class_order.txt file does not match the number of classes in the model.");
        }

        return interpreter;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("fruit_vegetable_model.tflite");
        File tempFile = File.createTempFile("temp_model", null);
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4 * 1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        fileOutputStream.close();
        inputStream.close();
        RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "r");
        MappedByteBuffer modelBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
        randomAccessFile.close();
        return modelBuffer;
    }

    private String[] readClassOrder(InputStream inputStream) throws IOException {
        List<String> classOrderList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classOrderList.add(line.trim());
            }
        }
        return classOrderList.toArray(new String[0]);
    }

    private void performObjectDetection(Bitmap bitmap) {
        try {
            Interpreter interpreter = getInterpreter();

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            ByteBuffer inputBuffer = preprocessImage(resizedBitmap);

            float[][] outputArray = new float[1][numClasses];
            interpreter.run(inputBuffer, outputArray);

            int maxIndex = getMaxIndex(outputArray[0]);
            String predictedClass = classOrder[maxIndex];
            Log.e("maxindex", predictedClass);

            AlertDialog.Builder builder = new AlertDialog.Builder(StorageActivity.this);
            builder.setTitle("Detected Class");
            builder.setMessage(predictedClass);
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Incorrect", null);
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(view -> {
                    Ingredient ingredient = new Ingredient(predictedClass);
                    User user = new User();
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("userId", 0);
                    user.setId(userId);
                    ingredient.setUser(user);
                    addIngredientToServer(ingredient);

                    dialog.dismiss();
                });
            });

            dialog.show();

            interpreter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("detect", e.toString());
        }
    }

    private ByteBuffer preprocessImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int channels = 3;

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(width * height * channels * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int pixelValue : pixels) {
            float r = ((pixelValue >> 16) & 0xFF) / 255.0f;
            float g = ((pixelValue >> 8) & 0xFF) / 255.0f;
            float b = (pixelValue & 0xFF) / 255.0f;

            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        inputBuffer.rewind();
        return inputBuffer;
    }

    private int getMaxIndex(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }
}
