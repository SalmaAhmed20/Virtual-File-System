package VFS;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class Indexed {
    private Directory root = new Directory ();
    private int DiskSize = 30; //KB
    private String B = "000000000000000000000000000000"; //intial value
    char [] Blocks = this.B.toCharArray();
    private int FreeBlocks = DiskSize;
    private int l = 0;

    Vector<Integer> Allocate (int FSize) {
        Vector<Integer> allocated=new Vector<Integer>();
        if ( FSize > FreeBlocks ) {
            System.out.println ("No space ");
            //Can't allocate
            return null;
        }
        int  index_block=-1;
        int flag=0;//to check if found index_block or not


        System.out.println ("Before Allocate " + String.valueOf (Blocks));

        Random rand = new Random();
        index_block = rand.nextInt(30);
        // to get index block
        if ((Blocks[index_block] =='1')&&(flag==0)) {
            for (int i = index_block; i < FreeBlocks; i++) {
                if (Blocks[i] == '0') {
                    flag = 1;
                    index_block = i;
                    break;
                }
            }
        }
        if(flag==0) {
            for (int i=index_block-1;i>=0;i--) {
                if (Blocks[i] =='0') {
                    flag = 1;
                    index_block = i;
                    allocated.add(i);
                    break;
                }
            }
        }
        else {
            allocated.add(index_block);
        }
        Array.setChar (Blocks,index_block,'1');
        this.FreeBlocks--;

        // to get indexes

        for(int i=0;i<FSize;i++)
        {

            flag=0;
            int indexes = rand.nextInt(30);

            for (int x=indexes;x<DiskSize;x++) {
                if (Blocks[x] == '0') {
                    flag = 1;
                    indexes = x;
                    allocated.add(x);
                    Array.setChar (Blocks,x,'1');
                    this.FreeBlocks--;
                    break;
                }
            }
            if(flag==0) {
                for (int x=indexes-1;x>=0;x--) {
                    if (Blocks[x] =='0') {
                        flag = 1;
                        indexes = x;
                        allocated.add(x);
                        Array.setChar (Blocks,x,'1');
                        this.FreeBlocks--;
                        break;
                    }
                }
            }
        }

        System.out.println ("After Allocate  " + String.valueOf (Blocks));
        return  allocated;
    }

    boolean deallocateSpace (Vector<Integer> indexes)
    {
        System.out.println ("Before deAllocate " + String.valueOf (Blocks));
        for(int i=0;i<indexes.size();i++){
            if ( indexes.get(i)!= null ) {
                Array.setChar (Blocks,indexes.get(i),'0');
                this.FreeBlocks++;
            }
            else {
                System.out.println ("Error");
                return false;
            }
        }
        System.out.println ("After deAllocate  " + String.valueOf (Blocks));
        return true;
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

    //Utility functions private
    //for create file
    Directory DirExist (Directory Dir,String[] folders,int start,int num) {
        if ( folders.length == 2 && Dir != null ) {


            //System.out.println (Dir.getName ());
            if ( folders[ 0 ].equals (Dir.getName ()) )
                return Dir;
            return null;
        }
        assert Dir != null;
        for (Directory dir : Dir.getSubDirectories ()) {
            if ( dir != null ) {
                if ( folders[ start ].equals (dir.getName ()) ) {
                    if ( start == num && folders[ start ].equals (dir.getName ()) )
                        return dir;
                }
                return DirExist (dir,folders,start + 1,num);
            }
        }
        return null;
    }
    //for create folder
    Directory DirExist2 (Directory Dir,String[] folders,int start,int num) {
        if(folders.length == 2 && Dir != null) {
            return Dir;
        }
        assert Dir != null;
        for (Directory dir : Dir.getSubDirectories ()) {
            if(dir != null) {
                if(folders[ start ].equals (dir.getName ())) {
                    if(start == num && folders[ start ].equals (dir.getName ()))
                        return dir;
                    return dir;
                }
                return DirExist2 (dir,folders,start + 1,num);
            }
        }
        return null;
    }

    void deleteContent (Directory ob) {
        int i = 0, count = 0;
        //System.out.println (ob.getName ());
        int num = ob.getSubDirectories ().length;
        while (true) {
            count++;
            Directory folder = ob.getSubDirectories ()[ i ];
            File[] filles = ob.getFiles ();
            if(filles != null) {
                for (File fille : filles) {
                    this.DeleteFile (fille.getFilePath ());
                }
            }
            if(folder != null) {
                this.deleteContent (folder);
                i++;
            } else if(count == num)
                break;
        }

    }
    void DeleteFile (String path) {
        int idx;
        String[] Folder = path.split ("/");
        File F = new File ();
        F.setName (Folder[ Folder.length - 1 ]);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);
        if(dir != null) {
            idx = this.Existfile (F.getName (),dir.getFiles ());
            if(idx == - 1) {
                System.out.println ("File not exist ");
            } else {
                File files = dir.getFiles ()[ idx ];
                F.setDeleted (true);
                System.out.println ("File " + files.getName () + " is deleted");
                if(dir.getFiles () == null || idx >= dir.getFiles ().length) {
                    dir.setFiles (dir.getFiles ());
                }
                File[] anotherArray = new File[ dir.getFiles ().length - 1 ];
                System.arraycopy (dir.getFiles (),0,anotherArray,0,idx);
                System.arraycopy (dir.getFiles (),idx + 1,anotherArray,idx,dir.getFiles ().length - idx - 1);
                dir.setFiles (anotherArray);
                if(! this.deallocateSpace (files.getAllocatedBlocksINDX ())) System.out.println ("Error");

            }
        } else
            System.out.println ("Path doesn't exist");
    }

    void DeleteFolder (String path) {

        String[] Folder = path.split ("/");
        File F = new File ();
        F.setName (Folder[ Folder.length - 1 ]);
        F.setFilePath (path);
        if(F.getName ().equals ("root")) {
            System.out.println ("Can't delete root");
            return;
        }
        if(Folder.length < 2) {
            System.out.println ("Not vaild path");
            return;
        }
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);
        if(dir != null) {
            int flag = - 1;
            String Fname = Folder[ Folder.length - 1 ];
            if(dir.getSubDirectories () == null) {
                flag = - 1;
            } else if(dir.getSubDirectories () != null) {

                assert dir.getSubDirectories ()!=null;
                for (int i = 0 ; i < dir.getSubDirectories ().length  ; i++) {
                    if(dir.getSubDirectories ()[i]!=null)
                        if(dir.getSubDirectories ()[ i ].getName ().equals (Fname) ) {
                            flag = i;

                        }
                    //  System.out.println (dir.getSubDirectories ()[ i ].getName ());
                }
                if(flag != - 1) {
                    Directory TobeDelete = dir.getSubDirectories ()[ flag ];
                    this.deleteContent (TobeDelete);
                    dir.getSubDirectories ()[ flag ].setDeleted (true);
                    ArrayList <Directory> D = new ArrayList <Directory> (Arrays.asList (dir.getSubDirectories ()));
                    D.remove (flag);
                    dir.setSubDirectories (D.toArray (new Directory[ 0 ]));

                    System.out.println ("Directory " + Fname + "  is deleted Successfully");
                    System.out.println ("Remaining folders :");
                    for (Directory Dd : dir.getSubDirectories ()) {
                        if(Dd != null)
                            System.out.println (Dd.getName ());
                    }
                } else {
                    System.out.println ("Directory NOT exist");
                }
            } else
                System.out.println ("Path doesn't exist");
        }
    }
    void retrive (String path,Vector<Integer> allowcate) {
        //   Vector<Integer> allocated=new Vector<Integer>();
        String[] Folder = path.split ("/");
        //Arrays.copyOf (Folder,Folder.length - 1);
        File F = new File ();
        F.setName (Folder[ Folder.length - 1 ]);
        F.setSize (allowcate.size()-1);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);

        if ( dir != null ) // if the file doesn't exist  then you can add it
        {

            if ( this.Existfile (F.getName (),dir.getFiles ()) == - 1 ) //doesn't exist
            {

                if (  allowcate == null ) {
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

                    F.setAllocatedBlocksINDX( allowcate);
                    System.out.println ("Index Block Is   " + F.getAllocatedBlocksINDX ().get(0) );
                    for(int i=1;i< F.getAllocatedBlocksINDX ().size();i++)
                    {
                        System.out.print ("  " + F.getAllocatedBlocksINDX ().get(i) );
                    }
                    System.out.println ("\nFile " + F.getName () + " Created successfully");
                }

            } else
                System.out.println ("File Already exist");
        } else
            System.out.println ("Path doesn't exist");
    }

    //commands function
    void CreateFile (String path,int Size) {
        Vector<Integer> allocated=new Vector<Integer>();
        String[] Folder = path.split ("/");
        //Arrays.copyOf (Folder,Folder.length - 1);
        File F = new File ();
        F.setName (Folder[ Folder.length - 1 ]);
        F.setSize (Size);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);

        if ( dir != null ) // if the file doesn't exist  then you can add it
        {

            if ( this.Existfile (F.getName (),dir.getFiles ()) == - 1 ) //doesn't exist
            {
                allocated = Allocate (Size);
                if ( allocated == null ) {
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

                    F.setAllocatedBlocksINDX(allocated);
                    System.out.println ("Index Block Is   " + F.getAllocatedBlocksINDX ().get(0) );
                    for(int i=1;i< F.getAllocatedBlocksINDX ().size();i++)
                    {
                        System.out.print ("  " + F.getAllocatedBlocksINDX ().get(i) );
                    }
                    System.out.println ("\nFile " + F.getName () + " Created successfully");
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
        dir = this.DirExist2 (root,Folder,1,Folder.length - 2);
        if(dir != null) {
            if(dir.getSubDirectories () == null)
                flag = - 1;
            else if(dir.getSubDirectories () != null) {
                for (int i = 0 ; i < dir.getSubDirectories ().length && dir.getSubDirectories ()[ i ] != null ; i++) {
                    if(dir.getSubDirectories ()[ i ].getName ().equals (Fname))
                        flag = i;
                }
            }
            if(flag == - 1) {
                if(dir.getSubDirectories () != null) {
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
            } else {
                System.out.println ("Directory Already exist");
                return false;
            }
        } else {
            System.out.println ("Path doesn't exist");
            return false;
        }
    }

    void DisplayDiskStatus()
    {
        ArrayList<Integer> empty  =new ArrayList <> ();
        ArrayList<Integer> Alloc  =new ArrayList <> ();
        System.out.println ("Total Empty space : " + this.FreeBlocks +" KB");
        System.out.println ("Total Allocated Space : " + (this.DiskSize - this.FreeBlocks)+" KB");
        for (int i = 0 ; i < Blocks.length ; i++) {
            if (Blocks[i] == '0')
                empty.add (i);
            else
                Alloc.add (i);
        }
        System.out.print ("Allocated blocks are : ");
        for (Integer n : Alloc) {
            System.out.print(n+" ");
        }
        System.out.print ("\nEmpty blocks are : ");
        for (Integer n : empty) {
            System.out.print(n+" ");
        }
        System.out.println ("\n");
    }

    void DisplayDiskStructure(Directory dir) {
        int level = l;
        Directory d = dir;
        if (level == 0) {
            d = root;
            System.out.println("< " + d.getName() + " > ");
            if (d.getFiles() != null) {
                ArrayList<File> files = new ArrayList<>(Arrays.asList(d.getFiles()));
                for (File f : files) {
                    System.out.print("       ");
                    System.out.println(f.getName());
                }
            }
            ArrayList<Directory> D = new ArrayList<>(Arrays.asList(d.getSubDirectories()));
            l++;
            for (Directory dir1 : D) {
                if (dir1 != null) {
                    this.DisplayDiskStructure(dir1);
                }
            }
        } else {
            if (d != null) {
                System.out.print("       ");
                System.out.println("< " + d.getName() + " > ");
                if (d.getFiles() != null) {
                    ArrayList<File> files = new ArrayList<>(Arrays.asList(d.getFiles()));
                    System.out.print("       ");
                    for (File f : files) {
                        System.out.print("       ");
                        System.out.println(f.getName());
                    }
                }
                ArrayList<Directory> D = new ArrayList<>(Arrays.asList(d.getSubDirectories()));
                l++;
                for (Directory dir1 : D) {
                    if (dir1 != null) {
                        System.out.print("       ");
                        this.DisplayDiskStructure(dir1);
                    }

                }
            }
        }
    }

    void storeToVFS () {
        try{
            FileWriter fWriter = new FileWriter ("Indexed.vfs");
            fWriter.write (String.valueOf (Blocks) + "-");
            fWriter.write (DiskSize + "-");
            fWriter.write (String.valueOf (this.FreeBlocks) );
            this.SaveRec (root,fWriter);
            fWriter.close ();
        }catch (IOException e){
            e.printStackTrace ();
        }

    }
    void SaveRec (Directory dir,FileWriter f) {
        try{
            if(dir != null) {
                f.append ("-Dir||" + dir.getDirectoryPath ());
                if(dir.getSubDirectories () == null && dir.getFiles () == null) return;
                else {
                    if(dir.getFiles () != null) {

                        ArrayList <File> files = new ArrayList <> (Arrays.asList (dir.getFiles ()));
                        for (File fl : files){
                            f.append ("-Fl||" + fl.getFilePath ()
                                    + "||" + fl.getAllocatedBlocksINDX ().get(0) );
                            for(int i=1;i<fl.getAllocatedBlocksINDX ().size();i++)
                            {
                                f.append( "||" + fl.getAllocatedBlocksINDX ().get(i));
                            }

                        }
                    }
                    if(dir.getSubDirectories () != null) {
                        ArrayList <Directory> D = new ArrayList <> (Arrays.asList (dir.getSubDirectories ()));
                        for (Directory dir1 : D){
                            SaveRec (dir1,f);
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace ();
        }
    }
    void  loadFromFile()
    {
        try{
            FileReader fR= new FileReader("Indexed.vfs");
            BufferedReader br = new BufferedReader (fR);


            if ( br.readLine() != null) {
                String str=br.readLine ();
                if(str!=null){
                    String [] st =str.split ("-");
                    this.Blocks=st[0].toCharArray ();
                    this.DiskSize=Integer.parseInt(st[1]);
                    this.FreeBlocks=Integer.parseInt(st[2]);
                    for (int i = 3 ; i < st.length ; i++){
                        String[] currentline = st[i].split ("||");
                        if(currentline[0].equals ("Dir")) {
                            if(currentline[1].equals ("root/"))
                                continue;
                            this.CreateFolder (currentline[1]);
                        }else if(currentline[0].equals ("Fl")) {
                            Vector<Integer> v=new Vector<Integer>();
                            for(int x=i+1;x<st.length;x++)
                            {
                                v.add(Integer.parseInt(currentline[x]));
                            }
                            retrive(currentline[1],v);

                        }
                    }

                }
            }
        }catch (IOException fileNotFoundException){
            fileNotFoundException.printStackTrace ();
        }
    }




    public static void main (String[] args) {
        Indexed Indx = new  Indexed ();
        Indx.loadFromFile ();
        Indx.CreateFile ("root/file.txt",10);
        Indx.CreateFile ("root/file.txt",10);
        System.out.println ("-----------------------------");
        Indx.CreateFile ("root/file2.txt",8);
        System.out.println ("--------------------------------");
        Indx.CreateFolder ("root/Folder");
        System.out.println ("--------------------------------");
        Indx.CreateFile ("root/Folder/test.txt",5);
        Indx.CreateFile ("root/Folderr/test.txt",5);
        System.out.println ("--------------------------------");
        Indx.CreateFolder ("root/Folder/Folder2");
        System.out.println ("--------------------------------");
        Indx.CreateFolder ("root/Folder3");
        System.out.println ("--------------------------------");
        Indx.DisplayDiskStatus ();
        System.out.println ("--------------------------------");
        Indx.DisplayDiskStructure (Indx.root);
        System.out.println ("-----------in---------------------");
        Indx.DeleteFile ("root/file2.txt");
        System.out.println ("------------in--------------------");
        Indx.DisplayDiskStatus ();
        Indx.DisplayDiskStructure (Indx.root);
        Indx.DeleteFolder ("root/Folder");
        System.out.println ("--------------------------------");
        Indx.DisplayDiskStatus ();
        Indx.DisplayDiskStructure (Indx.root);
        Indx.storeToVFS ();

    }
}