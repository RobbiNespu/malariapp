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

package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.DashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DashboardFragment extends ListFragment {
    int mCurCheckPosition = 0;

    private List<Survey> surveys;
    private List<IDashboardAdapter> adapters;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // show the progress bar
        setListShown(false);

        // Get the not-sent surveys ordered by date
        this.surveys = Survey.getUnsentSurveys(Constants.MAX_ITEMS_IN_DASHBOARD);

        // make a list with the adapters
        this.adapters = new ArrayList<>();
        this.adapters.add(new AssessmentAdapter(this.surveys, getActivity()));

        // create a list of listeners to capture the "see all" event
        List<View.OnClickListener> listeners = new ArrayList<>();
        listeners.add(new DashboardListener(getActivity(), getString(R.string.dashboard_button_see_all), 0));

        setListAdapter(new DashboardAdapter(this.adapters, listeners, getActivity()));

        if (savedInstanceState != null){
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        // hide the progress bar
        setListShown(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // show the progress bar
        setListShown(false);
        // show details in fragment or activity
        showDetails(position);
        // hide the progress bar
        setListShown(true);
    }*/

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;
        // Otherwise we need to launch a new activity to display
        // the dialog fragment with selected text.
        Intent intent = new Intent();
        intent.setClass(getActivity(), DashboardActivity.class);
        intent.putExtra("index", index);
        Session.setAdapterUnsent(adapters.get(index));
        startActivity(intent);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    private class DashboardListener implements View.OnClickListener {

        private String listenerOption; //One of edit, delete
        private Activity context;
        private int index;

        public DashboardListener(Activity context, String listenerOption, int index) {
            this.context = context;
            this.listenerOption = listenerOption;
            this.index = index;
        }

        public void onClick(View view) {
            if (listenerOption.equals(context.getString(R.string.dashboard_button_see_all))) {
                showDetails(index);
            }
        }
    }
}