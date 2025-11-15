package com.example.alpha;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.menuAuth) {
            intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuListView) {
            intent = new Intent(this, ListViewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuGallery) {
            intent = new Intent(this, Gallery.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuCamera) {
            intent = new Intent(this, Camera.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
