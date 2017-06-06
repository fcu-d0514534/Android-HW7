package com.example.user.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static final int LIST_PETS=1;
    private PetArrayAdapter adapter;

    public Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case LIST_PETS:
                    List<Pet> pets=(List<Pet>)msg.obj;
                    refreshPetList(pets);
                    break;
            }
        }
    };

    private void refreshPetList(List<Pet> pets) {
        adapter.clear();
        adapter.addAll(pets);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.lv);
        adapter = new PetArrayAdapter(this, new ArrayList<Pet>());
        listView.setAdapter(adapter);

        getPetsFromFirebase();
    }

    public void getPetsFromFirebase(){
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new FireBaseThread(dataSnapshot).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class FireBaseThread extends Thread {

        DataSnapshot dataSnapshot;

        public FireBaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot=dataSnapshot;
        }

        @Override
        public void run() {
            List<Pet> listPet=new ArrayList<>();
            for(DataSnapshot ds: dataSnapshot.getChildren()){
                DataSnapshot dsAdd=ds.child("Add");
                DataSnapshot dsName=ds.child("Name");
                DataSnapshot dsUrl=ds.child("Picture1");

                String name=(String) dsName.getValue();
                String add=(String) dsAdd.getValue();
                String Imgurl=(String) dsUrl.getValue();
                Bitmap bitmap=getImgBitmap(Imgurl);

                Log.v("TEST",name+" "+add+" "+ Imgurl);

                Pet pet=new Pet();
                pet.setShelter(name);
                pet.setKind(add);
                pet.setImgUrl(bitmap);
                listPet.add(pet);

                Message message=new Message();
                message.what=MainActivity.LIST_PETS;
                message.obj=listPet;
                handler.sendMessage(message);
            }
        }

        private Bitmap getImgBitmap(String Imgurl){
            try{
                URL url=new URL(Imgurl);
                Bitmap bm= BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bm;
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class PetArrayAdapter extends ArrayAdapter<Pet>{

        Context context;

        public PetArrayAdapter(Context context, List<Pet> items) {
            super(context,0, items);
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=LayoutInflater.from(context);
            LinearLayout itemlayout=null;
            if(convertView==null){
                itemlayout=(LinearLayout) inflater.inflate(R.layout.item_layout,null);
            }else{
                itemlayout=(LinearLayout)convertView;
            }

            Pet item=(Pet)getItem(position);
            TextView tvKind=(TextView)itemlayout.findViewById(R.id.tv_kind);
            TextView tvShelter=(TextView)itemlayout.findViewById(R.id.tv_shelter);
            ImageView ig=(ImageView)itemlayout.findViewById(R.id.imageView);

            tvKind.setText(item.getKind());
            tvShelter.setText(item.getShelter());
            ig.setImageBitmap(item.getImgUrl());

            return itemlayout;
        }
    }


}


