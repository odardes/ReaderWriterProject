package pack;
import java.util.concurrent.Semaphore;


///Main class
public class ReaderWriter{
		   public static final int NUM_OF_READERS = 3;
		   public static final int NUM_OF_WRITERS = 2;

		    public static void main(String args[]){
		 	   ReadWriteLock rwlock = new ReadWriteLock();
		   
		      Thread[] readerArray = new Thread[NUM_OF_READERS];
		      Thread[] writerArray = new Thread[NUM_OF_WRITERS];
		   
		      for (int i = 0; i < NUM_OF_READERS; i++) {
		         readerArray[i] = new Thread(new Reader(rwlock));
		         readerArray[i].start();
		      }
		   
		      for (int i = 0; i < NUM_OF_WRITERS; i++) {
		         writerArray[i] = new Thread(new Writer(rwlock));
		         writerArray[i].start();
		      }
		   }
	 
}

//Writer class
class Writer implements Runnable 
{
	private static int writers = 0;
   private ReadWriteLock rwlock;
   private int writerNum;

    public Writer(ReadWriteLock rw) {
      this.rwlock = rw;
      this.writerNum=Writer.writers++;
   }

 
	public void run() {
      while (true){
    	  final int DELAY = 5000;
          try
          {
            Thread.sleep((int) (Math.random() * DELAY));
          }
          catch (InterruptedException e) {	  
          }
         System.out.println("writer " + writerNum + " wants to write.");
         rwlock.WriteLock(writerNum);
         try
         {
           Thread.sleep((int) (Math.random() * DELAY));
         }
         catch (InterruptedException e) {
         }
         rwlock.WriteUnLock(writerNum);
      }
   }
}

//Reader class
class Reader implements Runnable
{

	private static int readers = 0;
   private ReadWriteLock rwlock;
   private int readerNum;

    public Reader(ReadWriteLock rwlock) {
      this.rwlock = rwlock;
      this.readerNum=Reader.readers++;
   }

	public void run() {
      while (true) {
    	  final int DELAY = 5000;
          try
          {
            Thread.sleep((int) (Math.random() * DELAY));
          }
          catch (InterruptedException e) { 
          }
         System.out.println("reader " + readerNum + " wants to read.");
         rwlock.ReadLock(readerNum);
         
         try
         {
           Thread.sleep((int) (Math.random() * DELAY));
         }
         catch (InterruptedException e) {
         }
         rwlock.ReadUnLock(readerNum);
      }
   };
}

//Lock class
class ReadWriteLock {
    private int readerCount;  // the number of active readers
    private Semaphore mutex;  // controls access to readerCount
    private Semaphore rwlock; // controls access to the database
 
     public ReadWriteLock() {
       readerCount = 0;
       mutex = new Semaphore(1);
       rwlock = new Semaphore(1);
    }
 
     public void ReadLock(int readerNum) {
       try{
       //mutual exclusion for readerCount 
          mutex.acquire();
       }
       catch (InterruptedException e) {
       }
       ++readerCount;
    
    // if I am the first reader tell all others
    // that the database is being read
       if (readerCount == 1){
          try{
        	  rwlock.acquire();
          }
          catch (InterruptedException e) {  
          }
       }
       System.out.println("Reader " + readerNum + " is reading. Reader count = " + readerCount);
       //mutual exclusion for readerCount
       mutex.release();
    }
 
     public void ReadUnLock(int readerNum) {
       try{
       //mutual exclusion for readerCount
          mutex.acquire();
       }
           catch (InterruptedException e) {}
    
       --readerCount;
    
    // if I am the last reader tell all others
    // that the database is no longer being read
       if (readerCount == 0){
    	   rwlock.release();
       }
    
       System.out.println("Reader " + readerNum + " is done reading. Reader count = " + readerCount);
    
       mutex.release();
    }
 
     public void WriteLock(int writerNum) {
       try{
    	   rwlock.acquire();
       }
           catch (InterruptedException e) {}
       System.out.println("Writer " + writerNum + " is writing.");
    }
 
     public void WriteUnLock(int writerNum) {
       System.out.println("Writer " + writerNum + " is done writing.");
       rwlock.release();
    }
 }
