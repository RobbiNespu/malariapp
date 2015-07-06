/*
 * Copyright (c) 2015.
 *
 * This file is part of Facility QA Tool App.
 *
 *  Facility QA Tool App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Facility QA Tool App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;


public class DashboardActivity extends BaseActivity {
    public final static String TAG = DashboardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);

        Dhis2.getInstance().enableLoading(this, Dhis2.LOAD_EVENTCAPTURE);
        NetworkManager.getInstance().setCredentials(Dhis2.getCredentials(this));
        NetworkManager.getInstance().setServerUrl(Dhis2.getServer(this));
        //Dhis2.activatePeriodicSynchronizer(this);
        /*if (Dhis2.isInitialDataLoaded(this)) {
            //showSelectProgramFragment();
            Log.i(".DetailsActivity", "data is already loaded");
        } else {
            //loadInitialData();
        }*/

        if (savedInstanceState == null) {
            DashboardUnsentFragment unsentFragment = new DashboardUnsentFragment();
            unsentFragment.setArguments(getIntent().getExtras());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.dashboard_details_container, unsentFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            DashboardSentFragment sentFragment = new DashboardSentFragment();
            sentFragment.setArguments(getIntent().getExtras());
            FragmentTransaction ftr = getFragmentManager().beginTransaction();
            ftr.add(R.id.dashboard_completed_container, sentFragment);
            ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ftr.commit();
        }
    }

@Override
    protected void initTransition(){
        this.overridePendingTransition(R.transition.anim_slide_in_right, R.transition.anim_slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_update_metadata:
                // TODO: Here we must take the user and get its allowed data from server
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Just to avoid trying to navigate back from the dashboard. There's no parent activity here
     */
    @Override
    public void onBackPressed() {
        Log.d(".DashboardDetails", "back pressed");
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit the app?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).create().show();
    }
}
