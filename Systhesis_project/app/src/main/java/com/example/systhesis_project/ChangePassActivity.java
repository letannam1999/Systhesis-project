package com.example.systhesis_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.systhesis_project.Connection.ConnectionClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ChangePassActivity extends AppCompatActivity {

    Button btnBack, btnUpdate;
    EditText oldPass, newPass, rePass;
   // TextView mypass;
    Connection con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        btnBack=(Button)findViewById(R.id.back_button);
        btnUpdate=(Button)findViewById(R.id.submit_button);
        oldPass=(EditText)findViewById(R.id.currentPass);
        newPass=(EditText)findViewById(R.id.newPass);
        rePass=(EditText)findViewById(R.id.reEnterPass);
   //     mypass=(TextView)findViewById(R.id.mypass);
        String userPass = getIntent().getStringExtra("pword");
//        mypass.setText(userPass);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePassActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPass.getText().toString().equals("")||newPass.getText().toString().equals("")
                        ||rePass.getText().toString().equals("")){
                    oldPass.setText("");
                    newPass.setText("");
                    rePass.setText("");
                    Toast.makeText(ChangePassActivity.this,"Please input all fields",Toast.LENGTH_LONG).show();
                }else if(oldPass.getText().toString().equals(userPass)
                        && newPass.getText().toString().equals(rePass.getText().toString())){
                    new ChangePassActivity.updatePassword().execute("");
                }else{
                    oldPass.setText("");
                    newPass.setText("");
                    rePass.setText("");
                    Toast.makeText(ChangePassActivity.this,"Your information you input was wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class updatePassword extends AsyncTask<String, String , String>{
        String userName = getIntent().getStringExtra("uname");

        String z = null;
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s) { }

        @Override
        protected String doInBackground(String... strings) {
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),
                    ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            if(con==null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePassActivity.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
                    }
                });
                z = "On Internet Connection";
            }else {
                try {
                    String sql = "UPDATE Student SET Password = '"+newPass.getText().toString()+"' WHERE StudentId='"+userName+"' AND Password ='"+oldPass.getText().toString()+"'";
                    Statement statement = con.createStatement();
                    statement.executeUpdate(sql);
                    con.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePassActivity.this,"Update password success please login again",Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(ChangePassActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL Error: ", e.getMessage());
                }
            }
            return z;
        }
    }

    @SuppressLint("NewApi")
    public java.sql.Connection connectionClass(String user, String password, String database, String server){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        java.sql.Connection connection = null;
        String connectionURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://"+server+"/"+database+";user="+user+";password="+password+";";
            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception e){
            Log.e("SQL Connection Error: ",e.getMessage());
        }
        return connection;
    }
}