package com.example.mytask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDataBase extends AppCompatActivity {

    @BindView(R.id.list)
    ListView ListView;

    UserDataBaseManager userDataBaseManager;
    ArrayList<String> arrayList ;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data_base);
        ButterKnife.bind(this);
        registerForContextMenu(ListView);
        userDataBaseManager=new UserDataBaseManager(this);
        try {
            arrayList=new ArrayList<>();
            Cursor c=userDataBaseManager.allData();
            while (c.moveToNext()) {
                arrayList.add("Name  = "+c.getString(0) + "\nSecret = " + c.getString(1));
            }
            arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,arrayList);
            ListView.setAdapter(arrayAdapter);
        }catch (Exception e){
            Toast.makeText(this, "Exception\nNo Data To Show\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.list_view_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()){
                case R.id.delete:
                    String selectedFromList =(ListView.getItemAtPosition(info.position).toString());
                    arrayList.remove(info.position);
                    arrayAdapter.notifyDataSetChanged();
                    arrayAdapter.getItemId(info.position);
                    userDataBaseManager.deleteByName(selectedFromList);
                    return true;
                    default:
                        return super.onContextItemSelected(item);
            }
        }catch (Exception e){
            Toast.makeText(UserDataBase.this , "Exception "+e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }


        return super.onContextItemSelected(item);
    }
}
