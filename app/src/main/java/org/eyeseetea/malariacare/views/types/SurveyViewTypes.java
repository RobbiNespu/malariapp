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

package org.eyeseetea.malariacare.views.types;

import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Question;

/**
 * Types of views in a survey, required for reusing Views.
 * Created by arrizabalaga on 9/07/15.
 */
public class SurveyViewTypes {
    public static final int HEADER = 0;
    public static final int DROPDOWN_LIST = 1;
    public static final int INT = 2;
    public static final int LONG_TEXT = 3;
    public static final int SHORT_TEXT = 4;
    public static final int DATE = 5;
    public static final int POSITIVE_INT = 6;
    public static final int NO_ANSWER = 7;
    public static final int RADIO_GROUP_HORIZONTAL = 8;
    public static final int RADIO_GROUP_VERTICAL = 9;


    /**
     * Returns the different types of items that can be created in a survey
     * @return
     */
    public static int getViewTypeCount(){
        return 10;
    }

    /**
     * Each type of header/question has an associated view.
     * @param item
     * @return
     */
    public static int getItemViewType(Object item){
        if(item instanceof Header){
            return SurveyViewTypes.HEADER;
        }

        Question question=(Question)item;

        return question.getAnswer().getOutput();
    }

    /**
     * Returns the type of layout to inflate according to the viewType
     * @param viewType
     * @return
     */
    public static int getLayoutByViewType(int viewType){
        switch (viewType){
            case (DROPDOWN_LIST):
                return R.layout.ddl;
            case (INT):
                return R.layout.integer;
            case (LONG_TEXT):
                return R.layout.longtext;
            case (SHORT_TEXT):
                return R.layout.shorttext;
            case (DATE):
                return R.layout.date;
            case (POSITIVE_INT):
                return R.layout.integer;
            case (NO_ANSWER):
                return R.layout.label;
            case (RADIO_GROUP_HORIZONTAL):
                return R.layout.radio;
            case (RADIO_GROUP_VERTICAL):
                return R.layout.radio;
            default://header
                return R.layout.headers;
        }
    }

}
