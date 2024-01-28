package unipi.mircv;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomByteReader {

        public RandomAccessFile randomAccessFile;
        public FileInputStream fileInputStream;
        public BufferedInputStream bufferedInputStream;
        public Compressor compressor;

        public RandomByteReader(String file, Compressor compressor){
            this.compressor = compressor;
            try{
                this.randomAccessFile = new RandomAccessFile(file, "r");
                this.fileInputStream = new FileInputStream(this.randomAccessFile.getFD());
                this.bufferedInputStream = new BufferedInputStream(this.fileInputStream);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public int read() {
            return compressor.readBytes(bufferedInputStream);
        }

        public void close() {
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public Object getReader() {
            return bufferedInputStream;
        }

        public void goToOffset(int offset){
            try{
                randomAccessFile.seek(offset);
                fileInputStream = new FileInputStream(randomAccessFile.getFD());
                bufferedInputStream = new BufferedInputStream(fileInputStream);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }