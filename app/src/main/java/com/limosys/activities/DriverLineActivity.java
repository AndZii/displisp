package com.limosys.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.limosys.fragments.DispLineMapFragment;
import com.limosys.fragments.DispLogInFragment;
import com.limosys.fragments.DriverLinesMapFragment;
import com.limosys.fragments.DriverLogInFragment;
import com.limosys.linedisp.DeviceUtils;
import com.limosys.linedisp.DispLineUtils;
import com.limosys.linedisp.R;
import com.limosys.ws.obj.displine.Ws_GetDispLineParamResult;

public class DriverLineActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_line);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);



        if(DeviceUtils.getDeviceId(this) != null && !"".equals(DeviceUtils.getDeviceId(this))
                && DeviceUtils.getCompanyName(this) != null && !"".equals(DeviceUtils.getCompanyName(this))
                && DeviceUtils.getCarId(this) != null && !"".equals(DeviceUtils.getCarId(this))) {
           DriverLinesMapFragment driverLineMapFragment = new DriverLinesMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.driver_fragments_place, driverLineMapFragment, driverLineMapFragment.getTag()).commit();
        } else {

            if(DeviceUtils.getDeviceId(this) == null){

                DispLineUtils.getDispLineParams(this, new DispLineUtils.GetDispLineParamCallback() {
                    @Override
                    public void onSuccess(Ws_GetDispLineParamResult res) {
                        if (res != null && res.getDeviceId() != null) {
                            DeviceUtils.setDeviceId(DriverLineActivity.this, res.getDeviceId());
                            DriverLogInFragment loginFragment = new DriverLogInFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.driver_fragments_place, loginFragment, loginFragment.getTag()).commit();
                        }
                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(DriverLineActivity.this, error, Toast.LENGTH_LONG).show();

                    }
                });

            } else {
                DriverLogInFragment loginFragment = new DriverLogInFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.driver_fragments_place, loginFragment, loginFragment.getTag()).commit();
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

    public void showToolbar(){
        toolbar.setVisibility(View.VISIBLE);
    }

    public void hideTollBar(){
        toolbar.setVisibility(View.GONE);
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
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.pop_enter, R.anim.pop_exit).replace(R.id.driver_fragments_place,
                new DriverLogInFragment(), new DriverLogInFragment().getTag()).commit();
    }


}
