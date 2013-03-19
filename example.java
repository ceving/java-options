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

import szi.options.Options;
import szi.options.InvalidOptionException;

class example
{
    static void die () { System.exit (1); }

    static void die (String... message)
    {
        for (String msg : message) System.err.println (msg);
        die();
    }
    static void die (Throwable e)
    {
        e.printStackTrace(System.err);
        die();
    }

    int a (String x, String y) { return new Integer(x) + new Integer(y); }
    int s (String x, String y) { return new Integer(x) + new Integer(y); }
    int m (String x, String y) { return new Integer(x) + new Integer(y); }
    int d (String x, String y) { return new Integer(x) + new Integer(y); }
    int q (String x) { Integer i = new Integer (x); return i * i; }

    public static void main (String[] args)
    {
        Options options = new Options()
            .about  ("Calc version 1.0")
            .usage  ("calc <option> x [y]")
            .option ("help", "Display usage.")
            .option ('a', "add", "Addition", 2)
            .option ('s', "subtract", "Subtraction", 2)
            .option ('m', "multiply", "Multiplication", 2)
            .option ('d', "divide", "Division", 2)
            .option ('q', "square", "Square A", 1)
            ;
        
        if (args.length == 0)
            options.print_usage(System.out);
        else
        {
            try { options.parse(args); }
            catch (InvalidOptionException e)
            {
                die (e.getMessage());
            }

            if (options.isset("help"))
                options.print_usage();
            else
            {
                example calc = new example ();
                
                int result = 0;
                if (options.isset('a'))
                    result = calc.a(options.get('a', 0),
                                    options.get('a', 1));
                else if (options.isset('s'))
                    result = calc.s(options.get('s', 0),
                                    options.get('s', 1));
                else if (options.isset('m'))
                    result = calc.m(options.get('m', 0),
                                    options.get('m', 1));
                else if (options.isset('d'))
                    result = calc.d(options.get('d', 0),
                                    options.get('d', 1));
                else if (options.isset('q'))
                    result = calc.q(options.get('q', 0));
                else
                {
                    die ("Invalid option given");
                }
                System.out.println (result);
            }
        }
    }
}
