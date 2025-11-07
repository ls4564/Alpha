package com.example.alpha;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A base activity for activities that share a common options menu.
 * It handles the creation and selection of menu items.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle menu item clicks
        if (id == R.id.menuAuth) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuListView) {
            Intent intent = new Intent(this, ListViewActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
