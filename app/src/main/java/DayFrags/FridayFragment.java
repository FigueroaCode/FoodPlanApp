package DayFrags;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.personal.debrian.squareone.CustomListAdapter;
import com.personal.debrian.squareone.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Models.CurrentGroup;
import Models.Suggestion;

/**
 * A simple {@link Fragment} subclass.
 */
public class FridayFragment extends Fragment {

    private final String KEY = "Friday";
    private String NAME;
    private ListView list;
    private Spinner spinner;

    private CustomListAdapter adapter;
    private ArrayAdapter<String> adapterSpinner;
    private ArrayList<Suggestion> suggestions = new ArrayList<>();
    private Map<String,ArrayList<String>> familyGroups = new HashMap<>();
    private ArrayList<String> myGroups = new ArrayList<>();

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference familyRoot = FirebaseDatabase.getInstance().getReference().getRoot().child("-familygroup");
    private ValueEventListener valueListener;
    private ChildEventListener childListener;
    private FirebaseAuth mAuth;

    private String currentGroup = "nope";
    private String prevKey;

    public FridayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_template, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.food_fab);

        suggestions.clear();
        myGroups.clear();
        list = (ListView) view.findViewById(R.id.list);
        mAuth = FirebaseAuth.getInstance();
        NAME = mAuth.getCurrentUser().getDisplayName();
        adapter = new CustomListAdapter(getContext(),suggestions);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        adapterSpinner = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,myGroups);

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapterSpinner);
        list.setAdapter(adapter);

        ///Spinner Listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurrentGroup.groupName = myGroups.get(position);
                currentGroup = CurrentGroup.groupName;
                suggestions.clear();
                prevKey = "";
                root.addChildEventListener(childListener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /// Add info to db
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!myGroups.isEmpty()) {
                    prompt();
                }else{
                    Toast.makeText(getContext(),"Not in a group",Toast.LENGTH_SHORT).show();
                }
            }
        });
        ////// end of fab listener

        ///// Family listener
        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateGroup(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ///// End of Family listener

        ///// DB child listeners
        childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.d(KEY,"Current Key: " + dataSnapshot.getKey() + " vs " + prevKey);
                if(!dataSnapshot.getKey().equals(prevKey)) {
                    updateList(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //updateList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(int i = 0;  i < suggestions.size(); i++){
                    if(dataSnapshot.getKey().equals(suggestions.get(i).getKeyId())){
                        suggestions.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ////// End of Child event listener

        /// Delete Items
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String name = suggestions.get(position).getName();
                if(NAME.equals(name)) {
                    root.child(suggestions.get(position).getKeyId()).removeValue();
                }else{
                    Toast.makeText(getContext(),"Not Your Suggestion",Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        return view;
    }

    ////////// Methods //////////
    private int getIndex(String groupName) {

        return myGroups.indexOf(groupName);
    }

    public void prompt() {
        final View alertView = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final RadioGroup radioGroup = (RadioGroup) alertView.findViewById(R.id.radioGroup);
        final EditText foodSuggestion = (EditText) alertView.findViewById(R.id.foodSuggestion);

        builder.setTitle("Add Suggestion");
        builder.setView(alertView);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add info to db
                String food = foodSuggestion.getText().toString();
                RadioButton radBtn = (RadioButton) alertView.findViewById(radioGroup.getCheckedRadioButtonId());
                String color = "lunch";
                if(radBtn != null) {
                    color = radBtn.getText().toString().toLowerCase();
                }

                if(!food.isEmpty()){
                    String key = root.push().getKey();
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.putAll(ServerValue.TIMESTAMP);
                    Suggestion idea = new Suggestion(NAME,food,key,KEY,color,map);

                    root.child(key).setValue(idea);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /// Update group
    private void updateGroup(DataSnapshot snapshot){
        //find which groups user belongs
        Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();

        while (iter.hasNext()){
            DataSnapshot data = iter.next();

            String groupname = data.getKey();
            //String key = "";
            String name = "";
            Iterator<DataSnapshot> i = data.getChildren().iterator();
            ArrayList<String> arr = new ArrayList<>();
            while(i.hasNext()){
                DataSnapshot s = i.next();
                //key = s.getKey();
                Map<String,Object> map = (Map<String,Object>) s.getValue();
                name = (String) map.get("user");
                if(name.equals(NAME)){
                    myGroups.add(groupname);
                    adapterSpinner.notifyDataSetChanged();
                }
                arr.add(name);
            }
            //Get users groups
            //arr.add(key);
            familyGroups.put(groupname, arr);
        }
        //Set default group
        if(!myGroups.isEmpty()){
            if (CurrentGroup.groupName.isEmpty()) {
                Log.d(KEY,"empty groupname");
                CurrentGroup.groupName = myGroups.get(0);
            }
            spinner.setSelection(getIndex(CurrentGroup.groupName));
            //CurrentGroup.groupName = myGroups.get(0);
            Log.d(KEY,"after"+CurrentGroup.groupName);
        }
    }
    //// Update list
    public void updateList(DataSnapshot snapshot) {
        if(!snapshot.getKey().equals("-familygroup")) {
            Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
            String day = (String) map.get("weekday");


            if (day.equals(KEY)) {
                String name = (String)map.get("name");
                String key = (String)map.get("keyId");
                prevKey = key;
                if(partOfGroup(CurrentGroup.groupName,name)) {
                    String food = (String)map.get("food");
                    String color = (String)map.get("color");
                    Map<String,Object> timestamp = new HashMap<>();

                    long time = (long) map.get("timestamp");
                    timestamp.put("timestamp",time);
                    Suggestion idea = new Suggestion(name, food, key, day, color,timestamp);
                    suggestions.add(idea);
                }
            }
            adapter.notifyDataSetChanged();
        }

    }

    private boolean partOfGroup(String groupName,String name){
        //Log.d(KEY,groupName + ": "+familyGroups.get(groupName).toString() + " vs " + name);
        if(!familyGroups.isEmpty() && familyGroups.get(groupName) != null && familyGroups.get(groupName).contains(name)) {
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        familyRoot.addValueEventListener(valueListener);

        root.addChildEventListener(childListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        prevKey = "";
        if(childListener != null){
            root.removeEventListener(childListener);
        }
        if(valueListener != null){
            familyRoot.removeEventListener(valueListener);
        }

        CurrentGroup.groupName = currentGroup;//fixed it
        Log.d(KEY,"Stop: "+CurrentGroup.groupName);

    }
}
