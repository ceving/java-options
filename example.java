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
    
    static <T> void println (T... args)
    {
        for (T arg : args)
            System.out.println (arg);
    }

    public static void main (String[] args)
    {
        Options options = new Options()
            .about  ("Calc version 1.0")
            .usage  ("calc OPTION A [B]")
            .option ("help", "Display usage.")
            .option ('a', "add", "Addition: A + B", 2)
            .option ('s', "subtract", "Subtraction: A - B", 2)
            .option ('m', "multiply", "Multiplication: A * B", 2)
            .option ('d', "divide", "Division: A / B", 2)
            .option ('q', "square", "Square: A^2", 1)
            .option ("eight", "8 arguments.", 8)
            .option ("nine", "9 arguments.", 9)
            .option ("ordinals", "Three ordinals.",
                     "1st", "2nd", "3rd")
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
                
                if (options.isset('a'))
                    println (calc.a(options.get('a', 0),
                                    options.get('a', 1)));
                else if (options.isset('s'))
                    println (calc.s(options.get('s', 0),
                                    options.get('s', 1)));
                else if (options.isset('m'))
                    println (calc.m(options.get('m', 0),
                                    options.get('m', 1)));
                else if (options.isset('d'))
                    println (calc.d(options.get('d', 0),
                                    options.get('d', 1)));
                else if (options.isset('q'))
                    println (calc.q(options.get('q')));
                else if (options.isset("eight"))
                    println ("eight");
                else if (options.isset("nine"))
                    println ("nine");
                else if (options.isset("ordinals"))
                    println (options.get ("ordinals", 0),
                             options.get ("ordinals", 1),
                             options.get ("ordinals", 2));
                else
                    die ("Invalid option given");
            }
        }
    }
}
