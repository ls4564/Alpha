package com.example.alpha;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    EditText ed_Item;
    ListView lv_Items;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Weddings();

        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv_Items.setAdapter(adapter);
    }

    public void Weddings()
    {
        ed_Item = findViewById(R.id.ed_Item);
        lv_Items = findViewById(R.id.lv_Items);
    }

    public void AddItem_Click(View view) {
        String item = ed_Item.getText().toString();
        if(item.isEmpty())
        {
            Toast.makeText(this, "Item cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Add to Firebase
            FBRef.refStudents.push().setValue(item);


            items.add(item);
            adapter.notifyDataSetChanged();
            ed_Item.setText("");
        }
    }
}
