# Prime-Finder
to compile: javac prime_Finder.java
to run: java prime_Finder

To find primes, I used the Seive of Atkin approach. The class 'prime_Finder' creates and starts the threads. The prime_Runner class holds each thread as an object. The run function calls the isPrime function for each thread. Each thread is classified by a int thread ID. Each thread starts at the index 1 + ID and is incremented by ID at each iteration. This guaruntees work is divided evenly. 

To ensure mutual exclusion, i used an Object Lock. This ensured that multiple threads could be in the same method and be locked in blocks of thread safe code. Since the Sieve of Adkins algorithm requires multiple searches during each iteration. Having multiple threads in the same method was essensial to ensure a efficient parallel process.


Finally, you need to provide a brief summary of your approach and an informal statement reasoning about the correctness and efficiency of your design. Provide a summary of the experimental evaluation of your approach.

sources:
prime finding method: 
    https://www.geeksforgeeks.org/sieve-of-atkin/
    https://medium.com/smucs/sieve-of-atkin-the-theoretical-optimization-of-prime-number-generation-e47107d61e28
locks:
    https://howtodoinjava.com/java/multi-threading/object-vs-class-level-locking/