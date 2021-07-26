package com.harsha.eevee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class UserDetailsInput extends AppCompatActivity {

    EditText userNameET;
    RadioButton maleRB;
    RadioButton femaleRB;
    RadioGroup selectGenderRG;

    UserDetailsDBHandler dbHandler;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details_input);

        userNameET = (EditText) findViewById(R.id.userDetaisET);
        maleRB = (RadioButton) findViewById(R.id.maleRB);
        femaleRB = (RadioButton) findViewById(R.id.femaleRB);
        selectGenderRG = (RadioGroup) findViewById(R.id.selectGenderRG);

        /**
         * This is designed for a single user system
         * This ensures that always update operation has to be done on UserData Table
         * */
        dbHandler = new UserDetailsDBHandler(this, null, null, 1);
        if (dbHandler.getCount() == 1) {
            autofill();
        } else {
            dbHandler.dropTable();
            UserDetails dummyUser = new UserDetails("", true, "");
            dbHandler.insertRow(dummyUser);
        }
    }

    public void updateUserDetails(View view) {
        String userName = userNameET.getText().toString();
        boolean maleChecked = maleRB.isChecked();
        boolean femaleChecked = femaleRB.isChecked();

        if (userName.matches("")) {
            Toast.makeText(UserDetailsInput.this, "Please give your name , I promise you won't regret it", Toast.LENGTH_SHORT).show();
            return;
        } else if (!maleChecked && !femaleChecked) {
            Toast.makeText(UserDetailsInput.this, "Who should I call you boss? Your Title is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean maleSelected = true;
        if (maleChecked) {
            maleSelected = true;
        } else if (femaleChecked) {
            maleSelected = false;
        }

        UserDetails user = new UserDetails(userName, maleSelected, "");
        dbHandler.updateThisInFirstRow(UserDetailsDBHandler.COLUMN_USER_NAME, user.get_userName());
        dbHandler.updateThisInFirstRow(UserDetailsDBHandler.COLUMN_MALE, String.valueOf(user.is_Male()));

        Toast.makeText(this, "Hello , " + UserDetails.$USERNAME + " How are you ?", Toast.LENGTH_SHORT).show();
        Intent userGroupInput = new Intent(this, UserGroupInput.class);
        startActivity(userGroupInput);
    }

    private void autofill() {
        UserDetails userDetails = dbHandler.getUserDetails();
        userNameET.setText(userDetails.get_userName());

        maleRB.setChecked(false);
        femaleRB.setChecked(false);

        if (userDetails.is_Male()) {
            maleRB.setChecked(true);
        } else {
            femaleRB.setChecked(true);
        }
    }

    public void makeInputFieldsEmpty() {
        userNameET.setText("");
        selectGenderRG.clearCheck();
    }

    // TODO : Dummy function
    public void viewTableClicked(View view) {
        Intent changeActivity = new Intent(this, ShowUsersTable.class);
        String usersTableToString = dbHandler.viewTable();
        changeActivity.putExtra("tableUsersData", usersTableToString);
        startActivity(changeActivity);
    }

}