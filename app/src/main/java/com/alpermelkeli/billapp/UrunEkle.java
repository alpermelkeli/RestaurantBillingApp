package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UrunEkle extends AppCompatActivity {
    AppCompatEditText urunAdiInput;
    AppCompatEditText fiyatInput;
    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int REQUEST_CODE_GALLERY = 1002;
    ArrayList<String> optionsArr = new ArrayList<>();
    String selectedItem;
    ImageView urunEkleOnay;

    ImageView urunEkleFoto;
    ImageView imageView3;
    FirebaseStorage storage;
    StorageReference storageRef;
    String urunAdi;
    String fiyat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urun_ekle);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        urunEkleOnay = findViewById(R.id.urunEkleOnay);
        urunEkleFoto = findViewById(R.id.urunEkleFoto);
        urunAdiInput = findViewById(R.id.urunAdiInput);
        fiyatInput = findViewById(R.id.fiyatInput);
        imageView3 = findViewById(R.id.imageView3);
        optionsArr.add("Kategori Seç");
        optionsArr.add("Helva");
        optionsArr.add("Soğuk");
        optionsArr.add("Sıcak");
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Spinner mySpinner = findViewById(R.id.my_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UrunEkle.this, R.layout.my_spinner_options, optionsArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                urunAdi = urunAdiInput.getText().toString();
                fiyat = fiyatInput.getText().toString();
                if (selectedItem.equals("Soğuk")){
                    selectedItem= "soguk";
                } else if (selectedItem.equals("Sıcak")) {
                    selectedItem = "sicak";
                } else if (selectedItem.equals("Helva")) {
                    selectedItem = "helva";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        urunEkleOnay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Verileri Firebase Firestore'a kaydetme işlemi
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference urunRef = firestore.collection("Urunler").document(urunAdi);

                Map<String, Object> urunData = new HashMap<>();
                urunData.put("urunAdi", urunAdi);
                urunData.put("fiyat", fiyat);
                urunData.put("kategori",selectedItem);
                urunData.put("stokMiktari", "0");

                urunRef.set(urunData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Kaydetme başarılı olduğunda yapılacak işlemler
                                Toast.makeText(UrunEkle.this, "Ürün başarıyla eklendi", Toast.LENGTH_SHORT).show();
                                // Diğer işlemler veya sayfaya yönlendirme
                                Intent intent = new Intent(UrunEkle.this,Yonetici.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Kaydetme başarısız olduğunda yapılacak işlemler
                                Toast.makeText(UrunEkle.this, "Ürün eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        urunEkleFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urunAdi.equals("") || selectedItem.equals("Kategori Seç") || fiyat.equals("")){
                    Toast.makeText(UrunEkle.this,"ürün adı girin, fiyatı belirleyin ve kategori seçin",Toast.LENGTH_LONG).show();
                    return;
                }

                if (ContextCompat.checkSelfPermission(UrunEkle.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(UrunEkle.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(v, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(UrunEkle.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                            }
                        }).show();
                    } else {
                        ActivityCompat.requestPermissions(UrunEkle.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                    }
                } else {
                    openGallery();
                }
            }
        });




    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    // İzin sonuçlarını işlemek için metot
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildiyse, galeriyi aç
                openGallery();
            } else {
                // İzin verilmediyse, kullanıcıya bilgi ver veya işlem yap
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {

                Uri imageUri = data.getData();
                StorageReference imageRef = storageRef.child("/Products" + "/" + selectedItem + "/" + urunAdi +".jpg");

                imageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Image uploaded successfully
                                Toast.makeText(UrunEkle.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                urunEkleFoto.setImageURI(imageUri);
                                imageView3.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error uploading image
                                Toast.makeText(UrunEkle.this, "Failed to upload your image try again or check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UrunEkle.this,Yonetici.class);
        startActivity(intent);
    }
}