package Manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.boostup.MainActivity;
import com.example.boostup.R;
import com.example.boostup.Seettings;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerHomeFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    TextView username;
    ProgressDialog pd;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String uid;

    public ManagerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_manager_home, container, false);
        // Inflate the layout for this fragment
        username=view.findViewById(R.id.username);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("user").child("Managers");

        Query query=databaseReference.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    String confirm= ""+ds.child("Account").getValue();
                    String uname=""+ds.child("UserName").getValue();
                    username.setText(uname);
                    if(confirm.equals("Delete")){
                        deleteAccount();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    private void deleteAccount() {
        try{
                AlertDialog.Builder myAlert= new AlertDialog.Builder(getActivity());
                myAlert.setTitle("Account Deleted.........!");
                myAlert.setMessage("Sorry! Account is Deleted");
                myAlert.setPositiveButton("OK", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
                myAlert.show();
            }
            catch (Exception e){
               // Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            pd=new ProgressDialog(getActivity());
            pd.setMessage("Logging Out");
            pd.show();
            signOut();
            Intent i = new Intent(getActivity(),ManagerLogin.class);
            pd.dismiss();
            startActivity(i);
        }
        if(id == R.id.settings){
            Intent i = new Intent(getActivity(), Seettings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    private void signOut() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account!=null){
            String PersonName=account.getDisplayName();
            String PersonGivenName=account.getGivenName();
            String PersonFamilyName=account.getFamilyName();
            String PersonEmail=account.getEmail();
            String PersonId=account.getId();
            Uri PersonPhoto=account.getPhotoUrl();
            //Toast.makeText(this, "Welcome "+PersonName, Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected()){
            return  true;

        }
        else{
            return false;
        }

    }
    @Override
    public void onStart() {
        if(isNetworkAvailable(getContext())) {
        }
        else{
            androidx.appcompat.app.AlertDialog.Builder myAlert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            myAlert.setTitle("Warning!");
            myAlert.setMessage("No Internet Connection!");
            myAlert.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            myAlert.show();
        }
        super.onStart();
    }




}
