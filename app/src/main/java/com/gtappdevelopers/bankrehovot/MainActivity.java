package com.gtappdevelopers.bankrehovot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.annotation.Documented;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("Trades").document("TradesID");
//    private Map<String, Object> dataToSave;
//    private Map<String, Object> dataFetch;
//    private String fetchData;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
//       CollectionReference Trades = db.collection("Trades");
//        Trades.document("Prices").collection()


        //this sets data
        Map<String, Object> docData = new HashMap<>();
        docData.put("price", "1234");
        db.collection("Trades").add(docData); // this line creates a new document

        //this gets data
        db.collection("Trades").document("Prices").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    TextView textView = findViewById(R.id.txt1);
                    textView.setText(document.getData() + "s");
                }
            }
        });


//        TextView textView = findViewById(R.id.txt1);
//        textView.setText(docData + "s");
//        db.collection("Trades").document("Prices")
//                .set(docData)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
        //.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                // after the data addition is successful
//                // we are displaying a success toast message.
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // this method is called when the data addition process is failed.
//                // displaying a toast message when data addition is failed.
//            }
//        });


        //save data
//        dataToSave = new HashMap<String, Object>();
//        saveDataFirebase();

        //get data and show on screen
        // fetchDataFirebase();
//        dataFetch= mDocRef.get().addOnSuccessListener.getResult().getData();


//        TextView textView = findViewById(R.id.txt1);
//        textView.setText(dataFetch + "s");
        //end of onCreate
    }


//
//    public void saveDataFirebase() {
//
//        dataToSave.put("price", "" + 5555);
//        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                ;
//            }
//
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                ;
//            }
//        });
//
//    }
//
//
//    public void fetchDataFirebase() {
//
//        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    fetchData = documentSnapshot.getString("price");
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                ;
//            }
//        });
//    }


    //end of main
}