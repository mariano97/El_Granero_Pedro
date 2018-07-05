package surabastos.pedro.granero.com.el_granero_pedro.controller;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import surabastos.pedro.granero.com.el_granero_pedro.R;
import surabastos.pedro.granero.com.el_granero_pedro.model.FormRegistry;

public class MainActivity extends AppCompatActivity implements View.OnClickListener/*, AdapterView.OnItemSelectedListener*/ {

    private static final String TAG = "EmailPassword";

    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;

    private int dd, mm, aaaa, yearBirthday;
    private int i=0;
    private EditText txtBirthdate;
    private EditText txtID;
    private EditText txtFirstName;
    private EditText txtSecondName;
    private EditText txtAddress;
    private EditText txtPhone;
    private EditText txtDistrict;
    private EditText txtMunicipality;
    private EditText txtAge;
    private Button btnContinue;

    private AwesomeValidation awesomeValidation;

    //private String[] typeStreets = {"CL", "KR", "AU", "AV", "AC", "AK", "BL", "CT", "CV", "DG", "PT", "TV", "TC"};
    private boolean adult = false;
    private FormRegistry formRegistry;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        txtBirthdate = (EditText) findViewById(R.id.labelBirthdate);
        txtID = (EditText) findViewById(R.id.ID);
        txtFirstName = (EditText) findViewById(R.id.labelFirstName);
        txtSecondName = (EditText) findViewById(R.id.labelSecondName);
        txtAddress = (EditText) findViewById(R.id.labelAddress);
        txtPhone = (EditText) findViewById(R.id.labelPhone);
        txtDistrict = (EditText) findViewById(R.id.labelBarrio);
        txtMunicipality = (EditText) findViewById(R.id.labelMunicipio);
        txtAge = (EditText) findViewById(R.id.labelAge);

        btnContinue = (Button) findViewById(R.id.btnComRegistry);
        btnContinue.setOnClickListener(this);
        txtBirthdate.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        validateFields();

        /*spin = (Spinner) findViewById(R.id.spTypeStreet);
        spin.setOnItemSelectedListener(this);*/

       /*ArrayAdapter aa = new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_item,typeStreets);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);*/

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.labelBirthdate:
                //i = i+1;

                final Calendar calendar = Calendar.getInstance();
                dd = calendar.get(Calendar.DAY_OF_MONTH);
                mm = calendar.get(Calendar.MONTH);
                aaaa = calendar.get(Calendar.YEAR);

                int difYear = aaaa - 118;

                Date dateMin = new GregorianCalendar(difYear, 0, 2).getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtBirthdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        //Log.i(TAG, " year " + year);
                        yearBirthday = year;
                        isAdult(year);

                        Log.e(TAG, " year " + yearBirthday);
                    }
                }, dd, mm, aaaa);
                yearBirthday = datePickerDialog.getDatePicker().getYear();
                Log.e(TAG, "datePicker " + datePickerDialog.getDatePicker().getYear() + " yB " + yearBirthday + " ");
                datePickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                //calendar.add(Calendar.YEAR, 0);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                //Log.i(TAG, " dateMin " + dateMin + " dateMin.get " + dateMin.getTime() + " year " + aaaa);
                datePickerDialog.show();
                //Log.e(TAG, " i " + i );
                break;

            case R.id.btnComRegistry:

                validateIfExistUser(txtID.getText().toString().trim());

                Log.i(TAG, "i " + (i++) +" awe " + awesomeValidation.validate() + " boo " + adult + " mAgetE " + mAuth.getCurrentUser().getEmail() + " mAgetET " + mAuth.getCurrentUser().getEmail());
        }
    }

    private void validateIfExistUser(final String dataField){

        Log.e(TAG,"metodo ifExist " + (i++));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        final String userUid = mAuth.getUid();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(dataField) && awesomeValidation.validate() && adult){
                    saveData(userUid);
                    //No olvidar borrar las sharedPreferences cuando se hace LogOut
                    savePreferencesRegistryComplete();
                    Toast.makeText(MainActivity.this, R.string.message_assert_complete_registry, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Log.e(TAG, "No existe " + (i++));
                }else{
                    Toast.makeText(MainActivity.this, R.string.message_err_user_exist, Toast.LENGTH_LONG).show();
                    txtID.setText("");
                    txtFirstName.setText("");
                    txtSecondName.setText("");
                    txtBirthdate.setText("");
                    txtAge.setText("");
                    txtAddress.setText("");
                    txtMunicipality.setText("");
                    txtDistrict.setText("");
                    txtPhone.setText("");
                    Log.e(TAG, "Si existe " + (i++));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateFields() {

        awesomeValidation.addValidation(MainActivity.this, R.id.ID, "[0-9]{1,10}", R.string.message_err_id);
        awesomeValidation.addValidation(MainActivity.this, R.id.labelFirstName, "[a-zA-Z\\\\s]+", R.string.message_err_firstsecond_name);
        awesomeValidation.addValidation(MainActivity.this, R.id.labelSecondName, "[a-zA-Z\\\\s]+", R.string.message_err_firstsecond_name);
        awesomeValidation.addValidation(MainActivity.this, R.id.labelBarrio, "[a-zA-Z\\\\s]+", R.string.message_err_firstsecond_name);
        awesomeValidation.addValidation(MainActivity.this, R.id.labelMunicipio, "[a-zA-Z\\\\s]+", R.string.message_err_firstsecond_name);
        awesomeValidation.addValidation(MainActivity.this, R.id.labelAddress, "([a-zA-Z]*[0-9])*(.)+", R.string.message_err_address);
        awesomeValidation.addValidation(MainActivity.this, R.id.labelPhone, "(?:\\(\\d{3}\\)|\\d{3}[-]*)\\d{3}[-]*\\d{4}", R.string.message_err_phone);
    }


    @SuppressLint("SetTextI18n")
    private void isAdult(int year) {
        int realAge = aaaa - year;
        if (realAge >= 18) {
            txtAge.setText(realAge + " " + getString(R.string.mainActivity_form_registry_rpt_age));
            txtBirthdate.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
            txtAge.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
            adult = true;
        } else {
            txtBirthdate.setText("");
            txtAge.setText("");
            txtBirthdate.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            txtAge.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(MainActivity.this, R.string.message_err_age, Toast.LENGTH_SHORT).show();
            adult = false;
        }
        //Log.e(TAG, "isAdult " + adult + " R.string " + getString(R.string.mainActivity_form_registry_rpt_age));
    }

   /* @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), typeStreets[position], Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/

   private void saveData(String uid){

       String ID = txtID.getText().toString().trim();
       String firstName = txtFirstName.getText().toString().trim();
       String secondName = txtSecondName.getText().toString().trim();
       String birthday = txtBirthdate.getText().toString().trim();
       String age = txtAge.getText().toString().trim();
       String address = txtAddress.getText().toString().trim();
       String district = txtDistrict.getText().toString().trim();
       String municipality = txtMunicipality.getText().toString().trim();
       String phone = txtPhone.getText().toString().trim();
       String email = mAuth.getCurrentUser().getEmail().trim();

       FormRegistry formRegistry = new FormRegistry(ID, firstName, secondName, birthday, age, address, district, municipality, phone, email);

       FirebaseDatabase database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference();

       myRef.child("users").child(uid).setValue(formRegistry);
   }

   private void savePreferencesRegistryComplete(){

       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putString("RegitryComplete", "true");
       editor.apply();
   }
}
