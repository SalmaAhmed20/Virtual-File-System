package VFS;

public class Linked {
    private final Directory root = new Directory ();
    private final int DiskSize = 30; //KB
    private String B = "000000000000000000000000000000"; //intial value
    char [] Blocks = this.B.toCharArray();
    private int FreeBlocks = DiskSize;
}
