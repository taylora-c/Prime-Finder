import java.util.*;
import java.io.PrintWriter;
import java.io.IOException;

public class prime_Finder   
{
    //variables
    public static final int MAX = 100000000;  //10^8 100000000
    public  final static int nThreads = 8; //number of threads
    public static long nPrimes = 0; //number of primes after calculation
    public static long sumPrimes = 0; //sum of all primes found
    public static Queue<Integer> maxQ = new LinkedList<Integer>();  //holds top 10 values of primes
    


    //arrays

    /*
        primes[] holds the boolean value of each number
            true = prime
            false = not prime
        tStatus[] holds the status of each thread
            true = running
            false = not running
    */
    public boolean primes[] = new boolean[MAX]; //
    public boolean tStatus[] = new boolean[nThreads];

    //Threads
    private List<prime_Runner> primeRunner;
    private List<Thread> primeThreads;

    //Lock
    public final static Object lock = new Object();

    public static void main(String args[]) throws IOException
    {
        //open output file
        PrintWriter out = new PrintWriter("primes.txt");

        prime_Finder mainThread = new prime_Finder();

        //set all values of prime[] to false
        for(int i = 0; i < MAX; i++){
            mainThread.primes[i] = false;
        }

        // start the clock
        long startTime = System.currentTimeMillis();

        // Create threads
        mainThread.primeRunner = new ArrayList<>();
        mainThread.primeThreads = new ArrayList<>();
        for(int i = 0; i < nThreads; i++){
            prime_Runner primeRunner = new prime_Runner(mainThread, i, MAX);
            mainThread.primeRunner.add(primeRunner);
            Thread t = new Thread(primeRunner);
            mainThread.primeThreads.add(t);
        }

         
        

        //start prime threads
        for (Thread i : mainThread.primeThreads)
        {
            i.start();
        }

        
        //mark 2 and 3 as true in primes[]
        synchronized(lock)
        {
            
            mainThread.primes[2] = true;
            mainThread.primes[3] = true;
        }
       

        //wait for threads to finish
        for (Thread i : mainThread.primeThreads)
        {
            try
            {
                i.join();
            }
            catch(Exception e) 
            {
               // System.out.println("[Exception]: " + e);
            }
        }
        //end time
        long endTime = System.currentTimeMillis();

        //end all threads
        for (Thread i : mainThread.primeThreads)
        {
            i.interrupt();
        }

        long duration = endTime - startTime;

        //print all info
        mainThread.getPrimes();

        out.println("runtime: " + duration +"ms");
        out.println("# of primes: " + nPrimes );
        out.println("sum of primes: " + sumPrimes );
        out.println("top 10 primes: ");
        while(!maxQ.isEmpty()){
            out.println(maxQ.remove());
        }

        out.close();
    }
   
    //gets sum, num of primes, and the top 10 primes
    public void getPrimes(){
        for(int i = 0; i < MAX; i++){
            if(primes[i] == true){
                nPrimes++;
                sumPrimes += i;
                maxQ.add(i);
                if(maxQ.size() > 10){
                    maxQ.remove(); 
                }

            }
        }
    }

}

class prime_Runner implements Runnable
{
    //lock
    private Object lock = new Object();
    //class variables
    private prime_Finder mainThread;

    private int tID;
    private int max;

    //thread constructor
    public prime_Runner(prime_Finder thread, int tID, int max)
    {
        this.mainThread = thread;
        this.tID = tID;
        this.max = max;
    }
    
    @Override
    public void run()
    {
        try
        {
            //find primes
            isPrime(mainThread, tID, max);
        }
        catch (Exception e)
        {
           // System.out.println("[Exception]: " + e);
        }
    }

    /*Seive of Atkin approach
        sources for algoithm:
            https://medium.com/smucs/primes-of-atkin-the-theoretical-optimization-of-prime-number-generation-e47107d61e28
    */
    public void isPrime(prime_Finder mainThread, int tID, int max)
    {   
        // Mark primes[n] is true if one of the following is true:
        for (int x = 1 + tID; x * x < max; x += mainThread.nThreads)
        {
            for (int y = 1; y * y < max; y++)
            {
                // n = (4*x*x)+(y*y) has odd number of solutions, i.e., there exists
                // an odd number of distinct pairs (x,y) that satisfy the equation and
                // n % 12 = 1 or n % 12 = 5.
                int n = (4 * x * x) + (y * y);
                if (n <= max && (n % 12 == 1 || n % 12 == 5))
                {
                    synchronized(lock)
                    {
                        this.mainThread.primes[n] ^= true;     
                    }
                }

                // n = (3*x*x)+(y*y) has odd number of solutions and n % 12 = 7.
                n = (3 * x * x) + (y * y);
                if (n <= max && n % 12 == 7)
                {
                    synchronized(lock)
                    {
                        this.mainThread.primes[n] ^= true;
                    }
                }

                // n = (3*x*x)-(y*y) has odd number of solutions, x > y and n % 12 = 11
                n = (3 * x * x) - (y * y);
                if (x > y && n <= max && n % 12 == 11)
                {
                    synchronized(lock)
                    {
                        this.mainThread.primes[n] ^= true;
                    }
                    
                }
            }
        }

        // Mark this thread as done with the first section.
        mainThread.tStatus[tID] = true;
        
        // Wait for all threads to finish first section.
        boolean alltStatus = false;
        while(!alltStatus)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {

            }
            alltStatus = true;
            for (int i = 0; i < mainThread.nThreads; i++)
            {
                if (mainThread.tStatus[i] == false)
                {
                    alltStatus = false;
                    break;
                }
            }
        }

        // Mark all multiples of squares as non-prime
        for (int r = 5 + tID; r * r < max; r += mainThread.nThreads)
        {
            synchronized(lock)
            {
                if (this.mainThread.primes[r])
                {
                    for (int i = r * r; i < max; i += r * r)
                    {
                        if (this.mainThread.primes[i] == true)
                        {
                            this.mainThread.primes[i] = false;
                        }
                    }
                }
            }

        }
    }
}
