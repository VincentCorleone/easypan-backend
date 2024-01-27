package love.vincentcorleone.easypan.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@Service
public class AsyncTasks {

    @Async
    public void union(String toPath, String fromPathDir, Integer chunks){
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(toPath, "rw");

            byte[] buffer = new byte[1024 * 10];

            for (int i = 0; i < chunks; i++) {
                int len = -1;
                RandomAccessFile from = null;
                try {
                    from = new RandomAccessFile(fromPathDir + "/" + i, "r");
                    while((len = from.read(buffer)) != -1){
                        raf.write(buffer,0,len);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("文件访问失败");
                } catch (IOException e) {
                    throw new RuntimeException("合并文件失败");
                } finally {
                    from.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("合并文件失败");
        }

    }
}
