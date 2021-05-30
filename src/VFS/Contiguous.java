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
    //Utility functions private
    Directory DirExist (Directory Dir , String[] folders,int start,int num)
    {
        if (folders.length == 2 && Dir instanceof  Directory)
            return Dir;
        for (var dir : Dir.getSubDirectories ())
        {
            if (folders[start].equals (dir.getName ()) && Dir instanceof  Directory)
            {
                if (start == num && folders[start].equals (dir.getName ())  )
                    return dir;
                return DirExist (dir,folders,start+1,num);
            }
        }
        return null;
    }
    //commands function
    boolean CreateFile(String path, int Size)
    {
        String[] Folder = path.split ("/");
        File F = new File ();
        F.setName (Folder[Folder.length-1]);
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
        String[] Folder = "root/file.txt ".split ("/");
        for (int i = 0 ; i < Folder.length ; i++) {
            System.out.println (Folder[i]);
        }
        File F = new File ();
        F.setName (Folder[Folder.length-1]);
        System.out.println (F.getName ());
    }

}
