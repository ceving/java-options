/**
 * Parse command line arguments.
 *
 * Copyright (C) 2013  Sascha Ziemann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package szi.options;

import java.util.Arrays;

class Option
{
    final Character flag;
    final String    name;
    final String    description;
    final int       required_values;
    final String[]  default_values;

    boolean  isset  = false;
    String[] values = null;

    /**
     * @throws InvalidOptionException
     */
    Option (Character flag,
            String    name,
            String    description,
            int       required_values,
            String... default_values)
    {
        if (flag == null && name == null)
            throw new InvalidOptionException
                ("Missing option identifier");
        if (description == null)
            throw new InvalidOptionException
                ("Missing option description");
        this.flag            = flag;
        this.name            = name;
        this.description     = description;
        this.required_values = required_values;
        this.default_values  = default_values;
        this.values = new String[required_values];
    }
        
    String id ()
    {
        if (name != null)
            return name;
        if (flag != null)
            return flag.toString();
        return null;
    }

    String value (int i)
    {
        if (values != null)
            if (values[i] != null)
                return values[i];
            else
                if (default_values != null)
                    return default_values[i];
                else
                    return null;
        else
            if (default_values != null)
                return default_values[i];
            else
                return null;
    }

    public String toString()
    {
        return "{flag=" + flag + ";name=" + name + ";description=" +
            description + ";required_values=" + required_values +
            ";default_values=" +
            (default_values == null ? null : 
             Arrays.toString((Object[])default_values)) +
            ";values=" + 
            (values == null ? null :
             Arrays.toString((Object[])values))
            + "}";
    }
}
