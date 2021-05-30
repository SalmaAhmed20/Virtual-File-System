package VFS;

public class File {
        private String filePath;
        private  int Size;
        private String name;
        private int[] allocatedBlocks;
        private boolean deleted;
        public int getSize ( ) {
                return Size;
        }

        public void setSize (int size) {
                Size = size;
        }
        public String getFilePath ( ) {
                return filePath;
        }

        public void setFilePath (String filePath) {
                this.filePath = filePath;
        }
        public int[] getAllocatedBlocks ( ) {
                return allocatedBlocks;
        }

        public void setAllocatedBlocks (int[] allocatedBlocks) {
                this.allocatedBlocks = allocatedBlocks;
        }


        public boolean isDeleted ( ) {
                return deleted;
        }

        public void setDeleted (boolean deleted) {
                this.deleted = deleted;
        }


        public String getName ( ) {
                return name;
        }

        public void setName (String name) {
                this.name = name;
        }
}
