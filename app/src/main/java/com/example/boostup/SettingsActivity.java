package com.example.boostup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    ListView lst,detailslist;
    String textFields[]={"Phone Number","Email"};
    String editFields[]={"0967648","fhdf@gkek.com"};

    String fields[]={"Logout","Change Password","Phone number","About","Email"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        lst= findViewById(R.id.listview);

        myAdapter adapter=new myAdapter(this,fields);
        lst.setAdapter(adapter);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Toast.makeText(SettingsActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                }
                if(position==1){
                    Toast.makeText(SettingsActivity.this, "Change Password", Toast.LENGTH_SHORT).show();
                }
                if(position==2){
                    Toast.makeText(SettingsActivity.this, "Phone Number", Toast.LENGTH_SHORT).show();
                }
                if(position==3){
                    Toast.makeText(SettingsActivity.this, "About", Toast.LENGTH_SHORT).show();
                }
                if(position==4){
                    Toast.makeText(SettingsActivity.this, "Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    class myAdapter extends ArrayAdapter<String>{
        Context context;
        String display[];
        myAdapter(Context c,String[]fields){
            super(c,R.layout.settingsview,R.id.item,fields);
            this.context=c;
            this.display=fields;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.settingsview,parent,false);
            TextView tv= view.findViewById(R.id.item);
            tv.setText(display[position]);
            return view;
        }
    }
}
