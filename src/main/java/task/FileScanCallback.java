package task;

import java.io.File;

//文件扫面任务的回调方法
public interface FileScanCallback {
    void execute(File dir);
}
