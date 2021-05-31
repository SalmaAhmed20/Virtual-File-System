package VFS;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Contiguous {
    private final Directory root = new Directory ();
    private int DiskSize = 30; //KB
    private final String B = "000000000000000000000000000000"; //initial value
    char[] Blocks = this.B.toCharArray ();
    private int FreeBlocks = DiskSize;
    private int l = 0;

    //---------------------Free Space Management function----------------------------------
    int Allocate (int FSize) {

        if(FSize > FreeBlocks) {
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
        for (int i = 0 ; i < Blocks.length ; i++){
            if(Blocks[i] == ('0')) {
                if(! flag) {
                    //System.out.println (startIdx);
                    flag = true;
                    startIdx = i;
                }
                count++;
            }else {
                if(count >= FSize && count >= worestSize) {
                    IdxofWorest = startIdx;
                    worestSize = count;
                }
                count = 0;
                flag = false;
            }
        }
        if(count >= FSize && count >= worestSize)
            IdxofWorest = startIdx;
        //allocate space
        System.out.println ("Before Allocate " + String.valueOf (Blocks));
        for (int i = IdxofWorest ; i < IdxofWorest + FSize ; i++){
            if(IdxofWorest == - 1) {
                System.out.println ("No Contiguous space");
                return - 1;
            }else {
                Array.setChar (Blocks,i,'1');
                this.FreeBlocks--;
            }
        }
        System.out.println ("After Allocate  " + String.valueOf (Blocks));
        return IdxofWorest;
    }

    boolean deallocateSpace (int startidx,int Fsize) {
        System.out.println ("Before Allocate " + String.valueOf (Blocks));
        for (int i = startidx ; i < startidx + Fsize ; i++){
            if(Blocks[i] == ('1')) {
                Array.setChar (Blocks,i,'0');
                this.FreeBlocks++;
            }else {
                System.out.println ("Error");
                return false;
            }
        }
        System.out.println ("After Allocate  " + String.valueOf (Blocks));
        return true;
    }

    //Utility functions private
    //for create file
    Directory DirExist (Directory Dir,String[] folders,int start,int num) {
        if(folders.length == 2 && Dir != null) {


            //System.out.println (Dir.getName ());
            if(folders[ 0 ].equals (Dir.getName ()))
                return Dir;
            return null;
        }
        assert Dir != null;
        for (Directory dir : Dir.getSubDirectories ()) {
            if(dir != null) {
                if(folders[ start ].equals (dir.getName ())) {
                    if(start == num && folders[ start ].equals (dir.getName ()))
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
        for (Directory dir : Dir.getSubDirectories ()){
            if(dir != null) {
                if(folders[start].equals (dir.getName ())) {
                    if(start == num && folders[start].equals (dir.getName ()))
                        return dir;
                    return dir;
                }
                return DirExist2 (dir,folders,start + 1,num);
            }
        }
        return null;
    }

    int Existfile (String nameOfFile,File[] files) {
        if(files == null)
            return - 1;
        for (int i = 0 ; i < files.length ; i++){
            if(files[i].getName ().equals (nameOfFile))
                return i;
        }
        return - 1;
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
    void Retrive (String path,int startidx,int end) {
        int start;
        String[] Folder = path.split ("/");
        //Arrays.copyOf (Folder,Folder.length - 1);
        File F = new File ();
        F.setName (Folder[Folder.length - 1]);
        F.setSize (end-startidx);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);

        if(dir != null) // if the file doesn't exist  then you can add it
        {

            if(this.Existfile (F.getName (),dir.getFiles ()) == - 1) //doesn't exist
            {
                start = Allocate (end-startidx);
                if(start == - 1) {
                    return;
                }else {
                    if(dir.getFiles () != null) {
                        File[] New = new File[dir.getFiles ().length + 1];
                        for (int i = 0 ; i < dir.getFiles ().length ; i++){
                            New[i] = dir.getFiles ()[i];
                        }
                        New[dir.getFiles ().length] = F;
                        dir.setFiles (New);
                    }else {
                        File[] New = new File[1];
                        New[0] = F;
                        dir.setFiles (New);
                    }

                    F.getAllocatedBlocks ()[0][0] = startidx;
                    F.getAllocatedBlocks ()[0][1] = end;
                    System.out.println ("Allocated space is from   " + F.getAllocatedBlocks ()[0][0] + " to " + F.getAllocatedBlocks ()[0][1]);
                    System.out.println ("File " + F.getName () + " Created successfully");
                }

            }else
                System.out.println ("File Already exist");
        }else
            System.out.println ("Path doesn't exist");
    }

    //commands function
    void CreateFile (String path,int Size) {
        int start;
        String[] Folder = path.split ("/");
        //Arrays.copyOf (Folder,Folder.length - 1);
        File F = new File ();
        F.setName (Folder[Folder.length - 1]);
        F.setSize (Size);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);

        if(dir != null) // if the file doesn't exist  then you can add it
        {

            if(this.Existfile (F.getName (),dir.getFiles ()) == - 1) //doesn't exist
            {
                start = Allocate (Size);
                if(start == - 1) {
                    return;
                }else {
                    if(dir.getFiles () != null) {
                        File[] New = new File[dir.getFiles ().length + 1];
                        for (int i = 0 ; i < dir.getFiles ().length ; i++){
                            New[i] = dir.getFiles ()[i];
                        }
                        New[dir.getFiles ().length] = F;
                        dir.setFiles (New);
                    }else {
                        File[] New = new File[1];
                        New[0] = F;
                        dir.setFiles (New);
                    }

                    F.getAllocatedBlocks ()[0][0] = start;
                    F.getAllocatedBlocks ()[0][1] = Size + start;
                    System.out.println ("Allocated space is from   " + F.getAllocatedBlocks ()[0][0] + " to " + F.getAllocatedBlocks ()[0][1]);
                    System.out.println ("File " + F.getName () + " Created successfully");
                }

            }else
                System.out.println ("File Already exist");
        }else
            System.out.println ("Path doesn't exist");
    }

    boolean CreateFolder (String path) {
        int flag = - 1;
        String[] Folder = path.split ("/");
        String Fname = Folder[Folder.length - 1];
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
                for (int i = 0 ; i < dir.getSubDirectories ().length && dir.getSubDirectories ()[i] != null ; i++){
                    if(dir.getSubDirectories ()[i].getName ().equals (Fname))
                        flag = i;
                }
            }
            if(flag == - 1) {
                if(dir.getSubDirectories () != null) {
                    Directory[] New = new Directory[dir.getSubDirectories ().length + 1];
                    for (int i = 0 ; i < dir.getSubDirectories ().length ; i++){
                        New[i] = dir.getSubDirectories ()[i];
                    }
                    New[dir.getSubDirectories ().length] = newDir;
                    dir.setSubDirectories (New);
                }else {
                    Directory[] New = new Directory[1];
                    New[0] = newDir;
                    dir.setSubDirectories (New);
                }
                System.out.println ("Directory " + newDir.getName () + " Created successfully");
                return true;
            }else {
                System.out.println ("Directory Already exist");
                return false;
            }
        }else {
            System.out.println ("Path doesn't exist");
            return false;
        }
    }

    void DeleteFile (String path) {
        int idx;
        String[] Folder = path.split ("/");
        File F = new File ();
        F.setName (Folder[Folder.length - 1]);
        F.setFilePath (path);
        Directory dir = this.DirExist (root,Folder,1,Folder.length - 2);
        if(dir != null) {
            idx = this.Existfile (F.getName (),dir.getFiles ());
            if(idx == - 1) {
                System.out.println ("File not exist ");
            }else {
                File files = dir.getFiles ()[idx];
                F.setDeleted (true);
                System.out.println ("File " + files.getName () + " is deleted");
                if(dir.getFiles () == null || idx >= dir.getFiles ().length) {
                    dir.setFiles (dir.getFiles ());
                }
                File[] anotherArray = new File[dir.getFiles ().length - 1];
                System.arraycopy (dir.getFiles (),0,anotherArray,0,idx);
                System.arraycopy (dir.getFiles (),idx + 1,anotherArray,idx,dir.getFiles ().length - idx - 1);
                dir.setFiles (anotherArray);
                int Start = files.getAllocatedBlocks ()[0][0];
                int size = Math.abs (files.getAllocatedBlocks ()[0][1] - files.getAllocatedBlocks ()[0][0]);
                if(! this.deallocateSpace (Start,size)) System.out.println ("Error");

            }
        }else
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

    void DisplayDiskStatus () {
        ArrayList <Integer> empty = new ArrayList <> ();
        ArrayList <Integer> Alloc = new ArrayList <> ();
        System.out.println ("Total Empty space : " + this.FreeBlocks + " KB");
        System.out.println ("Total Allocated Space : " + (this.DiskSize - this.FreeBlocks) + " KB");
        for (int i = 0 ; i < Blocks.length ; i++){
            if(Blocks[i] == '0')
                empty.add (i);
            else
                Alloc.add (i);
        }
        System.out.print ("Allocated blocks are : ");
        for (Integer n : Alloc){
            System.out.print (n + " ");
        }
        System.out.print ("\nEmpty blocks are : ");
        for (Integer n : empty){
            System.out.print (n + " ");
        }
        System.out.println ("\n");
    }

    void DisplayDiskStructure (Directory dir) {
        int level = l;
        Directory d = dir;
        if(level == 0) {
            d = root;
            System.out.println ("< " + d.getName () + " > ");
            if(d.getFiles () != null) {
                ArrayList <File> files = new ArrayList <> (Arrays.asList (d.getFiles ()));
                for (File f : files){
                    System.out.println (f.getName ());
                }
            }
            ArrayList <Directory> D = new ArrayList <> (Arrays.asList (d.getSubDirectories ()));
            l++;
            for (Directory dir1 : D){
                if(dir1 != null)
                    this.DisplayDiskStructure (dir1);
            }
        }else {
            if(d != null) {
                System.out.println ("< " + d.getName () + " > ");
                if(d.getFiles () != null) {
                    ArrayList <File> files = new ArrayList <> (Arrays.asList (d.getFiles ()));
                    for (File f : files){
                        System.out.println (f.getName ());
                    }
                }
                ArrayList <Directory> D = new ArrayList <> (Arrays.asList (d.getSubDirectories ()));
                l++;
                for (Directory dir1 : D){
                    if(dir1 != null)
                        this.DisplayDiskStructure (dir1);
                }
            }
        }
    }

    //files .vfs save and load
    void storeToVFS () {
        try{
            FileWriter fWriter = new FileWriter ("Contiguous.vfs");
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
                                    + "||" + fl.getAllocatedBlocks ()[0][0] + "||" + fl.getAllocatedBlocks ()[0][1]);
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
            FileReader fR= new FileReader("Contiguous.vfs");
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
                        this.Retrive (currentline[1],Integer.parseInt (currentline[2]),Integer.parseInt (currentline[3]));
                    }
                }

            }
            }
        }catch (IOException fileNotFoundException){
            fileNotFoundException.printStackTrace ();
        }
    }


    //Main
    public static void main (String[] args) {
        Contiguous ctgs = new Contiguous ();
        ctgs.loadFromFile ();
        ctgs.CreateFile ("root/file.txt",10);
        ctgs.CreateFile ("root/file.txt",10);
        System.out.println ("-----------------------------");
        ctgs.CreateFile ("root/file2.txt",8);
        System.out.println ("--------------------------------");
        ctgs.CreateFolder ("root/Folder");
        System.out.println ("--------------------------------");
        ctgs.CreateFile ("root/Folder/test.txt",5);
        ctgs.CreateFile ("root/Folderr/test.txt",5);
        System.out.println ("--------------------------------");
        ctgs.CreateFolder ("root/Folder/Folder2");
        System.out.println ("--------------------------------");
        ctgs.CreateFolder ("root/Folder3");
        System.out.println ("--------------------------------");
        ctgs.DisplayDiskStatus ();
        System.out.println ("--------------------------------");
        ctgs.DisplayDiskStructure (ctgs.root);
        System.out.println ("-----------in---------------------");
        ctgs.DeleteFile ("root/file2.txt");
        System.out.println ("------------in--------------------");
        ctgs.DisplayDiskStatus ();
        ctgs.DisplayDiskStructure (ctgs.root);
        ctgs.DeleteFolder ("root/Folder");
        System.out.println ("--------------------------------");
        ctgs.DisplayDiskStatus ();
        ctgs.DisplayDiskStructure (ctgs.root);
        ctgs.storeToVFS ();
        /*System.out.println ("\n\n----------------Example on----------------");
        Directory Dir = new Directory();
        Dir.setDirectoryPath("root/Folder");
        Dir.setName("Folder");
        Directory[] sub = new Directory[2];
        sub[0] = Dir;
        ctgs.CreateFile("root/Folder/hi.txt", 2);
        ctgs.CreateFolder("root/Folder/fold");
        ctgs.DeleteFolder ("root/Folder");
        ctgs.DisplayDiskStatus ();
        ctgs.DisplayDiskStructure(ctgs.root);*/
       /* Directory Dir = new Directory();

        ctgs.CreateFile("root/file.txt", 10);
        ctgs.CreateFolder("root/Folder");
        ctgs.CreateFolder("root/Folde3");
        ctgs.CreateFolder("root/Folder/fold");
        ctgs.CreateFile("root/Folder/file.txt", 2);
        //ctgs.DeleteFile ("root/Folder/file.txt");
        ctgs.DisplayDiskStatus();
        ctgs.DeleteFolder ("root/Folder");
        //ctgs.DisplayDiskStatus ();
        ctgs.CreateFile("root/Folder/Folde2/file2.txt", 2);
        ctgs.DisplayDiskStructure(ctgs.root);
*/



    }

}
