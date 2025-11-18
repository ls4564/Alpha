package com.example.alpha;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListViewActivity extends BaseActivity {
    EditText ed_Item;
    ListView lv_Items;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;

    private ValueEventListener itemListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setTitle("List View Demo");
        Weddings();

        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv_Items.setAdapter(adapter);

        // Create the listener object. It will be attached in onResume.
        createitemListener();
    }

    public void Weddings() {
        ed_Item = findViewById(R.id.ed_Item);
        lv_Items = findViewById(R.id.lv_Items);
    }

    private void createitemListener() {
        itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = snapshot.getValue(String.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListViewActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When the activity resumes, start listening for data changes.
        if (itemListener != null) {
            FBRef.refItems.addValueEventListener(itemListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // When the activity is paused, stop listening to save resources.
        if (itemListener != null) {
            FBRef.refItems.removeEventListener(itemListener);
        }
    }

    public void AddItem_Click(View view) {
        String item = ed_Item.getText().toString().trim();
        if (item.isEmpty()) {
            Toast.makeText(this, "Item cannot be empty.", Toast.LENGTH_SHORT).show();
        } else {
            // The listener will automatically update the UI.
            FBRef.refItems.push().setValue(item);
            ed_Item.setText("");
        }
    }

}
