package com.example.systhesis_project;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.systhesis_project.Connection.ConnectionClass;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.sql.DriverManager;

public class ViewAccount extends AppCompatActivity {

    EditText edEmail,edPhone,edDob;
    TextView tvStatus,tvFullName;
    Button btBack,btUpdate;
    Connection con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userName = getIntent().getStringExtra("uname");
        String password = getIntent().getStringExtra("pword");

        edEmail=(EditText)findViewById(R.id.email);
        edPhone=(EditText)findViewById(R.id.phone);
//        edDob=(EditText)findViewById(R.id.dob);
        tvStatus=(TextView)findViewById(R.id.status);
        tvFullName=(TextView)findViewById(R.id.accountName);
        btBack=(Button)findViewById(R.id.backMain);
        btUpdate=(Button)findViewById(R.id.updateInfo);

        con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),
                ConnectionClass.db.toString(),ConnectionClass.ip.toString());
        String sql = "SELECT * FROM Student WHERE StudentId = '" + userName + "' AND Password ='" + password + "'";
        try{
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                tvFullName.setText(rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5));
                edEmail.setText(rs.getString(7));
                edPhone.setText(rs.getString(8));
//                edDob.setText(rs.getString(9));
                tvStatus.setText(rs.getString(10));
            }
            con.close();
        }catch (Exception e){
            Log.e("SQL Error: ", e.getMessage());
        }
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewAccount.this,MainActivity.class);
                intent.putExtra("username",userName);
                intent.putExtra("password",password);
                startActivity(intent);
                finish();
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewAccount.updateAccount().execute("");
            }
        });
    }

    public class updateAccount extends AsyncTask<String, String, String>{

        String userName = getIntent().getStringExtra("uname");
        String password = getIntent().getStringExtra("pword");

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
                        Toast.makeText(ViewAccount.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
                    }
                });
                z = "On Internet Connection";
            }else {
                try {
                    String sql = "UPDATE Student SET Email = '" + edEmail.getText().toString() + "', Phone ='"
                            + edPhone.getText().toString() + "'" +
                            "WHERE StudentId='"+userName+"' AND Password ='"+password+"'";
                    Statement statement = con.createStatement();
                    statement.executeUpdate(sql);
                    con.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ViewAccount.this,"Update information success",Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(ViewAccount.this, MainActivity.class);
                    intent.putExtra("username",userName);
                    intent.putExtra("password",password);
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