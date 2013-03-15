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

class example
{
    void die () { System.exit (1); }
    void die (String... message)
    {
        for (String msg : message) System.err.println (msg);
        die();
    }

    Integer x = null;
    Integer y = null;

    example (String[] args)
    {
        if (args.length > 0)
            x = new Integer(args[0]);
        else
            die ("Argument missing");
        if (args.length > 1)
            y = new Integer(args[1]);
    }

    int a () { return x + y; }
    int s () { return x - y; }
    int m () { return x * y; }
    int d () { return x / y; }
    int q () { return x * x; }

    public static void main (String[] args)
    {
        Options options = new Options()
            .about  ("Calc version 1.0")
            .usage  ("calc <option> x [y]")
            .option (null, "help", "Display usage.")
            .option ('a', "add", "Addition")
            .option ('s', "subtract", "Subtraction")
            .option ('m', "multiply", "Multiplication")
            .option ('d', "divide", "Division")
            .option ('q', "square", "Square A", (String)null)
            ;
        

        if (args.length == 0)
            options.print_usage(System.out);
        else
        {
            try { args = options.parse(args); }
            catch (InvalidOptionException e)
            {
                System.err.println (e.getMessage());
                System.exit (1);
            }

            if (options.isset("help"))
                options.print_usage(System.out);
            else
            {
                example calc = new example (args);

                if (options.isset('a'))
                    System.out.println (calc.a());
                else if (options.isset('s'))
                    System.out.println (calc.s());
                else if (options.isset('m'))
                    System.out.println (calc.m());
                else if (options.isset('d'))
                    System.out.println (calc.d());
                
                else
                {
                    System.err.println ("Invalid option given");
                    System.exit (1);
                }
            }
        }
    }
}
