package com.zqi.frame.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.zqi.unit.FileUtil;

public class NIOFileUtil {

	public static void writeFile(String content,String file){
	    FileOutputStream fos = null;
	    FileChannel fc = null;
	    ByteBuffer buf = null;
	    FileUtil.mkParent( file );
		try {
			fos = new FileOutputStream(file);
			fc = fos.getChannel();
			buf = ByteBuffer.wrap(content.getBytes("utf-8"));
			byte[] message = content.getBytes("utf-8");
			buf.put(message);
			buf.flip();
			fc.write(buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if ( fc != null && fos != null ) {
                try {
                	fc.close();
                	fos.close();
                }
                catch ( Exception ex ) {

                }
            }
        }
	}
}
