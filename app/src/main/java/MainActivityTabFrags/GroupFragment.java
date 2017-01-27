package MainActivityTabFrags;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.personal.debrian.squareone.GroupListAdapter;
import com.personal.debrian.squareone.Login;
import com.personal.debrian.squareone.MainActivity;
import com.personal.debrian.squareone.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {


    private FirebaseAuth myAuth;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("-familygroup");
    private ValueEventListener eventListener;
    private String NAME;
    private ListView list;
    private GroupListAdapter adapter;
    private ArrayList<String> groups = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        groups.clear();
        myAuth = FirebaseAuth.getInstance();
        NAME = myAuth.getCurrentUser().getDisplayName();
        list = (ListView) view.findViewById(R.id.myGroupList);
        adapter = new GroupListAdapter(getContext(),groups);
        list.setAdapter(adapter);

        //Listener
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return view;
    }

    //Methods
    private void updateList(final DataSnapshot dataSnapshot) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();

                while(iter.hasNext()){
                    DataSnapshot snap = iter.next();
                    String groupName = snap.getKey();
                        Iterator<DataSnapshot> i = snap.getChildren().iterator();
                        while (i.hasNext()) {
                            Map<String, Object> map = (Map<String, Object>) i.next().getValue();

                            String name = (String) map.get("user");
                            if (NAME.equals(name) && !groups.contains(groupName)) {
                                groups.add(groupName);

                            }
                        }
                }

                //handler
                handler.sendEmptyMessage(0);
            }
        };

        //thread
        Thread thread = new Thread(r);
        thread.start();
    }


    @Override
    public void onStart() {
        super.onStart();
        root.addValueEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(eventListener != null){
            root.removeEventListener(eventListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(eventListener != null){
            root.removeEventListener(eventListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//
//        if(eventListener != null)
//            root.addValueEventListener(eventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(eventListener != null)
            root.addValueEventListener(eventListener);
    }
}
