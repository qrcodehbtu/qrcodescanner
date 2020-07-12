
package obstinate.demon.qrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView codeScannerView;
    String rollno;
    String yearno;
    String branch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        codeScannerView=findViewById(R.id.scannerview);
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rollno = result.getText();
                        Log.d("rollno",rollno);
                        final DatabaseReference RootRef;
                        RootRef = FirebaseDatabase.getInstance().getReference();
                        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("students").child(rollno).exists())
                                {
                                    students studentdata=snapshot.child("students").child(rollno).getValue(students.class);
                                    yearno=studentdata.getYear();
                                    branch=studentdata.getBranch();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM:yyyy");
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd:MM:yyyy");
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
                                    String monthAndYear = sdf.format(new Date());
                                    String date = sdf1.format(new Date());
                                    String time = sdf2.format(new Date());
                                    if(!(snapshot.child("attendance").child(yearno).child(branch).child(rollno).child(monthAndYear).child(date).exists()))
                                    {
                                        Log.d("attendanceexist", "No");
                                        RootRef.child("attendance").child(yearno).child(branch).child(rollno).child(monthAndYear).child(date).setValue(time);
                                        Toast.makeText(MainActivity.this, "Attendance recorded", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Log.d("attendanceexist", "Yes");
                                        Toast.makeText(MainActivity.this, "Attendance already recorded", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Please place valid QR Code", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        codeScanner.startPreview();
                    }
                });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }
    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

}