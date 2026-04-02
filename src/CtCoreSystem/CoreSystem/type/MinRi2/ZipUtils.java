package CtCoreSystem.CoreSystem.type.MinRi2;

import arc.files.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.io.*;

import java.io.*;
import java.util.zip.*;

/**
 * @author minri2
 * Create by 2024/7/19
 */
public class ZipUtils{
    private static final ObjectMap<String, byte[]> tempMap = new ObjectMap<>();

    /**
     * 将文件添加到压缩包中
     * @param zipFi 压缩包文件
     * @param files 写入的文件 可为文件夹
     * @param basePath 写入文件截取掉的路径 用于在压缩包中保留指定的文件结构
     */
    public static void writeAll(Fi zipFi, Seq<Fi> files, String basePath){
        writeAll(zipFi, files, basePath, false);
    }

    /**
     * 将文件添加到压缩包中
     * @param zipFi 压缩包文件
     * @param files 写入的文件 可为文件夹
     * @param basePath 写入文件截取掉的路径 用于在压缩包中保留指定的文件结构
     */
    public static void writeAll(Fi zipFi, Seq<Fi> files, String basePath, boolean append){
        ObjectMap<String, byte[]> map = null;
        if(append && zipFi.exists()){
            map = readZip(zipFi);
        }

        try(ZipOutputStream zos = new ZipOutputStream(zipFi.write())){
            if(map != null){
                for(Entry<String, byte[]> entry : map){
                    zos.putNextEntry(new ZipEntry(entry.key));
                    zos.write(entry.value);
                }
            }

            writeAll(zos, files, basePath);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 将文件追加到压缩包中
     * @param zipFi 写入的压缩包
     * @param fi 文件可为文件夹
     */
    public static void write(Fi zipFi, Fi fi){
        write(zipFi, fi, null, false);
    }

    /**
     * 将文件追加到压缩包中
     * @param zipFi 写入的压缩包
     * @param fi 文件可为文件夹
     */
    public static void write(Fi zipFi, Fi fi, String zipPath, boolean append){
        ObjectMap<String, byte[]> map = null;
        if(append && zipFi.exists()){
            map = readZip(zipFi);
        }

        try(ZipOutputStream zos = new ZipOutputStream(zipFi.write())){
            if(map != null){
                for(Entry<String, byte[]> entry : map){
                    zos.putNextEntry(new ZipEntry(entry.key));
                    zos.write(entry.value);
                }
            }

            String path = fi.name();

            if(zipPath != null){
                if(!zipPath.endsWith("/")){
                    zipPath = zipPath + "/";
                }

                path = zipPath + path;
            }

            write(zos, fi, path);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private static void write(ZipOutputStream zos, Fi fi, String path){
        try{
            if(fi.isDirectory() && !path.endsWith("/")){
                path = path + "/";
            }

            path = path.startsWith("/") ? path.substring(1) : path;

            zos.putNextEntry(new ZipEntry(path));

            if(!fi.isDirectory()){
                try(InputStream stream = fi.read()){
                    Streams.copy(stream, zos);
                }
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private static void writeAll(ZipOutputStream zos, Seq<Fi> files, String basePath){
        for(Fi fi : files){
            String path = basePath == null ? fi.path() : fi.path().substring(basePath.length());
            write(zos, fi, path);
        }
    }

    private static ObjectMap<String, byte[]> readZip(Fi zipFi){
        tempMap.clear();

        try(ZipInputStream zis = new ZipInputStream(zipFi.read())){
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }

                tempMap.put(entry.getName(), baos.toByteArray());
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        return tempMap;
    }
}
