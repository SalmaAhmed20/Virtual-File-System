package VFS;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Contiguous {
    private final Directory root = new Directory ();
    private final int DiskSize = 30; //KB
    private String B = "000000000000000000000000000000"; //initial value
    char[] Blocks = this.B.toCharArray ();
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
            if ( Blocks[ i ] == ('0') ) {
                if ( ! flag ) {
                    //System.out.println (startIdx);
                    flag = true;
                    startIdx = i;
                }
                count++;
            } else {
                if ( count >= FSize && count >= worestSize ) {
                    IdxofWorest = startIdx;
                    worestSize = count;
                }
                count = 0;
                flag = false;
            }
        }
        if ( count >= FSize && count >= worestSize )
            IdxofWorest = startIdx;
        //allocate space
        System.out.println ("Before Allocate " + String.valueOf (Blocks));
        for (int i = IdxofWorest ; i < IdxofWorest + FSize ; i++) {
            if ( IdxofWorest == - 1 ) {
                System.out.println ("No Contiguous space");
                return - 1;
            } else {
                Array.setChar (Blocks,i,'1');
                this.FreeBlocks--;
            }
        }
        System.out.println ("After Allocate " + String.valueOf (Blocks));
        return IdxofWorest;
    }

    boolean deallocateSpace (int startidx,int Fsize) {
        System.out.println ("Before Allocate " + String.valueOf (Blocks));
        for (int i = startidx ; i < startidx + Fsize ; i++) {
            if ( Blocks[ i ] == ('1') ) {
                Array.setChar (Blocks,i,'0');
                this.FreeBlocks++;
            } else {
                System.out.println ("Error");
                return false;
            }
        }
        System.out.println ("After Allocate " + String.valueOf (Blocks));
        return true;
    }

    //Utility functions private
    //for create file
    Directory DirExist (Directory Dir,String[] folders,int start,int num) {
        if ( folders.length == 2 && Dir != null ) {


            //System.out.println (Dir.getName ());
            //if ( folders[ 0 ].equals (Dir.getName ()) )
                return Dir;
            //return null;
        }
        //System.out.println ("in for");
        assert Dir != null;
        for (Directory dir : Dir.getSubDirectories ()) {
            //System.out.println (folders[start]);
            if ( dir != null ) {
                if ( folders[ start ].equals (dir.getName ()) ) {
                    //System.out.println ( folders[ start ].equals (dir.getName ()));
                    if ( start == num && folders[ start ].equals (dir.getName ()) )
                        //System.out.println (folders[ start ].equals (dir.getName ()));
                        return dir;
                }
                return DirExist (dir,folders,start + 1,num);
            }
        }
        return null;
    }
    //for create folder
    Directory DirExist2 (Directory Dir,String[] folders,int start,int num) {
        if ( folders.length == 2 && Dir != null ) {
            return Dir;
        }
        assert Dir != null;
        for (Directory dir : Dir.getSubDirectories ()) {
            if ( dir != null ) {
                if ( folders[ start ].equals (dir.getName ()) ) {
                    if ( start == num && folders[ start ].equals (dir.getName ()) )
                        return dir;
                    return dir;
                }
                return DirExist2 (dir,folders,start + 1,num);
            }
        }
        return null;
    }

    int Existfile (String nameOfFile,File[] files) {
        if ( files == null )
            return - 1;
        for (int i = 0 ; i < files.length ; i++) {
            if ( files[ i ].getName ().equals (nameOfFile) )
                return i;
        }
        return - 1;
    }

    //commands function
    void CreateFile (String path,int Size) {
        int start;
        String[] Folder = path.split ("/");
        Arrays.copyOf (Folder,Folder.length - 1);
        File F = new File ();
        F.setName (Folder[ Folder.length - 1 ]);
        F.setSize (Size);
        F.setFilePath (path);
        //treverse file system as tree until reach the last directory before file
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);

        if ( dir != null ) // if the file doesn't exist  then you can add it
        {

            if ( this.Existfile (F.getName (),dir.getFiles ()) == - 1 ) //doesn't exist
            {
                start = Allocate (Size);
                if ( start == - 1 ) {
                    return;
                } else {
                    if ( dir.getFiles () != null ) {
                        File[] New = new File[ dir.getFiles ().length + 1 ];
                        for (int i = 0 ; i < dir.getFiles ().length ; i++) {
                            New[ i ] = dir.getFiles ()[ i ];
                        }
                        New[ dir.getFiles ().length ] = F;
                        dir.setFiles (New);
                    } else {
                        File[] New = new File[ 1 ];
                        New[ 0 ] = F;
                        dir.setFiles (New);
                    }
                    F.getAllocatedBlocks ()[ 0 ][ 0 ] = start;
                    F.getAllocatedBlocks ()[ 0 ][ 1 ] = Size + start;
                    System.out.println ("Allocated space is from   " + F.getAllocatedBlocks ()[ 0 ][ 0 ] + " to " + F.getAllocatedBlocks ()[ 0 ][ 1 ]);
                    System.out.println ("File " + F.getName () + " Created successfully");
                }

            } else
                System.out.println ("File Already exist");
        } else
            System.out.println ("Path doesn't exist");
    }

    boolean CreateFolder (String path) {
        int flag = - 1;
        String[] Folder = path.split ("/");
        String Fname = Folder[ Folder.length - 1 ];
        Directory newDir = new Directory ();
        newDir.setName (Fname);
        newDir.setDirectoryPath (path);

        new Directory ();
        Directory dir;
       dir= this.DirExist2 (root,Folder,1,Folder.length);
        //System.out.println (dir.getSubDirectories ().toString ());
        if ( dir != null ) {
            //System.out.println (dir.getName ());
            if ( dir.getSubDirectories () == null )
                flag = - 1;
            else if(dir.getSubDirectories ()!=null){
                for (int i = 0 ; i < dir.getSubDirectories ().length  &&dir.getSubDirectories ()[ i ]!=null ; i++) {
                    if ( dir.getSubDirectories ()[ i ].getName ().equals (Fname) )
                        flag = i;
                }
            }
            if ( flag == - 1 ) {
                if ( dir.getSubDirectories () != null ) {
                    Directory[] New = new Directory[ dir.getSubDirectories ().length + 1 ];
                    for (int i = 0 ; i < dir.getSubDirectories ().length ; i++) {
                        New[ i ] = dir.getSubDirectories ()[ i ];
                    }
                    New[ dir.getSubDirectories ().length ] = newDir;
                    dir.setSubDirectories (New);
                } else {
                    Directory[] New = new Directory[ 1 ];
                    New[ 0 ] = newDir;
                    dir.setSubDirectories (New);
                }
                System.out.println ("Directory " + newDir.getName () + " Created successfully");
                return true;
            }
            else {
                System.out.println ("Directory Already exist");
                return false;
            }
        } else {
            System.out.println ("Path doesn't exist");
            return false;
        }
    }
    void DeleteFile (String path)
    {
        String[] Folder = path.split ("/");
        String Fname = Folder[ Folder.length - 1 ];
        File F = new File ();
        F.setName (Folder[ Folder.length - 1 ]);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);


    }


    //Main
    public static void main (String[] args) {
        Contiguous ctgs = new Contiguous ();
        Directory Dir = new Directory ();
        Dir.setDirectoryPath ("root/Folder");
        Dir.setName ("Folder");
        Directory [] sub = new  Directory[2];
        sub[0]=Dir;
        ctgs.root.setSubDirectories (sub);
       ctgs.CreateFile ("root/file.txt",10);

       ctgs.CreateFolder ("root/Folder");
       ctgs.CreateFile ("root/Folder/Folder2/file.txt",2);

    }

}
