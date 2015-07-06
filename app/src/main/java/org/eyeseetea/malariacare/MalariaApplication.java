/*
 * Copyright (c) 2015.
 *
 * This file is part of Health Network QIS App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare;

import android.app.Activity;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.android.sdk.persistence.IDhis2Application;

/**
 * Created by nacho on 22/06/15.
 */
public class MalariaApplication extends com.orm.SugarApp implements IDhis2Application {

    /*public static Bus bus;
    public static Dhis2 dhis2;

    static {
        bus = new MainThreadBus(ThreadEnforcer.ANY);
        dhis2 = new Dhis2();
        bus.register(dhis2);
    }*/

    public Class<? extends Activity> getMainActivity() {
        return new DashboardActivity().getClass();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }

}