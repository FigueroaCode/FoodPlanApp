package MainActivityTabFrags;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.personal.debrian.squareone.FoodPlanActivity;
import com.personal.debrian.squareone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerFragment extends Fragment {

    private Button planBtn;
    private Button createBtn;
    private Button joinBtn;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("-familygroup");
    private FirebaseAuth myAuth;
    private ValueEventListener valueListener;
    private String NAME;

    //private ArrayList<String> familyGroups = new ArrayList<>();
    private Map<String,ArrayList<String>> familyGroups = new HashMap<>();

    public PlannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_planner, container, false);

        familyGroups.clear();
        myAuth = FirebaseAuth.getInstance();
        NAME = myAuth.getCurrentUser().getDisplayName();
        planBtn = (Button) view.findViewById(R.id.goToPlan);
        createBtn = (Button) view.findViewById(R.id.createGroupBtn);
        joinBtn = (Button) view.findViewById(R.id.joinGroupBtn);

        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),FoodPlanActivity.class));
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroup();
            }
        });


        return view;
    }

    private void joinGroup(){
        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.create_group_dialog,null);
        final EditText groupName = (EditText) alertView.findViewById(R.id.createGroupInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Join Group");
        builder.setView(alertView);
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = groupName.getText().toString();
                if(!name.isEmpty()){
                    Map<String,Object> map = new HashMap<>();
                    if(groupExists(name)){
                        if(notInGroup(name)) {
                            String key = root.child(name).push().getKey();
                            map.put("user", NAME);
                            map.put("status", "not owner");
                            root.child(name).child(key).updateChildren(map);

                            Toast.makeText(getContext(), "Group " + name + " joined", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(),"Already part of group", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(),"Group Doesn't Exist", Toast.LENGTH_SHORT).show();
                    }
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

    private void createGroup(){

        View alertView = LayoutInflater.from(getContext()).inflate(R.layout.create_group_dialog,null);
        final EditText groupName = (EditText) alertView.findViewById(R.id.createGroupInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Create Group");
        builder.setView(alertView);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = groupName.getText().toString();
                if(!name.isEmpty() && !groupExists(name)){
                    Map<String,Object> map = new HashMap<>();
                    //root.setValue(name);
                    String key = root.child(name).push().getKey();
                    map.put("user", NAME);
                    map.put("status","owner");

                    root.child(name).child(key).updateChildren(map);

                    Toast.makeText(getContext(), "Group Created", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Name not Available", Toast.LENGTH_SHORT).show();
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

    private boolean groupExists(String name){

        if(familyGroups.containsKey(name)){
            return true;
        }else{
            return false;
        }

    }

    private void updateList(DataSnapshot snapshot){
        Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();

        //ArrayList<Suggestion> temp = new ArrayList<>();
        while (iter.hasNext()){
            DataSnapshot data = iter.next();

            String groupname = data.getKey();
            String key = "";
            String name = "";
            Iterator<DataSnapshot> i = data.getChildren().iterator();
            ArrayList<String> arr = new ArrayList<>();
            while(i.hasNext()){
                DataSnapshot s = i.next();
                //key = s.getKey();
                Map<String,Object> map = (Map<String,Object>) s.getValue();
                name = (String) map.get("user");
                arr.add(name);
            }
            //arr.add(key);
            familyGroups.put(groupname, arr);
        }
    }

    private boolean notInGroup(String name){
        for(int i = 0; i < familyGroups.get(name).size();i++){
            if(familyGroups.get(name).get(i).equals(NAME)){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        root.addValueEventListener(valueListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(valueListener != null){
            root.removeEventListener(valueListener);
        }
    }
}
