package com.android.tumb.Misc;

import android.content.Context;
import com.android.tumb.Data.PostSerializable;
import com.android.tumb.Data.PostWrapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trust on 8/28/2016.
 * Class to read posts from cache
 */
public class CacheStorage {
    private Context context;


    public CacheStorage (Context context){
        this.context = context;
    }

    public List<PostWrapper> readFromCache(int start, int end){
        List<PostWrapper> outList = new ArrayList<>();

        String dirPath = context.getFilesDir().getAbsolutePath() + File.separator + "cache";
        File projDir = new File(dirPath);
        if (projDir.exists()) {


            if (projDir.listFiles() != null) {
                for (File f : projDir.listFiles()) {
                    if (f.isFile() & f.getName().startsWith("postFile")) {
                        String name = f.getName();
                        int pos = Integer.valueOf(name.split("_")[1].replaceAll("\\.\\w*", ""));
                        if ((pos >= start) && (pos < end)) {
                            try {
                                ObjectInputStream input;
                                input = new ObjectInputStream(new FileInputStream(f));
                                PostSerializable thisPostSerializable = (PostSerializable) input.readObject();
                                PostWrapper thisPostWrapper = new PostWrapper(thisPostSerializable);
                                outList.add(thisPostWrapper);
                                input.close();
                            } catch (StreamCorruptedException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }

        return  outList;
    }

}
