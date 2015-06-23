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

package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

public class User extends SugarRecord<User> {

    String uid;
    String name;

    public User() {
    }

    public User(String name){
        this.name = name;
    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the existing User in the database (first position of the list of users found searched
     * by the given username, otherwise it creates a new user, sets its username and returns it
     * @param username User name
     * @return the existing user matching the given username, or a new user with that username
     */
    public static User getUser(String username){
        return getUserOrCreate(username, true);
    }

    /**
     * Returns the existing User in the database (first position of the list of users found searched
     * by the given username, otherwise depending on the param create, it creates, saves and returns it,
     * or it simply it returns an empty new user without saving it
     * @param username User name
     * @param create Var to decide whether or not create the user in case no user matched
     * @return the existing user, a new user with that username already saved or an empty new user
     */
    public static User getUserOrCreate(String username, boolean create){
        User user;
        if (hasUser(username)){
            user = Select.from(User.class).where(Condition.prop("name").eq(username)).list().get(0);
        } else if (create){
            user = new User(username);
            user.save();
        } else {
            user = new User();
        }
        return user;
    }

    /**
     * Returns a boolean saying whether or not there is a user in the DB with that username
     * @param username User name
     * @return the existance of that user in the DB
     */
    public static boolean hasUser (String username){
        return (Select.from(User.class).where(Condition.prop("name").eq(username)).count() != 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (!uid.equals(user.uid)) return false;

        return true;
    }


    @Override
    public int hashCode() {
        int result = uid.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
