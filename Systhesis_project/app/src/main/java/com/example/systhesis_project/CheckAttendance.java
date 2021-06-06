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
import android.widget.TextView;
import android.widget.Toast;

import com.example.systhesis_project.Connection.ConnectionClass;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CheckAttendance extends AppCompatActivity {

    Button btnSubmit, btnBack;
    TextView txtLecturerName, txtCourseName, txtStudentId, txtDate, txtShift;
    Connection con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        btnSubmit=(Button)findViewById(R.id.confirm_checkatendance);
        btnBack=(Button)findViewById(R.id.backToMain);
        txtLecturerName=(TextView)findViewById(R.id.lecturerName);
        txtCourseName=(TextView)findViewById(R.id.courseName);
        txtShift=(TextView)findViewById(R.id.shift);
        txtDate=(TextView)findViewById(R.id.dateNow);
        txtStudentId=(TextView)findViewById(R.id.studentId);

        String userName = getIntent().getStringExtra("usname");
        String password = getIntent().getStringExtra("psword");
        String qrCode = getIntent().getStringExtra("qrcode");
        String classId = qrCode.substring(0,8);
        String subjectId = qrCode.substring(8,11);
        String courseId = classId.concat(subjectId);
        String shift = qrCode.substring(11,12);
        String date = qrCode.substring(12);

        txtLecturerName.setText(getLecturerName(courseId));
        txtStudentId.setText(userName);
        getCourseName(courseId);
        txtDate.setText(date);
        txtShift.setText(shift);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CheckAttendance.this,MainActivity.class);
                intent.putExtra("username",userName);
                intent.putExtra("password",password);
                startActivity(intent);
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckAttendance.checkAttendance().execute("");
            }
        });
    }

    public String getLecturerName(String courseId) {
        con = connectionClass(ConnectionClass.un.toString(), ConnectionClass.pass.toString(),
                ConnectionClass.db.toString(), ConnectionClass.ip.toString());
        String sql = "SELECT Lecturer.FirstName, Lecturer.MidName, Lecturer.LastName " +
                "FROM Lecturer INNER JOIN Course ON Lecturer.LecturerId = Course.LecturerId " +
                "WHERE CourseId = '" + courseId + "'";
        String fullname = null;
        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                fullname = rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3);
            }
            con.close();
        } catch (Exception e) {
            Log.e("SQL Error: ", e.getMessage());
        }
        return fullname;
    }

    public void getCourseName(String courseId){
        con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),
                ConnectionClass.db.toString(),ConnectionClass.ip.toString());
        String sql = "SELECT Subject.NameSubject " +
                "FROM Subject INNER JOIN Course ON Subject.SubjectId = Course.SubjectId " +
                "WHERE CourseId = '" + courseId + "'";
        try{
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                txtCourseName.setText(rs.getString(1));
            }
            con.close();
        }catch (Exception e){
            Log.e("SQL Error: ", e.getMessage());
        }
    }

    public class checkAttendance extends AsyncTask<String,String,String>{
        String userName = getIntent().getStringExtra("usname");
        String password = getIntent().getStringExtra("psword");
        String qrCode = getIntent().getStringExtra("qrcode");
        String classId = qrCode.substring(0,8);
        String subjectId = qrCode.substring(8,11);
        String courseId = classId.concat(subjectId);
        String shift = qrCode.substring(11,12);
        String date = qrCode.substring(12);
        int studentId = Integer.parseInt(userName);

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
                        Toast.makeText(CheckAttendance.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
                    }
                });
                z = "On Internet Connection";
            }else {
                try {
                    String sql = "INSERT INTO Attendance (CourseId, StudentId, Shift, Date, Status)" +
                            "VALUES ('"+courseId+"','"+studentId+"','"+shift+"','"+ date +"','Studying')";
                    Statement statement = con.createStatement();
                    statement.executeUpdate(sql);
                    con.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CheckAttendance.this,"Check Attendance Success",Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(CheckAttendance.this, MainActivity.class);
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