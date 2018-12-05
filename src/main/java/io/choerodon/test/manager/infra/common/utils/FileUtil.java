package io.choerodon.test.manager.infra.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.choerodon.core.exception.CommonException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by zongw.lee@gmail.com on 2018/11/21.
 */
public class FileUtil {
    private static final int BUFFER_SIZE = 2048;
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    private FileUtil() {
    }

    /**
     * json转yaml
     *
     * @param jsonValue json字符串
     * @return
     */
    public static String jsonToYaml(String jsonValue) {
        JsonNode jsonNodeTree = null;
        String json = "";
        try {
            jsonNodeTree = new ObjectMapper().readTree(jsonValue);
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        try {
            json = new YAMLMapper().writeValueAsString(jsonNodeTree);
        } catch (JsonProcessingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return json;
    }

    /**
     * 解压tgz包为字节数组
     */
    public static List<byte[]> unTarGzToMemory(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GzipCompressorInputStream gzInputStream = new GzipCompressorInputStream(inputStream);
             TarArchiveInputStream tarInputStream = new TarArchiveInputStream(gzInputStream, BUFFER_SIZE)) {
            List<byte[]> contents = new ArrayList<>();
            TarArchiveEntry entry;
            int len;
            byte[] buf = new byte[2048];
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                if (!entry.isFile()) {
                    continue;
                }
                outputStream.reset();
                while ((len = tarInputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                contents.add(outputStream.toByteArray());
            }
            return contents;
        }
    }

    /**
     * 解压tgz包
     */
    public static void unTarGZ(String file, String destDir) {
        File tarFile = new File(file);
        unTarGZ(tarFile, destDir);
    }

    /**
     * 解压tgz包
     */
    public static void unTarGZ(File tarFile, String destDir) {
        if (StringUtils.isBlank(destDir)) {
            destDir = tarFile.getParent();
        }
        destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
        try {
            unTar(new GzipCompressorInputStream(new FileInputStream(tarFile)), destDir);
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
    }

    private static void unTar(InputStream inputStream, String destDir) {

        TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream, BUFFER_SIZE);
        TarArchiveEntry entry = null;
        try {
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    createDirectory(destDir, entry.getName());
                } else {
                    File tmpFile = new File(destDir + File.separator + entry.getName());
                    createDirectory(tmpFile.getParent() + File.separator, null);
                    try (OutputStream out = new FileOutputStream(tmpFile)) {
                        int length = 0;
                        byte[] b = new byte[2048];
                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        } finally {
            IOUtils.closeQuietly(tarIn);
        }
    }

    /**
     * 创建目录
     */
    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        if (!(subDir == null || subDir.trim().equals(""))) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 获取文件总行数
     *
     * @param file 目标文件
     * @return 文件函数
     */
    public static int getFileTotalLine(String file) {
        Integer totalLine = 0;
        try (ByteArrayInputStream byteArrayInputStream =
                     new ByteArrayInputStream(file.getBytes(Charset.forName("utf8")))) {
            try (InputStreamReader inputStreamReader =
                         new InputStreamReader(byteArrayInputStream, Charset.forName("utf8"))) {
                try (BufferedReader br = new BufferedReader(inputStreamReader)) {
                    String lineTxt;
                    while ((lineTxt = br.readLine()) != null) {
                        totalLine = totalLine + 1;
                    }
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return totalLine;
    }

    /**
     * format yaml
     *
     * @param value json value
     * @return yaml
     */
    public static String checkValueFormat(String value) {
        try {
            if (value.equals("")) {
                return "{}";
            }
            JSONObject.parseObject(value);
            value = FileUtil.jsonToYaml(value);
            return value;
        } catch (Exception ignored) {
            return value;
        }
    }

    /**
     * yaml format
     *
     * @param yaml yaml value
     */
    public static void checkYamlFormat(String yaml) {
        try {
            Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), new Resolver());
            composer.getSingleNode();
        } catch (Exception e) {
            throw new CommonException(e.getMessage(), e);
        }
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile zip
     * @param descDir 目标文件位置
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir) {
        File pathFile = new File(descDir);
        pathFile.mkdirs();
        try (ZipFile zip = new ZipFile(zipFile)) {
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                getUnZipPath(zip, entry, zipEntryName, descDir);
            }
        } catch (IOException e) {
            throw new CommonException("error.not.zip", e);
        }
        logger.info("******************解压完毕********************");
    }

    private static void getUnZipPath(ZipFile zip, ZipEntry entry, String zipEntryName, String descDir) {
        try (InputStream in = zip.getInputStream(entry)) {

            String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            file.mkdirs();
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                return;
            }
            //输出文件路径信息
            logger.info(outPath);
            outPutUnZipFile(in, outPath);
        } catch (IOException e) {
            throw new CommonException("error.zip.inputStream", e);
        }
    }

    private static void outPutUnZipFile(InputStream in, String outPath) {
        try (OutputStream out = new FileOutputStream(outPath)) {
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
        } catch (FileNotFoundException e) {
            throw new CommonException("error.outPath", e);
        } catch (IOException e) {
            throw new CommonException("error.zip.outPutStream", e);
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean keepDirStructure) {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            isFile(sourceFile, zos, name, buf);

        } else {
            compressHandle(sourceFile, zos, name, keepDirStructure);
        }
    }

    private static void compressHandle(File sourceFile, ZipOutputStream zos, String name,
                                       boolean keepDirStructure) {
        File[] listFiles = sourceFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            // 需要保留原来的文件结构时,需要对空文件夹进行处理
            if (keepDirStructure) {
                // 空文件夹的处理
                try {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new CommonException(e.getMessage(), e);
                }
            }

        } else {
            // 判断是否需要保留原来的文件结构
            Arrays.stream(listFiles).forEachOrdered(file ->
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, zos,
                            keepDirStructure
                                    ? name + "/" + file.getName()
                                    : file.getName(), keepDirStructure)
            );
        }
    }

    private static void isFile(File sourceFile, ZipOutputStream zos, String name, byte[] buf) {
        // copy文件到zip输出流中
        int len;
        try (FileInputStream in = new FileInputStream(sourceFile)) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
        } catch (IOException e) {
            throw new CommonException(e.getMessage(), e);
        }
    }

}
