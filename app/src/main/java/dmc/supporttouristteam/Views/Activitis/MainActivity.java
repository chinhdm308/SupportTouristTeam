package dmc.supporttouristteam.Views.Activitis;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Views.Fragments.ChatsFragment;
import dmc.supporttouristteam.Views.Fragments.PeopleFragment;

public class MainActivity extends AppCompatActivity implements ChipNavigationBar.OnItemSelectedListener {

    ChipNavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mapping();
        navigationBar.setOnItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationBar.setItemSelected(R.id.menu_chats, true);
        }
    }

    private void mapping() {
        navigationBar = findViewById(R.id.navigation_bar);
    }

    @Override
    public void onItemSelected(int i) {
        if (i == R.id.menu_chats) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatsFragment()).commit();
        }
        if (i == R.id.menu_people) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PeopleFragment()).commit();
        }
    }
}