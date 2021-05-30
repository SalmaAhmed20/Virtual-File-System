package VFS;

import java.lang.reflect.Array;

public class Contiguous {
    private final Directory root = new Directory ();
    private final int DiskSize = 30; //KB
    private String B = "000000000000000000000000000000"; //intial value
    char [] Blocks = this.B.toCharArray();
    private int FreeBlocks = DiskSize;
//---------------------Free Space Management function----------------------------------
    int Allocate (int FSize) {

        if ( FSize > FreeBlocks ) {
            System.out.println ("No space ");
            //Can't allocate
            return - 1;
        }
        //because of worst fit we search to biggest block in size
        //if we found the biggest block
        int worestSize = 0;
        int IdxofWorest = - 1;
        boolean flag = false;
        int startIdx = - 1;
        int count = 0;
        for (int i = 0 ; i < Blocks.length ; i++) {
            if ( Blocks[i]== ('0') ) {
                if ( ! flag ) {
                    //System.out.println (startIdx);
                    flag = true;
                    startIdx = i;
                }
                count++;
            }
            else
            {
                if(count >= FSize && count >= worestSize)
                {
                    IdxofWorest =startIdx;
                    worestSize =count;
                }
                count=0;
               flag =false;
            }
        }
        if (count >= FSize && count >= worestSize)
            IdxofWorest =startIdx;
        //allocate space
        System.out.println ("Before Allocate " + String.valueOf (Blocks));
        for (int i = IdxofWorest ; i <IdxofWorest+FSize  ; i++) {
            if(IdxofWorest == -1) {
                System.out.println ("No Contiguous space");
                return - 1;
            }
            else {
                Array.setChar (Blocks,i,'1');
                this.FreeBlocks--;
            }
        }
        System.out.println ("After Allocate " + String.valueOf (Blocks));
        return IdxofWorest;
    }
    boolean deallocateSpace (int startidx,int Fsize)
    {
        System.out.println ("Before Allocate " + String.valueOf (Blocks));
        for (int i = startidx ; i < startidx+Fsize  ; i++) {
            if ( Blocks[i] == ('1') ) {
                Array.setChar (Blocks,i,'0');
                this.FreeBlocks++;
            }
            else {
                System.out.println ("Error");
                return false;
            }
        }
        System.out.println ("After Allocate " + String.valueOf (Blocks));
        return true;
    }
    //Main
    public static void main (String[] args) {
        Contiguous ctgs = new Contiguous ();
        ctgs.Allocate (25);
        System.out.println ("second");
        ctgs.Allocate (6);
        ctgs.deallocateSpace (6,2);
        ctgs.Allocate (6);
    }

}
