package MainActivityTabFrags;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.personal.debrian.squareone.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Models.Suggestion;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private final String TAG = "Home";
    private TextView recent;
    private TextView recentName;
    private TextView recentColor;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(!suggestions.isEmpty()) {
                recent.setText(suggestions.get(suggestions.size() - 1).getFood());
                recentName.setText("By: " + suggestions.get(suggestions.size() - 1).getName());
                recentColor.setText("For: "+ suggestions.get(suggestions.size() - 1).getWeekday()+"'s "+suggestions.get(suggestions.size() - 1).getColor());
            }
        }
    };
    //TODO: add forgot password, add check box in list to show cook will make that food
    private DatabaseReference root;
    private ValueEventListener valueListener;
    private ArrayList<Suggestion> suggestions = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        root = FirebaseDatabase.getInstance().getReference().getRoot();
        suggestions.clear();

        recent = (TextView) view.findViewById(R.id.recentSuggestionText);
        recentName = (TextView) view.findViewById(R.id.recentNameText);
        recentColor = (TextView) view.findViewById(R.id.colorText);

        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //place in thread
                update(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        removeOldSuggestions();
        return view;
    }

    private void removeOldSuggestions() {
        long cutOff = new Date().getTime()- TimeUnit.MILLISECONDS.convert(7,TimeUnit.DAYS);
        Query oldItems = root.orderByChild("timestamp").endAt(cutOff);

        //remove items that are 7 days old
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        if(!itemSnapshot.getKey().equals("-familygroup")) {
                            itemSnapshot.getRef().removeValue();
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void update(final DataSnapshot snapshot) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();

                //ArrayList<Suggestion> temp = new ArrayList<>();
                while (iter.hasNext()){
                    DataSnapshot snap = iter.next();
                    if(!snap.getKey().equals("-familygroup")) {
                        Map<String, Object> map = (Map<String, Object>) snap.getValue();
                        String food = (String) map.get("food");
                        String name = (String)map.get("name");
                        String weekday = (String)map.get("weekday");
                        String key = (String) map.get("keyId");
                        String color = (String) map.get("color");
                        Map<String,Object> timestamp = new HashMap<>();

                        long time = (long) map.get("timestamp");
                        timestamp.put("timestamp",time);
                        Suggestion idea = new Suggestion(name, food, key, weekday, color,timestamp);

                        suggestions.add(idea);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };

        Thread thread = new Thread(r);
        thread.start();

        //cant update interface in thread
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "OnDetach");
        if(valueListener != null){
            root.removeEventListener(valueListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "OnAttach");
        if(valueListener != null)
            root.addValueEventListener(valueListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
        if(valueListener != null){
            root.removeEventListener(valueListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart");
        root.addValueEventListener(valueListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");
        root.addValueEventListener(valueListener);
    }
}
