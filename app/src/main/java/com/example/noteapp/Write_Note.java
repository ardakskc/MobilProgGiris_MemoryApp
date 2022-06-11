package com.example.noteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Write_Note extends AppCompatActivity {
    protected Spinner spinner;
    protected ImageButton back,save,pdf;
    protected Button loc_but;
    protected TextView txt_date,loc_text;
    protected EditText title,entry;
    protected Database db;
    protected ImageView img;
    protected Bitmap mem_img;
    protected String img_path;
    protected LatLng result=null;
    protected int reqcod=1;
    protected int LOC_ACTIVITY=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);
        db = new Database(this);
        mem_img=null;
        img_path="NULL";
        txt_date = (TextView) findViewById(R.id.txt_date);
        loc_text = (TextView) findViewById(R.id.loc_text);
        back = (ImageButton) findViewById(R.id.back_but);
        save = (ImageButton) findViewById(R.id.save_but);
        title = (EditText) findViewById(R.id.editText);
        entry = (EditText) findViewById(R.id.editTextTextMultiLine);
        img = (ImageView) findViewById(R.id.photo);
        loc_but = (Button) findViewById(R.id.loc_but);
        pdf = (ImageButton) findViewById(R.id.pdf_but);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 22);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 22);
        }

        img.setImageResource(R.drawable.ic_baseline_insert_photo_24);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Moods, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(getIntent().hasExtra("Not")){
            Notes temp = (Notes) getIntent().getSerializableExtra("Not");
            spinner.setSelection(temp.getMood());
            title.setText(temp.getBaslik());
            entry.setText(temp.getText());
            txt_date.setText(temp.getDate());
            loc_text.setText(temp.getLokasyon());
            if (!temp.pht_isNull()){

                Bitmap bmImage = BitmapFactory.decodeFile(temp.getFoto());
                Bitmap rounded=Bitmap.createBitmap(bmImage.getWidth(), bmImage.getHeight(), bmImage.getConfig());
                Canvas canvas=new Canvas(rounded);
                Paint mpaint=new Paint();
                mpaint.setAntiAlias(true);
                mpaint.setShader(new BitmapShader(bmImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawRoundRect((new RectF(0, 0, bmImage.getWidth(), bmImage.getHeight())), 100, 100, mpaint); // Round Image Corner 100 100 100 100
                img.setImageBitmap(rounded);
            }
        }else{
            loc_text.setText("Not Set.");
            Date d = Calendar.getInstance().getTime();
            txt_date.setText(new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault()).format(d));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tit = title.getText().toString();
                String ent = entry.getText().toString();
                String lok = "Istanbul";
                if (result!=null){
                    lok=latToAdress(result);
                }
                Date d = Calendar.getInstance().getTime();
                String date = new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault()).format(d);
                int mood = spinner.getSelectedItemPosition();
                if(getIntent().hasExtra("Not")){
                    Notes temp = (Notes) getIntent().getSerializableExtra("Not");
                    if (mem_img!=null){

                        Notes note = new Notes(temp.getId(),tit,date,ent,mood,lok,temp.getSifre(),img_path);
                        db.updateNote(note);
                    }
                    else{
                        Notes note = new Notes(temp.getId(),tit,date,ent,mood,lok,temp.getSifre(),temp.getFoto());
                        db.updateNote(note);
                    }
                    Toast.makeText(getApplicationContext(), "Memory Updated.", Toast.LENGTH_SHORT).show();
                }else{
                    if (mem_img!=null){
                        Notes note = new Notes(tit,date,ent,mood,lok,img_path);
                        db.addNote(note);
                    }else{
                        Notes note = new Notes(tit,date,ent,mood,lok);
                        db.addNote(note);
                    }
                    Toast.makeText(getApplicationContext(), "Memory Created.", Toast.LENGTH_SHORT).show();
                }
                finish();


            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i,reqcod);

            }
        });
        loc_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivityForResult(intent,LOC_ACTIVITY);

            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                try {
                    loadpdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==reqcod){
            if(data!=null){
                Uri image = data.getData();
                File file = new File(getRealPathFromURI(image));
                img_path = file.getAbsolutePath();
                Toast.makeText(getApplicationContext(), "File Choosed.", Toast.LENGTH_SHORT).show();
                try {
                    Bitmap bmImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                    mem_img=bmImage;
                    //Alinan goruntunun ekranda estetik durmasi icin kenarlarını yuvarladım.
                    Bitmap rounded=Bitmap.createBitmap(bmImage.getWidth(), bmImage.getHeight(), bmImage.getConfig());
                    Canvas canvas=new Canvas(rounded);
                    Paint mpaint=new Paint();
                    mpaint.setAntiAlias(true);
                    mpaint.setShader(new BitmapShader(bmImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                    canvas.drawRoundRect((new RectF(0, 0, bmImage.getWidth(), bmImage.getHeight())), 100, 100, mpaint); // Round Image Corner 100 100 100 100
                    img.setImageBitmap(rounded);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return;
        }
        else if(requestCode == LOC_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                result = (LatLng) data.getParcelableExtra("result");
                loc_text.setText(latToAdress(result));
            }
            return;
        }
        else {
           return;
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private String latToAdress(LatLng latLng){
        String sonuc="";
        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        try{
            String[] parts = address.split(",");
            String part1 = parts[0];
            String part2 = parts[2];

            String[] parts2 = part2.split(" ");
            sonuc = parts2[2];
        }catch(Exception E){
            sonuc = country;
        }

        return sonuc;
    }
    public void write_perm(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
        }
    }
    public void loadpdf() throws FileNotFoundException{
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        write_perm();
        File f = new File(path,title.getText()+".pdf");

        OutputStream output = new FileOutputStream(f);
        PdfWriter writer = new PdfWriter(f);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Paragraph p = new Paragraph("Title: "+title.getText().toString()+"\n");
        Paragraph p2 = new Paragraph("Date: "+txt_date.getText().toString()+"\n");
        Paragraph p3 = new Paragraph("Location: "+loc_text.getText().toString()+"\n");
        Paragraph p4 = new Paragraph("Entry: "+entry.getText().toString()+"\n");

        document.add(p);
        document.add(p2);
        document.add(p3);
        document.add(p4);
        document.close();
        Toast.makeText(getApplicationContext(), "PDF Created.", Toast.LENGTH_SHORT).show();
    }
}