package com.limosys.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.limosys.fragments.DispLineMapFragment;
import com.limosys.fragments.DispLogInFragment;
import com.limosys.linedisp.DeviceUtils;
import com.limosys.linedisp.DispLineUtils;
import com.limosys.linedisp.R;
import com.limosys.ws.obj.displine.Ws_GetDispLineParamResult;


public class DispLineActivity extends AppCompatActivity {

    private DispLineMapFragment dispLineMapFragment;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_line);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        hideTollBar();

            if(DeviceUtils.getDeviceId(this) != null && !"".equals(DeviceUtils.getDeviceId(this))
                    && DeviceUtils.getEmployeeId(this) != null && !"".equals(DeviceUtils.getEmployeeId(this))
                    && DeviceUtils.getLineId(this) != null && !"".equals(DeviceUtils.getLineId(this))) {
                dispLineMapFragment = new DispLineMapFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_place, dispLineMapFragment, dispLineMapFragment.getTag()).commit();
            } else {

                if(DeviceUtils.getDeviceId(this) == null){

                    DispLineUtils.getDispLineParams(this, new DispLineUtils.GetDispLineParamCallback() {
                        @Override
                        public void onSuccess(Ws_GetDispLineParamResult res) {
                            if (res != null && res.getDeviceId() != null) {
                                DeviceUtils.setDeviceId(DispLineActivity.this, res.getDeviceId());
                                DispLogInFragment loginFragment = new DispLogInFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_place, loginFragment, loginFragment.getTag()).commit();
                            }
                        }

                        @Override
                        public void onError(String error) {

                            Toast.makeText(DispLineActivity.this, error, Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    DispLogInFragment loginFragment = new DispLogInFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_place, loginFragment, loginFragment.getTag()).commit();
                }
            }
    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_disp_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Do You want to logout?")
                    .setIcon(R.drawable.ic_exit_to_app_black_24dp)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            logOut();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        DeviceUtils.setEmployeeId(this,"");
        DeviceUtils.setLineId(this,"");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_place,
                new DispLogInFragment(), new DispLogInFragment().getTag()).commit();
    }

    public void showToolbar(){
        toolbar.setVisibility(View.VISIBLE);
    }

    public void hideTollBar(){
        toolbar.setVisibility(View.GONE);
    }
}
