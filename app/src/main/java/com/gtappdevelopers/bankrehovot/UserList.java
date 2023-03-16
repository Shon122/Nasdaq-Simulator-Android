package com.gtappdevelopers.bankrehovot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

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
        MainActivity.backToUsers=1;
        //initialize the arraylist and adapter

        userList = MainActivity.users;
        int size = userList.size();
        for (int i = 0; i < size - 1; i++) {
            User user = userList.get(i);
            for (int j = i + 1; j < size; j++) {
                if (user.equals(userList.get(j))) {
                    userList.remove(j);
                    size--;
                    j--;
                }
            }
        }
        MainActivity.users=userList;
        saveUserList = userList;
        adapter = new UserAdapter(this, userList);

        //initialize the ListView and set the adapter
        listView = findViewById(R.id.list_view_users);
        listView.setAdapter(adapter);

        //set onItemClickListener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.viewingUser = userList.get(position);
                Intent intent = new Intent(UserList.this, MyProfile.class);


                startActivity(intent);
                finish();
            }
        });

        searchView = findViewById(R.id.search_view_users);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        sortByName(null);
    }


    public void filter(String text) {
        ArrayList<User> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList = saveUserList;
        } else {
            for (User user : saveUserList) {
                if (user.username.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        userList = filteredList;
        adapter = new UserAdapter(this, userList);
        listView.setAdapter(adapter);
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
        adapter = new UserAdapter(this, userList);
        listView.setAdapter(adapter);
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
        adapter = new UserAdapter(this, userList);
        listView.setAdapter(adapter);
    }





    @Override //when user wants to go back
    public void onBackPressed() {
        // Handle back button press event here
        goBackUserList(null);
    }

    public void goBackUserList(View view) {
        Intent intent = new Intent(this, HomePage.class);


        startActivity(intent);
        finish();
    }

}

