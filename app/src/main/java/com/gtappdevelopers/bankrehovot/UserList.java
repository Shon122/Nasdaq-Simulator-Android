package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserList extends AppCompatActivity {
    private ArrayList<User> saveUserList;
    private ArrayList<User> userList;
    private UserAdapter adapter;
    private ListView listView;
    private SearchView searchView;
    int balanceSort = 0; // 0 = not sorted this way ,1= sorted this way
    int nameSort = 0; // 0 = not sorted this way ,1= sorted this way
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist);
        //initialize the arraylist and adapter
        userList = MainActivity.users;
        saveUserList=userList;
        adapter = new UserAdapter(this, userList);

        //initialize the ListView and set the adapter
        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //set onItemClickListener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.viewingUser = userList.get(position);
                Intent intent = new Intent(UserList.this, MyProfile.class);
                startActivity(intent);
            }
        });

        //initialize the SearchView and set the listener
        searchView = findViewById(R.id.search_view_users);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userList=saveUserList;
                adapter.notifyDataSetChanged();
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userList=saveUserList;
                adapter.notifyDataSetChanged();
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        sortByName(null);
    }

    //function to sort the list by name
    public void sortByName(View view) {
        balanceSort = 0;
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.username.compareTo(o2.username);
            }
        });

        if (nameSort != 0 && nameSort % 2 != 0) {
            Collections.reverse(userList);
        }
        nameSort++;
        adapter.notifyDataSetChanged();
    }

    //function to sort the list by gain/loss percentage
    public void sortByBalance(View view) {
        nameSort = 0;
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Double.compare(o1.balance, o2.balance);
            }
        });

        if (balanceSort != 0 && balanceSort % 2 != 0) {
            Collections.reverse(userList);
        }
        balanceSort++;
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                sortByName(null);
                return true;
            case R.id.sort_by_balance:
                sortByBalance(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

